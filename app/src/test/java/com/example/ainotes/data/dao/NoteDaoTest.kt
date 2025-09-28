package com.example.ainotes.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ainotes.data.Note
import com.example.ainotes.data.database.NotesDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Comprehensive tests for NoteDao using in-memory Room database
 * Tests all CRUD operations and edge cases
 */
@RunWith(RobolectricTestRunner::class)
class NoteDaoTest {
    
    private lateinit var database: NotesDatabase
    private lateinit var noteDao: NoteDao
    
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NotesDatabase::class.java
        ).allowMainThreadQueries().build()
        
        noteDao = database.noteDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun `insertNote and getNoteById should work correctly`() = runTest {
        // Given
        val note = Note(
            id = "test-1",
            title = "Test Note",
            content = "Test content",
            timestamp = System.currentTimeMillis(),
            isStarred = false,
            isAiSummarized = false
        )
        
        // When
        noteDao.insertNote(note)
        val retrievedNote = noteDao.getNoteById("test-1")
        
        // Then
        assertNotNull(retrievedNote)
        assertEquals(note.id, retrievedNote?.id)
        assertEquals(note.title, retrievedNote?.title)
        assertEquals(note.content, retrievedNote?.content)
        assertEquals(note.timestamp, retrievedNote?.timestamp)
        assertEquals(note.isStarred, retrievedNote?.isStarred)
        assertEquals(note.isAiSummarized, retrievedNote?.isAiSummarized)
    }
    
    @Test
    fun `getAllNotes should return empty list initially`() = runTest {
        // When
        val notes = noteDao.getAllNotes().first()
        
        // Then
        assertTrue(notes.isEmpty())
    }
    
    @Test
    fun `getAllNotes should return notes ordered by timestamp desc`() = runTest {
        // Given
        val currentTime = System.currentTimeMillis()
        val note1 = Note("1", "First", "Content 1", currentTime - 1000, false, false)
        val note2 = Note("2", "Second", "Content 2", currentTime, false, false)
        val note3 = Note("3", "Third", "Content 3", currentTime - 2000, false, false)
        
        // When
        noteDao.insertNote(note1)
        noteDao.insertNote(note2)
        noteDao.insertNote(note3)
        
        val notes = noteDao.getAllNotes().first()
        
        // Then
        assertEquals(3, notes.size)
        assertEquals("2", notes[0].id) // Most recent
        assertEquals("1", notes[1].id) // Middle
        assertEquals("3", notes[2].id) // Oldest
    }
    
    @Test
    fun `updateNote should modify existing note`() = runTest {
        // Given
        val originalNote = Note("update-test", "Original", "Original content", 12345L, false, false)
        noteDao.insertNote(originalNote)
        
        val updatedNote = originalNote.copy(
            title = "Updated Title",
            content = "Updated content",
            isStarred = true,
            isAiSummarized = true
        )
        
        // When
        noteDao.updateNote(updatedNote)
        val retrievedNote = noteDao.getNoteById("update-test")
        
        // Then
        assertNotNull(retrievedNote)
        assertEquals("Updated Title", retrievedNote?.title)
        assertEquals("Updated content", retrievedNote?.content)
        assertTrue(retrievedNote?.isStarred == true)
        assertTrue(retrievedNote?.isAiSummarized == true)
        assertEquals(12345L, retrievedNote?.timestamp) // Timestamp should remain same
    }
    
    @Test
    fun `deleteNote should remove note from database`() = runTest {
        // Given
        val note = Note("delete-test", "To Delete", "Content", 12345L, false, false)
        noteDao.insertNote(note)
        
        // Verify note exists
        assertNotNull(noteDao.getNoteById("delete-test"))
        
        // When
        noteDao.deleteNote("delete-test")
        
        // Then
        assertNull(noteDao.getNoteById("delete-test"))
    }
    
    @Test
    fun `deleteAllNotes should remove all notes`() = runTest {
        // Given
        noteDao.insertNote(Note("1", "Note 1", "Content 1", 12345L, false, false))
        noteDao.insertNote(Note("2", "Note 2", "Content 2", 12346L, false, false))
        noteDao.insertNote(Note("3", "Note 3", "Content 3", 12347L, false, false))
        
        // Verify notes exist
        assertEquals(3, noteDao.getAllNotes().first().size)
        
        // When
        noteDao.deleteAllNotes()
        
        // Then
        assertEquals(0, noteDao.getAllNotes().first().size)
    }
    
    @Test
    fun `getStarredNotes should return only starred notes`() = runTest {
        // Given
        noteDao.insertNote(Note("1", "Note 1", "Content 1", 12345L, true, false))
        noteDao.insertNote(Note("2", "Note 2", "Content 2", 12346L, false, false))
        noteDao.insertNote(Note("3", "Note 3", "Content 3", 12347L, true, false))
        
        // When
        val starredNotes = noteDao.getStarredNotes().first()
        
        // Then
        assertEquals(2, starredNotes.size)
        assertTrue(starredNotes.all { it.isStarred })
        assertEquals(setOf("1", "3"), starredNotes.map { it.id }.toSet())
    }
    
    @Test
    fun `getAiSummarizedNotes should return only AI summarized notes`() = runTest {
        // Given
        noteDao.insertNote(Note("1", "Note 1", "Content 1", 12345L, false, true))
        noteDao.insertNote(Note("2", "Note 2", "Content 2", 12346L, false, false))
        noteDao.insertNote(Note("3", "Note 3", "Content 3", 12347L, false, true))
        
        // When
        val aiNotes = noteDao.getAiSummarizedNotes().first()
        
        // Then
        assertEquals(2, aiNotes.size)
        assertTrue(aiNotes.all { it.isAiSummarized })
        assertEquals(setOf("1", "3"), aiNotes.map { it.id }.toSet())
    }
    
    @Test
    fun `insertNote with replace strategy should overwrite existing note`() = runTest {
        // Given
        val originalNote = Note("replace-test", "Original", "Original content", 12345L, false, false)
        val replacementNote = Note("replace-test", "Replaced", "Replaced content", 67890L, true, true)
        
        // When
        noteDao.insertNote(originalNote)
        noteDao.insertNote(replacementNote) // Should replace due to OnConflictStrategy.REPLACE
        
        val retrievedNote = noteDao.getNoteById("replace-test")
        
        // Then
        assertNotNull(retrievedNote)
        assertEquals("Replaced", retrievedNote?.title)
        assertEquals("Replaced content", retrievedNote?.content)
        assertEquals(67890L, retrievedNote?.timestamp)
        assertTrue(retrievedNote?.isStarred == true)
        assertTrue(retrievedNote?.isAiSummarized == true)
    }
    
    @Test
    fun `getNoteById with non-existent id should return null`() = runTest {
        // When
        val note = noteDao.getNoteById("non-existent")
        
        // Then
        assertNull(note)
    }
    
    @Test
    fun `deleteNote with non-existent id should not cause errors`() = runTest {
        // Given
        noteDao.insertNote(Note("existing", "Existing", "Content", 12345L, false, false))
        
        // When
        noteDao.deleteNote("non-existent")
        
        // Then - should not crash and existing note should remain
        assertNotNull(noteDao.getNoteById("existing"))
    }
}