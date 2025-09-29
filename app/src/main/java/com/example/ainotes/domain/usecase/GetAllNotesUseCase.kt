package com.example.ainotes.domain.usecase

import com.example.ainotes.data.Note
import com.example.ainotes.data.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {
    operator fun invoke(): Flow<List<Note>> = notesRepository.getAllNotes()
}
