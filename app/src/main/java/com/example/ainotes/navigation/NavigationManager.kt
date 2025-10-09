package com.example.ainotes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * Navigation Manager following Single Responsibility Principle
 * Responsible only for managing navigation setup and providing navigation components
 */
class NavigationManager(
    private val _navController: NavHostController
) {
    val navigationActions: NavigationActions = createNavigationActions(_navController)
    
    /**
     * Public access to the NavController
     */
    val navController: NavHostController get() = _navController
    
    /**
     * Get the current destination route
     */
    val currentRoute: String?
        get() = _navController.currentDestination?.route
    
    /**
     * Check if we can navigate back
     */
    val canNavigateBack: Boolean
        get() = _navController.previousBackStackEntry != null
    
    /**
     * Navigate to a specific destination with safety checks
     */
    fun navigateToDestination(destination: NavigationDestinations) {
        if (currentRoute != destination.route) {
            when (destination) {
                is NavigationDestinations.Onboarding -> navigationActions.navigateToOnboarding()
                is NavigationDestinations.Home -> navigationActions.navigateToHome()
                is NavigationDestinations.NoteEditor -> navigationActions.navigateToNoteEditor()
                is NavigationDestinations.Settings -> navigationActions.navigateToSettings()
                is NavigationDestinations.Search -> navigationActions.navigateToSearch()
                is NavigationDestinations.NoteDetails -> {
                    // Note: This requires a noteId, should be called through specific method
                    throw IllegalArgumentException("NoteDetails requires noteId parameter")
                }
                is NavigationDestinations.Summary -> {
                    // Note: This requires a noteId, should be called through specific method
                    throw IllegalArgumentException("Summary requires noteId parameter")
                }
            }
        }
    }
    
    /**
     * Navigate to note details with specific note ID
     */
    fun navigateToNoteDetails(noteId: String) {
        navigationActions.navigateToNoteDetails(noteId)
    }
    
    /**
     * Navigate to note editor with optional note ID
     */
    fun navigateToNoteEditor(noteId: String? = null) {
        navigationActions.navigateToNoteEditor(noteId)
    }
    
    /**
     * Navigate to summary screen with specific note ID
     */
    fun navigateToSummary(noteId: String) {
        navigationActions.navigateToSummary(noteId)
    }
    
    /**
     * Handle system back button press
     */
    fun handleBackPress(): Boolean {
        return navigationActions.navigateBack()
    }
}

/**
 * Composable function to create and remember NavigationManager
 * This provides a clean way to set up navigation in Compose
 */
@Composable
fun rememberNavigationManager(
    navController: NavHostController = rememberNavController()
): NavigationManager {
    return remember(navController) {
        NavigationManager(navController)
    }
}

/**
 * Complete navigation setup composable
 * This encapsulates the entire navigation configuration following SOLID principles
 */
@Composable
fun AINotesNavigationSetup(
    navigationManager: NavigationManager = rememberNavigationManager(),
    startDestination: String = NavigationDestinations.Onboarding.route
) {
    val navController = navigationManager.navController
    val navigationActions = navigationManager.navigationActions
    
    AINotesNavGraph(
        navController = navController,
        navigationActions = navigationActions,
        startDestination = startDestination
    )
}


/**
 * Navigation state holder for observing navigation changes
 * Useful for UI state management based on current route
 */
data class NavigationState(
    val currentRoute: String?,
    val canNavigateBack: Boolean,
    val isLoading: Boolean = false
)

/**
 * Extension function to get current navigation state
 */
fun NavigationManager.getNavigationState(): NavigationState {
    return NavigationState(
        currentRoute = currentRoute,
        canNavigateBack = canNavigateBack
    )
}

/**
 * Navigation configuration class for customizing navigation behavior
 * Follows Single Responsibility Principle by only handling configuration
 */
data class NavigationConfig(
    val startDestination: String = NavigationDestinations.Onboarding.route,
    val enableDeepLinks: Boolean = true,
    val enableBackGesture: Boolean = true,
    val popUpToSaveState: Boolean = true,
    val restoreState: Boolean = true
)
