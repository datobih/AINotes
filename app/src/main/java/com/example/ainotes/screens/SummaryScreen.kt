package com.example.ainotes.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ainotes.R
import com.example.ainotes.navigation.NavigationActions
import com.example.ainotes.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Summary Screen - Displays AI-generated summary of a note
 * 
 * @param noteId The ID of the note to summarize
 * @param navigationActions Navigation actions for handling screen transitions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    noteId: String,
    navigationActions: NavigationActions,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    val isTablet = screenWidth >= 600.dp
    
    // Load summary when screen opens
    LaunchedEffect(noteId) {
        viewModel.generateSummary(noteId)
    }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground),
        containerColor = DarkNavyBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Summary",
                        color = WhiteText,
                        fontSize = if (isTablet) 24.sp else 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = WhiteText
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkNavyBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = if (isTablet) 32.dp else 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 24.dp))
                
                // AI Icon and Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // AI Icon
                    Surface(
                        modifier = Modifier.size(if (isTablet) 64.dp else 56.dp),
                        shape = CircleShape,
                        color = BlueAccent
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.auto_awesome_24dp),
                                contentDescription = "AI",
                                tint = Color.White,
                                modifier = Modifier.size(if (isTablet) 32.dp else 28.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Header Text
                    Column {
                        Text(
                            text = "AI Generated Summary",
                            fontSize = if (isTablet) 26.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhiteText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Here's a concise summary of your note:",
                            fontSize = if (isTablet) 18.sp else 16.sp,
                            color = LightGrayText
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 24.dp))
                
                // Summary Content Card
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BlueAccent)
                    }
                } else if (uiState.error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "An error occurred",
                            modifier = Modifier.padding(if (isTablet) 24.dp else 20.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = if (isTablet) 18.sp else 16.sp
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2D3748)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = uiState.summary,
                            modifier = Modifier.padding(if (isTablet) 24.dp else 20.dp),
                            color = LightGrayText,
                            fontSize = if (isTablet) 18.sp else 16.sp,
                            lineHeight = if (isTablet) 28.sp else 24.sp
                        )
                    }
                }
            }
            
            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (isTablet) 32.dp else 24.dp)
            ) {
                // Copy Summary Button
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(uiState.summary))
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Summary copied to clipboard")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 64.dp else 56.dp),
                    enabled = !uiState.isLoading && uiState.error == null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueAccent,
                        contentColor = Color.White,
                        disabledContainerColor = BlueAccent.copy(alpha = 0.4f),
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.auto_awesome_24dp),
                        contentDescription = "Copy",
                        modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Copy Summary",
                        fontSize = if (isTablet) 18.sp else 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Back to Note Button
                OutlinedButton(
                    onClick = { navigationActions.navigateBack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 64.dp else 56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFF2D3748),
                        contentColor = WhiteText
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Back to Note",
                        fontSize = if (isTablet) 18.sp else 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SummaryScreenPreview() {
    AINotesTheme {
        SummaryScreen(
            noteId = "test-id",
            navigationActions = object : NavigationActions {
                override fun navigateToHome() {}
                override fun navigateToOnboarding() {}
                override fun navigateBack(): Boolean = true
                override fun navigateUp(): Boolean = true
                override fun navigateToNoteEditor(noteId: String?) {}
                override fun navigateToNoteDetails(noteId: String) {}
                override fun navigateToSettings() {}
                override fun navigateToSearch() {}
                override fun navigateToSummary(noteId: String) {}
            }
        )
    }
}
