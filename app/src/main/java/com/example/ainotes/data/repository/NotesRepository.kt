package com.example.ainotes.data.repository

import com.example.ainotes.screens.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: String): Note?
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(id: String)
    suspend fun updateNote(note: Note)
}