package com.example.ainotes.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ainotes.data.Note
import com.example.ainotes.navigation.NavigationActions
import com.example.ainotes.ui.theme.*

// Utility function to format timestamp
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m"
        diff < 86400_000 -> "${diff / 3600_000}h"
        diff < 604800_000 -> "${diff / 86400_000}d"
        diff < 2628000_000 -> "${diff / 604800_000}w"
        else -> "${diff / 2628000_000}m"
    }
}

// Sample data
private val sampleNotes = listOf(
    Note(
        id = "1",
        title = "Meeting Notes: Project Alpha",
        content = "AI-generated summary of the meeting, focusing on key decisions and action items assigned to...",
        timestamp = System.currentTimeMillis() - 172800000L, // 2 days ago
        isAiSummarized = true
    ),
    Note(
        id = "2",
        title = "App Feature Ideas",
        content = "Brainstorming session for new app features, including voice-to-text improvements,...",
        timestamp = System.currentTimeMillis() - 604800000L // 1 week ago
    ),
    Note(
        id = "3",
        title = "Weekly Groceries",
        content = "Milk, bread, eggs, apples, chicken breast, spinach, olive oil, and quinoa. Remember to...",
        timestamp = System.currentTimeMillis() - 1209600000L // 2 weeks ago
    ),
    Note(
        id = "4",
        title = "Marketing Campaign Ideas",
        content = "Ideas for the upcoming marketing campaign: influencer collaborations, social media contest,...",
        timestamp = System.currentTimeMillis() - 2628000000L // 1 month ago
    ),
    Note(
        id = "5",
        title = "User Research Insights",
        content = "Key takeaways from user interviews: users want better organization tools and more export...",
        timestamp = System.currentTimeMillis() - 5256000000L // 2 months ago
    )
)

enum class FilterTab {
    ALL, STARRED, AI_SUMMARIZED
}

/**
 * Home Screen - Main dashboard for the AI Notes app
 * Following Single Responsibility Principle - handles only home screen UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigationActions: NavigationActions,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    // Responsive design values
    val isTablet = screenWidth >= 600.dp
    val isLandscape = screenWidth > screenHeight
    
    val horizontalPadding = when {
        isTablet -> if (isLandscape) screenWidth * 0.12f else screenWidth * 0.08f
        screenWidth >= 480.dp -> 24.dp
        else -> 16.dp
    }
    
    val titleFontSize = when {
        isTablet -> 32.sp
        screenWidth >= 480.dp -> 28.sp
        else -> 24.sp
    }
    
    // Filter notes based on selected tab
    val filteredNotes = when (uiState.selectedTab) {
        FilterTab.ALL -> uiState.notes
        FilterTab.STARRED -> uiState.notes.filter { it.isStarred }
        FilterTab.AI_SUMMARIZED -> uiState.notes.filter { it.isAiSummarized }
    }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground),
        containerColor = DarkNavyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notes",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        color = WhiteText
                    )
                },
                actions = {
                    IconButton(onClick = { navigationActions.navigateToSettings() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = WhiteText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkNavyBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigationActions.navigateToNoteEditor() },
                containerColor = BlueAccent,
                contentColor = WhiteText,
                modifier = Modifier.size(if (isTablet) 64.dp else 56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note",
                    modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = horizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Bar - Using OutlinedTextField instead of SearchBar to avoid overlap issues
            OutlinedTextField(
                value = uiState.searchText,
                onValueChange = { viewModel.updateSearchText(it) },
                placeholder = {
                    Text(
                        text = "Search notes",
                        color = LightGrayText,
                        fontSize = if (isTablet) 18.sp else 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = LightGrayText
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2D3748),
                    unfocusedContainerColor = Color(0xFF2D3748),
                    focusedTextColor = WhiteText,
                    unfocusedTextColor = WhiteText,
                    cursorColor = BlueAccent,
                    focusedBorderColor = BlueAccent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isTablet) 64.dp else 56.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 28.dp))
            
            // Filter Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterTabChip(
                        text = "All",
                        selected = uiState.selectedTab == FilterTab.ALL,
                        onClick = { viewModel.filterNotes(FilterTab.ALL) },
                        isTablet = isTablet
                    )
                }
                item {
                    FilterTabChip(
                        text = "Starred",
                        selected = uiState.selectedTab == FilterTab.STARRED,
                        onClick = { viewModel.filterNotes(FilterTab.STARRED) },
                        isTablet = isTablet
                    )
                }
                item {
                    FilterTabChip(
                        text = "AI-Summarized",
                        selected = uiState.selectedTab == FilterTab.AI_SUMMARIZED,
                        onClick = { viewModel.filterNotes(FilterTab.AI_SUMMARIZED) },
                        isTablet = isTablet
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Notes List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredNotes) { note ->
                    NoteCard(
                        note = note,
                        onClick = { navigationActions.navigateToNoteEditor(note.id) },
                        isTablet = isTablet
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterTabChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    isTablet: Boolean
) {
    val backgroundColor = if (selected) BlueAccent else Color(0xFF2D3748)
    val textColor = if (selected) WhiteText else LightGrayText
    val fontSize = if (isTablet) 16.sp else 14.sp
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(
                horizontal = if (isTablet) 20.dp else 16.dp,
                vertical = if (isTablet) 12.dp else 10.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    isTablet: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D3748)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 20.dp else 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    fontSize = if (isTablet) 20.sp else 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhiteText,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = formatTimestamp(note.timestamp),
                    fontSize = if (isTablet) 16.sp else 14.sp,
                    color = LightGrayText,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = note.content,
                fontSize = if (isTablet) 16.sp else 14.sp,
                color = LightGrayText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = (if (isTablet) 16.sp else 14.sp) * 1.4f
            )
        }
    }
}

// Mock ViewModels and data for previews
private val mockRepository = object : com.example.ainotes.data.repository.NotesRepository {
    override fun getAllNotes() = kotlinx.coroutines.flow.flowOf(sampleNotes)
    override suspend fun getNoteById(id: String) = sampleNotes.find { it.id == id }
    override suspend fun insertNote(note: Note) {}
    override suspend fun deleteNote(id: String) {}
    override suspend fun updateNote(note: Note) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewHomeScreen(
    navigationActions: NavigationActions,
    isTablet: Boolean = false
) {
    val mockUiState = remember { 
        mutableStateOf(
            HomeUiState(
                notes = sampleNotes,
                selectedTab = FilterTab.ALL,
                searchText = "",
                isLoading = false
            )
        )
    }
    
    // Create preview version of HomeScreen with manual state management
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    // Responsive design values
    val actualIsTablet = isTablet || screenWidth >= 600.dp
    val isLandscape = screenWidth > screenHeight
    
    val horizontalPadding = when {
        actualIsTablet -> if (isLandscape) screenWidth * 0.12f else screenWidth * 0.08f
        screenWidth >= 480.dp -> 24.dp
        else -> 16.dp
    }
    
    val titleFontSize = when {
        actualIsTablet -> 32.sp
        screenWidth >= 480.dp -> 28.sp
        else -> 24.sp
    }
    
    // Filter notes based on selected tab
    val filteredNotes = when (mockUiState.value.selectedTab) {
        FilterTab.ALL -> mockUiState.value.notes
        FilterTab.STARRED -> mockUiState.value.notes.filter { it.isStarred }
        FilterTab.AI_SUMMARIZED -> mockUiState.value.notes.filter { it.isAiSummarized }
    }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground),
        containerColor = DarkNavyBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notes",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        color = WhiteText
                    )
                },
                actions = {
                    IconButton(onClick = { navigationActions.navigateToSettings() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = WhiteText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkNavyBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigationActions.navigateToNoteEditor() },
                containerColor = BlueAccent,
                contentColor = WhiteText,
                modifier = Modifier.size(if (actualIsTablet) 64.dp else 56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note",
                    modifier = Modifier.size(if (actualIsTablet) 28.dp else 24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = horizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Bar - Using OutlinedTextField instead of SearchBar to avoid overlap issues
            OutlinedTextField(
                value = mockUiState.value.searchText,
                onValueChange = { 
                    mockUiState.value = mockUiState.value.copy(searchText = it)
                },
                placeholder = {
                    Text(
                        text = "Search notes",
                        color = LightGrayText,
                        fontSize = if (actualIsTablet) 18.sp else 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = LightGrayText
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2D3748),
                    unfocusedContainerColor = Color(0xFF2D3748),
                    focusedTextColor = WhiteText,
                    unfocusedTextColor = WhiteText,
                    cursorColor = BlueAccent,
                    focusedBorderColor = BlueAccent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (actualIsTablet) 64.dp else 56.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(if (actualIsTablet) 32.dp else 28.dp))
            
            // Filter Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterTabChip(
                        text = "All",
                        selected = mockUiState.value.selectedTab == FilterTab.ALL,
                        onClick = { 
                            mockUiState.value = mockUiState.value.copy(selectedTab = FilterTab.ALL)
                        },
                        isTablet = actualIsTablet
                    )
                }
                item {
                    FilterTabChip(
                        text = "Starred",
                        selected = mockUiState.value.selectedTab == FilterTab.STARRED,
                        onClick = { 
                            mockUiState.value = mockUiState.value.copy(selectedTab = FilterTab.STARRED)
                        },
                        isTablet = actualIsTablet
                    )
                }
                item {
                    FilterTabChip(
                        text = "AI-Summarized",
                        selected = mockUiState.value.selectedTab == FilterTab.AI_SUMMARIZED,
                        onClick = { 
                            mockUiState.value = mockUiState.value.copy(selectedTab = FilterTab.AI_SUMMARIZED)
                        },
                        isTablet = actualIsTablet
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Notes List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredNotes) { note ->
                    NoteCard(
                        note = note,
                        onClick = { navigationActions.navigateToNoteEditor(note.id) },
                        isTablet = actualIsTablet
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Home Screen - Phone")
@Composable
private fun HomeScreenPreview() {
    AINotesTheme {
        PreviewHomeScreen(
            navigationActions = object : NavigationActions {
                override fun navigateToHome() {}
                override fun navigateToOnboarding() {}
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

@Preview(showBackground = true, name = "Home Screen - Tablet", widthDp = 800, heightDp = 1280)
@Composable
private fun HomeScreenTabletPreview() {
    AINotesTheme {
        PreviewHomeScreen(
            navigationActions = object : NavigationActions {
                override fun navigateToHome() {}
                override fun navigateToOnboarding() {}
                override fun navigateBack(): Boolean = false
                override fun navigateUp(): Boolean = false
                override fun navigateToNoteEditor(noteId: String?) {}
                override fun navigateToNoteDetails(noteId: String) {}
                override fun navigateToSettings() {}
                override fun navigateToSearch() {}
            },
            isTablet = true
        )
    }
}

@Preview(showBackground = true, name = "Note Card - Phone", widthDp = 360)
@Composable
private fun NoteCardPreview() {
    AINotesTheme {
        NoteCard(
            note = sampleNotes.first(),
            onClick = {},
            isTablet = false
        )
    }
}

@Preview(showBackground = true, name = "Note Card - Tablet", widthDp = 800)
@Composable
private fun NoteCardTabletPreview() {
    AINotesTheme {
        NoteCard(
            note = sampleNotes.first(),
            onClick = {},
            isTablet = true
        )
    }
}

@Preview(showBackground = true, name = "Filter Tabs", widthDp = 360)
@Composable
private fun FilterTabsPreview() {
    AINotesTheme {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkNavyBackground)
                .padding(16.dp)
        ) {
            item {
                FilterTabChip(
                    text = "All",
                    selected = true,
                    onClick = {},
                    isTablet = false
                )
            }
            item {
                FilterTabChip(
                    text = "Starred",
                    selected = false,
                    onClick = {},
                    isTablet = false
                )
            }
            item {
                FilterTabChip(
                    text = "AI-Summarized",
                    selected = false,
                    onClick = {},
                    isTablet = false
                )
            }
        }
    }
}