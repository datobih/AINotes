package com.example.ainotes.data.repository

import com.example.ainotes.data.Note
import org.junit.Assert.*
import org.junit.Test

/**
 * Simple unit tests for Room integration
 * These tests verify basic Note entity functionality
 */
class NotesRepositoryTest {
    
    @Test
    fun `note entity creation works correctly`() {
        // Given
        val timestamp = System.currentTimeMillis()
        
        // When
        val note = Note(
            id = "test-id",
            title = "Test Note",
            content = "Test content",
            timestamp = timestamp,
            isStarred = true,
            isAiSummarized = false
        )
        
        // Then
        assertEquals("test-id", note.id)
        assertEquals("Test Note", note.title)
        assertEquals("Test content", note.content)
        assertEquals(timestamp, note.timestamp)
        assertTrue(note.isStarred)
        assertFalse(note.isAiSummarized)
    }
    
    @Test
    fun `note entity with default values works correctly`() {
        // Given
        val timestamp = System.currentTimeMillis()
        
        // When
        val note = Note(
            id = "test-id-2",
            title = "Default Note",
            content = "Default content",
            timestamp = timestamp
        )
        
        // Then
        assertEquals("test-id-2", note.id)
        assertEquals("Default Note", note.title)
        assertEquals("Default content", note.content)
        assertEquals(timestamp, note.timestamp)
        assertFalse(note.isStarred) // default value
        assertFalse(note.isAiSummarized) // default value
    }
}