package com.example.ainotes.data.repository

import com.example.ainotes.data.Note
import com.example.ainotes.data.dao.NoteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NotesRepository {
    
    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes()
    }
    
    override suspend fun getNoteById(id: String): Note? {
        return noteDao.getNoteById(id)
    }
    
    override suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }
    
    override suspend fun deleteNote(id: String) {
        noteDao.deleteNote(id)
    }
    
    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }
}