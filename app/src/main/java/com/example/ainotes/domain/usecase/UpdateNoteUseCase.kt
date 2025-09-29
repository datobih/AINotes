package com.example.ainotes.domain.usecase

import com.example.ainotes.data.Note
import com.example.ainotes.data.repository.NotesRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {
    suspend operator fun invoke(note: Note) {
        notesRepository.updateNote(note)
    }
}
