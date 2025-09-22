package com.example.ainotes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.ainotes.SplashScreen
import com.example.ainotes.screens.HomeScreen

/**
 * Navigation graph configuration following SOLID principles:
 * - Open/Closed: Easy to extend with new destinations without modifying existing code
 * - Dependency Inversion: Depends on abstractions (NavHostController, NavigationActions)
 * - Single Responsibility: Only responsible for defining navigation routes
 */

/**
 * Main navigation graph for the AI Notes application
 * 
 * @param navController The navigation controller managing the navigation stack
 * @param navigationActions Actions interface for handling navigation operations
 * @param startDestination The initial destination when the app starts
 */
@Composable
fun AINotesNavGraph(
    navController: NavHostController,
    navigationActions: NavigationActions,
    startDestination: String = NavigationDestinations.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(route = NavigationDestinations.Splash.route) {
            SplashScreen(
                onGetStartedClick = {
                    navigationActions.navigateToHome()
                }
            )
        }

        // Home Screen
        composable(route = NavigationDestinations.Home.route) {
            HomeScreen(
                navigationActions = navigationActions
            )
        }

        // Note Editor Screen (with optional noteId parameter)
        composable(
            route = "${NavigationDestinations.NoteEditor.route}?${NavigationDestinations.NoteEditor.NOTE_ID_ARG}={${NavigationDestinations.NoteEditor.NOTE_ID_ARG}}",
            arguments = listOf(
                navArgument(NavigationDestinations.NoteEditor.NOTE_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString(NavigationDestinations.NoteEditor.NOTE_ID_ARG)
            NoteEditorScreen(
                noteId = noteId,
                navigationActions = navigationActions
            )
        }

        // Note Details Screen (with required noteId parameter)
        composable(
            route = "${NavigationDestinations.NoteDetails.route}/{${NavigationDestinations.NoteDetails.NOTE_ID_ARG}}",
            arguments = listOf(
                navArgument(NavigationDestinations.NoteDetails.NOTE_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString(NavigationDestinations.NoteDetails.NOTE_ID_ARG) 
                ?: error("Note ID is required for Note Details screen")
            NoteDetailsScreen(
                noteId = noteId,
                navigationActions = navigationActions
            )
        }

        // Settings Screen
        composable(route = NavigationDestinations.Settings.route) {
            SettingsScreen(
                navigationActions = navigationActions
            )
        }

        // Search Screen
        composable(route = NavigationDestinations.Search.route) {
            SearchScreen(
                navigationActions = navigationActions
            )
        }
    }
}

/**
 * Extension function to add custom destinations to the navigation graph
 * This follows the Open/Closed principle - extending functionality without modifying existing code
 */
@Composable
fun AINotesNavGraph(
    navController: NavHostController,
    navigationActions: NavigationActions,
    startDestination: String = NavigationDestinations.Splash.route,
    additionalDestinations: @Composable (NavigationActions) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Core destinations (same as above)
        composable(route = NavigationDestinations.Splash.route) {
            SplashScreen(
                onGetStartedClick = {
                    navigationActions.navigateToHome()
                }
            )
        }

        composable(route = NavigationDestinations.Home.route) {
            HomeScreen(navigationActions = navigationActions)
        }

        composable(
            route = "${NavigationDestinations.NoteEditor.route}?${NavigationDestinations.NoteEditor.NOTE_ID_ARG}={${NavigationDestinations.NoteEditor.NOTE_ID_ARG}}",
            arguments = listOf(
                navArgument(NavigationDestinations.NoteEditor.NOTE_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString(NavigationDestinations.NoteEditor.NOTE_ID_ARG)
            NoteEditorScreen(noteId = noteId, navigationActions = navigationActions)
        }

        composable(
            route = "${NavigationDestinations.NoteDetails.route}/{${NavigationDestinations.NoteDetails.NOTE_ID_ARG}}",
            arguments = listOf(
                navArgument(NavigationDestinations.NoteDetails.NOTE_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString(NavigationDestinations.NoteDetails.NOTE_ID_ARG)
                ?: error("Note ID is required for Note Details screen")
            NoteDetailsScreen(noteId = noteId, navigationActions = navigationActions)
        }

        composable(route = NavigationDestinations.Settings.route) {
            SettingsScreen(navigationActions = navigationActions)
        }

        composable(route = NavigationDestinations.Search.route) {
            SearchScreen(navigationActions = navigationActions)
        }
    }

    // Additional destinations can be added here
    additionalDestinations(navigationActions)
}

// Placeholder composables for screens that don't exist yet
// These will be replaced with actual screen implementations

@Composable
private fun HomeScreen(navigationActions: NavigationActions) {
    // Placeholder - will be implemented later
}

@Composable
private fun NoteEditorScreen(noteId: String?, navigationActions: NavigationActions) {
    // Placeholder - will be implemented later
}

@Composable
private fun NoteDetailsScreen(noteId: String, navigationActions: NavigationActions) {
    // Placeholder - will be implemented later
}

@Composable
private fun SettingsScreen(navigationActions: NavigationActions) {
    // Placeholder - will be implemented later
}

@Composable
private fun SearchScreen(navigationActions: NavigationActions) {
    // Placeholder - will be implemented later
}