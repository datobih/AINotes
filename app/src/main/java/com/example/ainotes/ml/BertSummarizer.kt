package com.example.ainotes.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * MobileBERT TFLite Summarizer
 * Handles text summarization using a TensorFlow Lite model
 */
class BertSummarizer(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private var maxInputLength = 384 // Changed from 512 to 384 (1536 bytes / 4 bytes per int)
    private val modelFileName = "1.tflite"
    
    init {
        loadModel()
    }
    
    /**
     * Load the TFLite model from assets
     */
    private fun loadModel() {
        try {
            val modelBuffer = loadModelFile(context, modelFileName)
            val options = Interpreter.Options().apply {
                // Use 4 threads for inference
                setNumThreads(4)
                // Disable NNAPI - use CPU only for better compatibility
                setUseNNAPI(false)
            }
            interpreter = Interpreter(modelBuffer, options)
            
            // Get the actual input shape from the model
            val inputShape = interpreter?.getInputTensor(0)?.shape()
            if (inputShape != null && inputShape.size >= 2) {
                maxInputLength = inputShape[1] // Update max length based on model's expectation
                println("✅ MobileBERT model loaded successfully - Input shape: ${inputShape.contentToString()}, Max length: $maxInputLength")
            } else {
                println("✅ MobileBERT model loaded successfully")
            }
        } catch (e: Exception) {
            println("❌ Error loading model: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Load model file from assets as MappedByteBuffer
     */
    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    
    /**
     * Generate summary from input text
     * 
     * @param text The input text to summarize
     * @return The generated summary
     */
    fun summarize(text: String): Result<String> {
        if (interpreter == null) {
            return Result.failure(Exception("Model not initialized"))
        }
        
        if (text.isBlank()) {
            return Result.failure(Exception("Input text is empty"))
        }
        
        return try {
            // Preprocess text
            val processedText = preprocessText(text)
            
            // Tokenize input
            val inputTokens = tokenize(processedText)
            
            // Run inference
            val output = runInference(inputTokens)
            
            // Decode output to text
            val summary = decodeOutput(output, processedText)
            
            Result.success(summary)
            
        } catch (e: Exception) {
            println("❌ Summarization error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Preprocess text before tokenization
     */
    private fun preprocessText(text: String): String {
        return text
            .trim()
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
            .take(2000) // Limit to reasonable length for processing
    }
    
    /**
     * Simple word-based tokenization
     * Note: For production, you should use proper BERT tokenizer with vocabulary
     */
    private fun tokenize(text: String): IntArray {
        val tokens = IntArray(maxInputLength) { 0 }
        
        // Special tokens for BERT
        val CLS_TOKEN = 101  // [CLS] token
        val SEP_TOKEN = 102  // [SEP] token
        val PAD_TOKEN = 0    // [PAD] token
        
        tokens[0] = CLS_TOKEN
        
        // Split text into words
        val words = text.lowercase()
            .split(Regex("[\\s\\p{Punct}]+"))
            .filter { it.isNotEmpty() }
        
        // Simple hash-based token generation (replace with proper tokenizer)
        words.take(maxInputLength - 2).forEachIndexed { index, word ->
            // Generate token ID from word (placeholder for proper tokenizer)
            tokens[index + 1] = word.hashCode().let { hash ->
                (Math.abs(hash) % 28996) + 1000 // Keep in vocab range
            }
        }
        
        // Add separator token
        val lastIndex = minOf(words.size + 1, maxInputLength - 1)
        tokens[lastIndex] = SEP_TOKEN
        
        return tokens
    }
    
    /**
     * Run inference on the model
     */
    private fun runInference(inputTokens: IntArray): Array<FloatArray> {
        val interpreter = this.interpreter 
            ?: throw IllegalStateException("Interpreter not initialized")
        
        // Get input and output tensor information
        val inputDetails = interpreter.getInputTensor(0)
        val outputDetails = interpreter.getOutputTensor(0)
        
        val inputShape = inputDetails.shape()
        val outputShape = outputDetails.shape()
        
        println("📊 Input shape: ${inputShape.contentToString()}")
        println("📊 Output shape: ${outputShape.contentToString()}")
        
        // Calculate the actual input size needed (from model's input shape)
        val expectedInputSize = inputShape[1] // Should be 384
        
        // Ensure input tokens match expected size
        val adjustedInputTokens = if (inputTokens.size > expectedInputSize) {
            inputTokens.copyOf(expectedInputSize)
        } else {
            IntArray(expectedInputSize) { i ->
                if (i < inputTokens.size) inputTokens[i] else 0
            }
        }
        
        println("📊 Adjusted input size: ${adjustedInputTokens.size} (expected: $expectedInputSize)")
        
        // Create input buffer with correct size
        val inputBuffer = ByteBuffer.allocateDirect(adjustedInputTokens.size * 4).apply {
            order(ByteOrder.nativeOrder())
            adjustedInputTokens.forEach { putInt(it) }
            rewind()
        }
        
        // Create output buffer based on model output shape
        val outputSize = outputShape.reduce { acc, i -> acc * i }
        val outputBuffer = ByteBuffer.allocateDirect(outputSize * 4).apply {
            order(ByteOrder.nativeOrder())
        }
        
        // Run inference
        interpreter.run(inputBuffer, outputBuffer)
        
        // Convert output buffer to array
        outputBuffer.rewind()
        val batchSize = outputShape[0]
        val sequenceLength = if (outputShape.size > 1) outputShape[1] else 1
        val output = Array(batchSize) { FloatArray(sequenceLength) }
        
        for (i in 0 until batchSize) {
            for (j in 0 until sequenceLength) {
                output[i][j] = outputBuffer.float
            }
        }
        
        return output
    }
    
    /**
     * Decode model output to readable summary
     */
    private fun decodeOutput(output: Array<FloatArray>, originalText: String): String {
        // Extract important sentences based on scores
        val sentences = originalText.split(Regex("[.!?]+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        
        if (sentences.isEmpty()) {
            return "No content to summarize"
        }
        
        // Get scores from model output
        val scores = output.firstOrNull() ?: return originalText
        
        // Create sentence-score pairs
        val sentenceScores = sentences.take(scores.size).mapIndexed { index, sentence ->
            sentence to scores.getOrElse(index) { 0f }
        }
        
        // Sort by score and take top 3 sentences
        val topSentences = sentenceScores
            .sortedByDescending { it.second }
            .take(3)
            .map { it.first }
        
        // Return as bullet points
        return if (topSentences.isNotEmpty()) {
            topSentences.joinToString("\n") { "• $it" }
        } else {
            // Fallback: return first few sentences
            sentences.take(3).joinToString("\n") { "• $it" }
        }
    }
    
    /**
     * Check if model is loaded and ready
     */
    fun isReady(): Boolean = interpreter != null
    
    /**
     * Release resources when done
     */
    fun close() {
        interpreter?.close()
        interpreter = null
        println("🔒 MobileBERT resources released")
    }
}
