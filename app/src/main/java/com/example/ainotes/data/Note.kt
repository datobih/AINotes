package com.example.ainotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a note in the AI Notes app
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long, // Changed from String to Long for better Room compatibility
    val isStarred: Boolean = false,
    val isAiSummarized: Boolean = false
)