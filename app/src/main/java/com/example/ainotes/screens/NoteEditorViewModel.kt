package com.example.ainotes.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainotes.data.Note
import com.example.ainotes.domain.usecase.NotesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val notesUseCases: NotesUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    fun loadNote(noteId: String?) {
        if (noteId == null) {
            // Creating a new note - reset to empty state
            _uiState.value = NoteEditorUiState(
                isLoading = false,
                isNewNote = true
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val note = notesUseCases.getNoteById(noteId)
                if (note != null) {
                    _uiState.value = _uiState.value.copy(
                        noteId = note.id,
                        title = note.title,
                        content = note.content,
                        isLoading = false,
                        isNewNote = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Note not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load note: ${e.message}"
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
    }

    fun saveNote(onSaveComplete: (Boolean) -> Unit = {}) {
        val currentState = _uiState.value
        
        // Validate input
        if (currentState.title.isBlank() && currentState.content.isBlank()) {
            _uiState.value = currentState.copy(error = "Please enter a title or content")
            onSaveComplete(false)
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(isSaving = true, error = null)
                
                val note = if (currentState.isNewNote) {
                    // Create new note
                    Note(
                        id = UUID.randomUUID().toString(),
                        title = currentState.title.ifBlank { "Untitled" },
                        content = currentState.content,
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    // Update existing note
                    val existingNote = notesUseCases.getNoteById(currentState.noteId!!)
                    existingNote?.copy(
                        title = currentState.title.ifBlank { "Untitled" },
                        content = currentState.content,
                        timestamp = System.currentTimeMillis()
                    ) ?: throw IllegalStateException("Note not found for update")
                }

                if (currentState.isNewNote) {
                    notesUseCases.insertNote(note)
                } else {
                    notesUseCases.updateNote(note)
                }

                _uiState.value = currentState.copy(
                    isSaving = false,
                    noteId = note.id,
                    isNewNote = false
                )
                onSaveComplete(true)
                
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSaving = false,
                    error = "Failed to save note: ${e.message}"
                )
                onSaveComplete(false)
            }
        }
    }

    fun setError(message:String){
        _uiState.value = _uiState.value.copy(error = message)
    }



    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class NoteEditorUiState(
    val noteId: String? = null,
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isNewNote: Boolean = true,
    val error: String? = null
)