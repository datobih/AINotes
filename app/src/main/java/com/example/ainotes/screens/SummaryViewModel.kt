package com.example.ainotes.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainotes.domain.usecase.NotesUseCases
import com.example.ainotes.ml.BertSummarizer
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.summarization.Summarization
import com.google.mlkit.genai.summarization.SummarizationRequest
import com.google.mlkit.genai.summarization.Summarizer
import com.google.mlkit.genai.summarization.SummarizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    application: Application,
    private val notesUseCases: NotesUseCases
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    private var summarizer: Summarizer? = null
    private var bertSummarizer: BertSummarizer? = null
    
    // Flag to choose which model to use
    private var useBertModel = true // Set to true to use TFLite BERT, false for ML Kit

    init {
        // Initialize BertSummarizer (TFLite model)
        bertSummarizer = BertSummarizer(application)
        
        // Initialize ML Kit Summarizer as fallback
        val summarizerOptions = SummarizerOptions.builder(application)
            .setInputType(SummarizerOptions.InputType.ARTICLE)
            .setOutputType(SummarizerOptions.OutputType.THREE_BULLETS)
            .setLanguage(SummarizerOptions.Language.ENGLISH)
            .build()
        summarizer = Summarization.getClient(summarizerOptions)
    }

    fun generateSummary(noteId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // Get the note
                val note = notesUseCases.getNoteById(noteId)
                
                if (note == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Note not found"
                    )
                    return@launch
                }

                if (note.content.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Note content is empty. Nothing to summarize."
                    )
                    return@launch
                }
                
                // Choose summarization method based on flag
                if (useBertModel && bertSummarizer != null && bertSummarizer!!.isReady()) {
                    // Use TFLite BERT model
                    generateBertSummary(note.content)
                } else {
                    // Use ML Kit to generate summary
                    prepareAndStartSummarization(note.content)
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to generate summary: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Generate summary using TFLite BERT model
     */
    private suspend fun generateBertSummary(text: String) {
        withContext(Dispatchers.Default) {
            try {
                _uiState.value = _uiState.value.copy(
                    downloadStatus = "Using local BERT model..."
                )
                
                val result = bertSummarizer?.summarize(text)
                
                result?.onSuccess { summary ->
                    _uiState.value = _uiState.value.copy(
                        summary = summary,
                        isLoading = false,
                        error = null,
                        downloadStatus = null
                    )
                }?.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "BERT summarization failed: ${error.message}",
                        downloadStatus = null
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "BERT error: ${e.message}",
                    downloadStatus = null
                )
            }
        }
    }
    
    private suspend fun prepareAndStartSummarization(text: String) {
        try {
            val currentSummarizer = summarizer ?: run {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Summarizer not initialized"
                )
                return
            }

            // Check feature availability using blocking get() on IO dispatcher
            val featureStatus = withContext(Dispatchers.IO) {
                currentSummarizer.checkFeatureStatus().get()
            }

            when (featureStatus) {
                FeatureStatus.DOWNLOADABLE -> {
                    // Update UI to show downloading
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        downloadProgress = 0f,
                        downloadStatus = "Preparing to download AI model..."
                    )

                    // Download feature
                    currentSummarizer.downloadFeature(object : DownloadCallback {
                        override fun onDownloadStarted(bytesToDownload: Long) {
                            _uiState.value = _uiState.value.copy(
                                downloadStatus = "Downloading AI model (${bytesToDownload / 1024 / 1024}MB)..."
                            )
                        }

                        override fun onDownloadFailed(e: GenAiException) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Failed to download AI model: ${e.message}",
                                downloadStatus = null,
                                downloadProgress = null
                            )
                        }

                        override fun onDownloadProgress(totalBytesDownloaded: Long) {
                            // Update progress (optional, can show percentage if total size is known)
                            _uiState.value = _uiState.value.copy(
                                downloadStatus = "Downloading... ${totalBytesDownloaded / 1024 / 1024}MB"
                            )
                        }

                        override fun onDownloadCompleted() {
                            _uiState.value = _uiState.value.copy(
                                downloadStatus = "Download complete. Generating summary...",
                                downloadProgress = null
                            )
                            viewModelScope.launch {
                                startSummarizationRequest(text, currentSummarizer)
                            }
                        }
                    })
                }

                FeatureStatus.DOWNLOADING -> {
                    _uiState.value = _uiState.value.copy(
                        downloadStatus = "AI model is downloading..."
                    )
                    startSummarizationRequest(text, currentSummarizer)
                }

                FeatureStatus.AVAILABLE -> {
                    startSummarizationRequest(text, currentSummarizer)
                }

                FeatureStatus.UNAVAILABLE -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "AI summarization feature is not available on this device"
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Error preparing summarization: ${e.message}",
                downloadStatus = null,
                downloadProgress = null
            )
        }
    }

    private suspend fun startSummarizationRequest(text: String, summarizer: Summarizer) {
        try {
            // Create task request
            val summarizationRequest = SummarizationRequest.builder(text).build()

            // Get non-streaming response using blocking get() on IO dispatcher
            val result = withContext(Dispatchers.IO) {
                summarizer.runInference(summarizationRequest).get()
            }
            
            _uiState.value = _uiState.value.copy(
                summary = result.summary,
                isLoading = false,
                error = null,
                downloadStatus = null,
                downloadProgress = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Failed to generate summary: ${e.message}",
                downloadStatus = null,
                downloadProgress = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Release ML Kit resources
        summarizer?.close()
        summarizer = null
        
        // Release BERT resources
        bertSummarizer?.close()
        bertSummarizer = null
    }
}

data class SummaryUiState(
    val summary: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val downloadStatus: String? = null,
    val downloadProgress: Float? = null
)
