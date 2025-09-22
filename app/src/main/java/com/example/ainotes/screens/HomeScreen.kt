package com.example.ainotes.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ainotes.navigation.NavigationActions
import com.example.ainotes.ui.theme.AINotesTheme

/**
 * Home Screen - Main dashboard for the AI Notes app
 * Following Single Responsibility Principle - handles only home screen UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigationActions: NavigationActions
) {

}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    AINotesTheme {
        // Create a mock NavigationActions for preview
        HomeScreen(
            navigationActions = object : NavigationActions {
                override fun navigateToHome() {}
                override fun navigateToSplash() {}
                override fun navigateBack(): Boolean = false
                override fun navigateUp(): Boolean = false
                override fun navigateToNoteEditor(noteId: String?) {}
                override fun navigateToNoteDetails(noteId: String) {}
                override fun navigateToSettings() {}
                override fun navigateToSearch() {}
            }
        )
    }
}