package com.example.ainotes.domain.usecase

import com.example.ainotes.data.repository.NotesRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {
    suspend operator fun invoke(id: String) {
        notesRepository.deleteNote(id)
    }
}
