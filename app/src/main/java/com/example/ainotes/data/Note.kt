package com.example.ainotes.data

/**
 * Data class representing a note in the AI Notes app
 */
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: String,
    val isStarred: Boolean = false,
    val isAiSummarized: Boolean = false
)