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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notesUseCases: NotesUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            notesUseCases.getAllNotes().collect { notes ->
                _uiState.value = _uiState.value.copy(
                    notes = notes,
                    isLoading = false
                )
            }
        }
    }

    fun filterNotes(filterTab: FilterTab) {
        _uiState.value = _uiState.value.copy(selectedTab = filterTab)
    }

    fun updateSearchText(searchText: String) {
        _uiState.value = _uiState.value.copy(searchText = searchText)
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            notesUseCases.deleteNote(noteId)
        }
    }
}

data class HomeUiState(
    val notes: List<Note> = emptyList(),
    val selectedTab: FilterTab = FilterTab.ALL,
    val searchText: String = "",
    val isLoading: Boolean = true
)