package com.example.ainotes.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.ainotes.data.Note
import com.example.ainotes.data.dao.NoteDao

/**
 * Room database for AINotes app
 * Contains the database configuration and serves as the main access point
 */
@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {
    
    /**
     * Provides access to the NoteDao
     */
    abstract fun noteDao(): NoteDao
    
    companion object {
        const val DATABASE_NAME = "notes_database"
    }
}