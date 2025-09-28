package com.example.ainotes.data.repository

import com.example.ainotes.data.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepositoryImpl @Inject constructor() : NotesRepository {
    
    // In-memory storage for demonstration purposes
    // In a real app, this would be replaced with Room database or other persistent storage
    private val notes = mutableListOf<Note>()
    
    init {
        // Initialize with sample data
        notes.addAll(getSampleNotes())
    }
    
    override fun getAllNotes(): Flow<List<Note>> {
        return flowOf(notes.toList())
    }
    
    override suspend fun getNoteById(id: String): Note? {
        return notes.find { it.id == id }
    }
    
    override suspend fun insertNote(note: Note) {
        notes.add(note)
    }
    
    override suspend fun deleteNote(id: String) {
        notes.removeAll { it.id == id }
    }
    
    override suspend fun updateNote(note: Note) {
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes[index] = note
        }
    }
    
    private fun getSampleNotes(): List<Note> {
        return listOf(
            Note(
                id = "1",
                title = "Meeting Notes: Project Alpha",
                content = "AI-generated summary of the meeting, focusing on key decisions and action items assigned to...",
                timestamp = "2d",
                isAiSummarized = true
            ),
            Note(
                id = "2",
                title = "App Feature Ideas",
                content = "Brainstorming session for new app features, including voice-to-text improvements,...",
                timestamp = "1w"
            ),
            Note(
                id = "3",
                title = "Weekly Groceries",
                content = "Milk, bread, eggs, apples, chicken breast, spinach, olive oil, and quinoa. Remember to...",
                timestamp = "2w"
            ),
            Note(
                id = "4",
                title = "Marketing Campaign Ideas",
                content = "Ideas for the upcoming marketing campaign: influencer collaborations, social media contest,...",
                timestamp = "1m"
            ),
            Note(
                id = "5",
                title = "User Research Insights",
                content = "Key takeaways from user interviews: users want better organization tools and more export...",
                timestamp = "2m"
            )
        )
    }
}