package com.example.ainotes.data.dao

import androidx.room.*
import com.example.ainotes.data.Note
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Note entity
 * Provides all CRUD operations for notes in the Room database
 */
@Dao
interface NoteDao {
    
    /**
     * Get all notes ordered by timestamp (most recent first)
     */
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>
    
    /**
     * Get a specific note by its ID
     */
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): Note?
    
    /**
     * Insert a new note or replace existing one
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)
    
    /**
     * Update an existing note
     */
    @Update
    suspend fun updateNote(note: Note)
    
    /**
     * Delete a note by its ID
     */
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: String)
    
    /**
     * Delete all notes
     */
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
    
    /**
     * Get starred notes only
     */
    @Query("SELECT * FROM notes WHERE isStarred = 1 ORDER BY timestamp DESC")
    fun getStarredNotes(): Flow<List<Note>>
    
    /**
     * Get AI summarized notes only
     */
    @Query("SELECT * FROM notes WHERE isAiSummarized = 1 ORDER BY timestamp DESC")
    fun getAiSummarizedNotes(): Flow<List<Note>>
}