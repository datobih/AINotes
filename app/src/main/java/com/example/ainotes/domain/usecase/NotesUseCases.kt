package com.example.ainotes.domain.usecase

import javax.inject.Inject

data class NotesUseCases @Inject constructor(
    val getAllNotes: GetAllNotesUseCase,
    val getNoteById: GetNoteByIdUseCase,
    val insertNote: InsertNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val deleteNote: DeleteNoteUseCase
)
