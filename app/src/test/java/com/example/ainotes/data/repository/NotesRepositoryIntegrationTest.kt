package com.example.ainotes.data.repository

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
 * Integration tests for NotesRepository with Room database
 * Tests the complete flow from repository to database
 */
@RunWith(RobolectricTestRunner::class)
class NotesRepositoryIntegrationTest {
    
    private lateinit var database: NotesDatabase
    private lateinit var repository: NotesRepository
    
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NotesDatabase::class.java
        ).allowMainThreadQueries().build()
        
        repository = NotesRepositoryImpl(database.noteDao())
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun `repository should initially return empty notes list`() = runTest {
        // When
        val notes = repository.getAllNotes().first()
        
        // Then
        assertTrue(notes.isEmpty())
    }
    
    @Test
    fun `repository insertNote and getAllNotes should work together`() = runTest {
        // Given
        val note = Note(
            id = "repo-test-1",
            title = "Repository Test Note",
            content = "Testing repository integration",
            timestamp = System.currentTimeMillis(),
            isStarred = true,
            isAiSummarized = false
        )
        
        // When
        repository.insertNote(note)
        val notes = repository.getAllNotes().first()
        
        // Then
        assertEquals(1, notes.size)
        assertEquals(note, notes[0])
    }
    
    @Test
    fun `repository getNoteById should retrieve correct note`() = runTest {
        // Given
        val note1 = Note("id-1", "Note 1", "Content 1", 12345L, false, false)
        val note2 = Note("id-2", "Note 2", "Content 2", 12346L, true, true)
        
        repository.insertNote(note1)
        repository.insertNote(note2)
        
        // When
        val retrievedNote1 = repository.getNoteById("id-1")
        val retrievedNote2 = repository.getNoteById("id-2")
        val nonExistentNote = repository.getNoteById("non-existent")
        
        // Then
        assertEquals(note1, retrievedNote1)
        assertEquals(note2, retrievedNote2)
        assertNull(nonExistentNote)
    }
    
    @Test
    fun `repository updateNote should modify existing note`() = runTest {
        // Given
        val originalNote = Note("update-repo", "Original", "Original content", 12345L, false, false)
        repository.insertNote(originalNote)
        
        val updatedNote = originalNote.copy(
            title = "Updated Repository",
            content = "Updated through repository",
            isStarred = true,
            isAiSummarized = true
        )
        
        // When
        repository.updateNote(updatedNote)
        
        // Then
        val retrievedNote = repository.getNoteById("update-repo")
        assertEquals(updatedNote, retrievedNote)
        
        // Verify in getAllNotes as well
        val allNotes = repository.getAllNotes().first()
        assertEquals(1, allNotes.size)
        assertEquals(updatedNote, allNotes[0])
    }
    
    @Test
    fun `repository deleteNote should remove note`() = runTest {
        // Given
        val note1 = Note("delete-1", "Note 1", "Content 1", 12345L, false, false)
        val note2 = Note("delete-2", "Note 2", "Content 2", 12346L, false, false)
        
        repository.insertNote(note1)
        repository.insertNote(note2)
        
        // Verify both notes exist
        assertEquals(2, repository.getAllNotes().first().size)
        
        // When
        repository.deleteNote("delete-1")
        
        // Then
        assertNull(repository.getNoteById("delete-1"))
        assertNotNull(repository.getNoteById("delete-2"))
        
        val remainingNotes = repository.getAllNotes().first()
        assertEquals(1, remainingNotes.size)
        assertEquals("delete-2", remainingNotes[0].id)
    }
    
    @Test
    fun `repository should handle multiple notes with correct ordering`() = runTest {
        // Given
        val currentTime = System.currentTimeMillis()
        val note1 = Note("multi-1", "First", "Content 1", currentTime - 2000, false, false)
        val note2 = Note("multi-2", "Second", "Content 2", currentTime - 1000, false, false)
        val note3 = Note("multi-3", "Third", "Content 3", currentTime, false, false)
        
        // When - insert in random order
        repository.insertNote(note2)
        repository.insertNote(note1)
        repository.insertNote(note3)
        
        val notes = repository.getAllNotes().first()
        
        // Then - should be ordered by timestamp descending
        assertEquals(3, notes.size)
        assertEquals("multi-3", notes[0].id) // Most recent
        assertEquals("multi-2", notes[1].id) // Middle
        assertEquals("multi-1", notes[2].id) // Oldest
    }
    
    @Test
    fun `repository should handle concurrent operations correctly`() = runTest {
        // Given
        val note = Note("concurrent", "Concurrent Test", "Content", 12345L, false, false)
        
        // When - perform multiple operations with cumulative updates
        repository.insertNote(note)
        
        var currentNote = repository.getNoteById("concurrent")!!
        currentNote = currentNote.copy(title = "Updated Title")
        repository.updateNote(currentNote)
        
        currentNote = repository.getNoteById("concurrent")!!
        currentNote = currentNote.copy(content = "Updated Content")
        repository.updateNote(currentNote)
        
        currentNote = repository.getNoteById("concurrent")!!
        currentNote = currentNote.copy(isStarred = true)
        repository.updateNote(currentNote)
        
        // Then
        val finalNote = repository.getNoteById("concurrent")
        assertNotNull(finalNote)
        assertEquals("Updated Title", finalNote?.title)
        assertEquals("Updated Content", finalNote?.content)
        assertTrue(finalNote?.isStarred == true)
        assertEquals(12345L, finalNote?.timestamp)
    }
    
    @Test
    fun `repository should maintain data consistency across operations`() = runTest {
        // Given
        val notes = listOf(
            Note("consistency-1", "Note 1", "Content 1", 12345L, true, false),
            Note("consistency-2", "Note 2", "Content 2", 12346L, false, true),
            Note("consistency-3", "Note 3", "Content 3", 12347L, true, true)
        )
        
        // When - insert all notes
        notes.forEach { repository.insertNote(it) }
        
        // Then - verify all data is consistent
        val allNotes = repository.getAllNotes().first()
        assertEquals(3, allNotes.size)
        
        // Verify each note individually
        notes.forEach { originalNote ->
            val retrievedNote = repository.getNoteById(originalNote.id)
            assertEquals(originalNote, retrievedNote)
        }
        
        // Test deletion consistency
        repository.deleteNote("consistency-2")
        assertEquals(2, repository.getAllNotes().first().size)
        assertNull(repository.getNoteById("consistency-2"))
        assertNotNull(repository.getNoteById("consistency-1"))
        assertNotNull(repository.getNoteById("consistency-3"))
    }
    
    @Test
    fun `repository should handle edge cases gracefully`() = runTest {
        // Test deleting non-existent note
        repository.deleteNote("non-existent")
        assertTrue(repository.getAllNotes().first().isEmpty())
        
        // Test updating non-existent note (should not crash)
        val nonExistentNote = Note("non-existent", "Title", "Content", 12345L, false, false)
        repository.updateNote(nonExistentNote)
        
        // Should still be empty
        assertTrue(repository.getAllNotes().first().isEmpty())
        
        // Test retrieving non-existent note
        assertNull(repository.getNoteById("still-non-existent"))
    }
}