package com.example.ainotes.navigation

/**
 * Navigation destinations for the AI Notes application.
 * This sealed class follows the Single Responsibility Principle by only defining navigation routes.
 * 
 * Each destination contains:
 * - route: The string identifier for navigation
 * - Optional arguments that can be passed to the destination
 */
sealed class NavigationDestinations(val route: String) {
    
    /**
     * Splash screen destination - entry point of the application
     */
    data object Splash : NavigationDestinations("splash")
    
    /**
     * Home screen destination - main dashboard/notes list
     */
    data object Home : NavigationDestinations("home")
    
    /**
     * Note creation/editing screen
     * @param noteId Optional note ID for editing existing notes
     */
    data object NoteEditor : NavigationDestinations("note_editor") {
        const val NOTE_ID_ARG = "noteId"
        fun createRoute(noteId: String? = null): String {
            return if (noteId != null) {
                "$route?$NOTE_ID_ARG=$noteId"
            } else {
                route
            }
        }
    }
    
    /**
     * Note details/viewing screen
     * @param noteId Required note ID to display
     */
    data object NoteDetails : NavigationDestinations("note_details") {
        const val NOTE_ID_ARG = "noteId"
        fun createRoute(noteId: String): String = "$route/$noteId"
    }
    
    /**
     * Settings screen
     */
    data object Settings : NavigationDestinations("settings")
    
    /**
     * Search screen for finding notes
     */
    data object Search : NavigationDestinations("search")
    
    companion object
}

/**
 * Extension function to get all available destinations
 * Useful for navigation testing and validation
 */
fun NavigationDestinations.Companion.getAllDestinations(): List<NavigationDestinations> {
    return listOf(
        NavigationDestinations.Splash,
        NavigationDestinations.Home,
        NavigationDestinations.NoteEditor,
        NavigationDestinations.NoteDetails,
        NavigationDestinations.Settings,
        NavigationDestinations.Search
    )
}