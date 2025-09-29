package com.example.ainotes.domain.usecase

import com.example.ainotes.data.Note
import com.example.ainotes.data.repository.NotesRepository
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {
    suspend operator fun invoke(id: String): Note? = notesRepository.getNoteById(id)
}
