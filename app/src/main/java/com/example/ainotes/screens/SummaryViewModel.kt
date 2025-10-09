package com.example.ainotes.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainotes.domain.usecase.NotesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val notesUseCases: NotesUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

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
                
                // TODO: Replace with actual AI API call
                // For now, create a simple mock summary
                val summary = generateMockSummary(note.content)
                
                _uiState.value = _uiState.value.copy(
                    summary = summary,
                    isLoading = false,
                    error = null
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to generate summary: ${e.message}"
                )
            }
        }
    }
    
    private fun generateMockSummary(content: String): String {
        // Simple mock summarization logic
        // TODO: Replace with actual AI API integration
        val words = content.split(" ")
        return if (words.size > 50) {
            words.take(50).joinToString(" ") + "..."
        } else {
            content
        }
    }
}

data class SummaryUiState(
    val summary: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
