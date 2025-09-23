package com.example.ainotes.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

/**
 * Navigation actions interface following Interface Segregation Principle.
 * Split into focused interfaces for different navigation concerns.
 */

/**
 * Basic navigation actions for common screen transitions
 */
interface BasicNavigationActions {
    fun navigateToHome()
    fun navigateToOnboarding()
    fun navigateBack(): Boolean
    fun navigateUp(): Boolean
}

/**
 * Note-specific navigation actions
 */
interface NoteNavigationActions {
    fun navigateToNoteEditor(noteId: String? = null)
    fun navigateToNoteDetails(noteId: String)
}

/**
 * Feature navigation actions for app features
 */
interface FeatureNavigationActions {
    fun navigateToSettings()
    fun navigateToSearch()
}

/**
 * Combined navigation actions interface
 * This follows the Interface Segregation Principle by composing focused interfaces
 */
interface NavigationActions : BasicNavigationActions, NoteNavigationActions, FeatureNavigationActions

/**
 * Implementation of NavigationActions that wraps NavController
 * This follows Dependency Inversion Principle by depending on abstractions (NavController interface)
 */
class NavigationActionsImpl(
    private val navController: NavController
) : NavigationActions {

    // Basic Navigation Actions
    override fun navigateToHome() {
        navController.navigate(NavigationDestinations.Home.route) {
            popUpTo(NavigationDestinations.Onboarding.route) { inclusive = true }
        }
    }

    override fun navigateToOnboarding() {
        navController.navigate(NavigationDestinations.Onboarding.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    override fun navigateBack(): Boolean {
        return navController.navigateUp()
    }

    override fun navigateUp(): Boolean {
        return navController.navigateUp()
    }

    // Note Navigation Actions
    override fun navigateToNoteEditor(noteId: String?) {
        val route = NavigationDestinations.NoteEditor.createRoute(noteId)
        navController.navigate(route)
    }

    override fun navigateToNoteDetails(noteId: String) {
        val route = NavigationDestinations.NoteDetails.createRoute(noteId)
        navController.navigate(route)
    }

    // Feature Navigation Actions
    override fun navigateToSettings() {
        navController.navigate(NavigationDestinations.Settings.route)
    }

    override fun navigateToSearch() {
        navController.navigate(NavigationDestinations.Search.route)
    }

    /**
     * Advanced navigation with custom options
     * Useful for specific navigation patterns
     */
    fun navigateWithOptions(destination: NavigationDestinations, navOptions: NavOptions) {
        navController.navigate(destination.route, navOptions)
    }

    /**
     * Clear back stack and navigate to destination
     * Useful for login/logout scenarios
     */
    fun navigateAndClearBackStack(destination: NavigationDestinations) {
        navController.navigate(destination.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    /**
     * Navigate with single top pattern
     * Prevents multiple instances of the same screen
     */
    fun navigateSingleTop(destination: NavigationDestinations) {
        navController.navigate(destination.route) {
            launchSingleTop = true
        }
    }
}

/**
 * Factory function to create NavigationActions
 * This provides a clean way to create navigation actions while hiding implementation details
 */
fun createNavigationActions(navController: NavController): NavigationActions {
    return NavigationActionsImpl(navController)
}