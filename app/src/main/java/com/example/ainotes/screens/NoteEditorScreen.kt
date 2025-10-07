package com.example.ainotes.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ainotes.R
import com.example.ainotes.navigation.NavigationActions
import com.example.ainotes.ui.theme.AINotesTheme
import com.example.ainotes.ui.theme.BlueAccent
import com.example.ainotes.ui.theme.DarkGrayText
import com.example.ainotes.ui.theme.DarkNavyBackground
import com.example.ainotes.ui.theme.WhiteText
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Note Editor Screen for creating and editing notes
 * 
 * @param noteId Optional note ID for editing existing notes, null for creating new notes
 * @param navigationActions Navigation actions for handling screen transitions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: String?,
    navigationActions: NavigationActions,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isTablet = screenWidth >= 600.dp
    val isLandscape = screenWidth > screenHeight

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showPermissionRationale by remember { mutableStateOf(false) }
    val recordAudioPermission = Manifest.permission.RECORD_AUDIO



    val speechRecognizerLauncher  = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()){
        result->

        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val spokenTextList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val spokenText = spokenTextList?.get(0) ?: ""
                viewModel.replaceSelectedTextWithSpeech(spokenText)
                viewModel.clearError()
            }
            Activity.RESULT_CANCELED -> {

                val errorMessage = "Speech recognition was cancelled"
                viewModel.setError(errorMessage)
            }
            else -> {

                val errorMessage = "Speech recognition failed"
                viewModel.setError(errorMessage)
            }
        }

    }

    fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizerLauncher.launch(intent)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Microphone permission granted. Voice recording will start when available."
                )
                startSpeechRecognition()
            }
        } else {
            val activity = context.findActivity()
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    recordAudioPermission
                )
            } ?: false

            coroutineScope.launch {
                if (shouldShowRationale) {
                    snackbarHostState.showSnackbar(
                        message = "Microphone permission is required to record voice notes."
                    )
                } else {
                    val result = snackbarHostState.showSnackbar(
                        message = "Microphone permission denied. Enable it in settings to use voice recording.",
                        actionLabel = "Settings",
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        context.openAppSettings()
                    }
                }
            }
        }
    }

    val onMicrophoneClick: () -> Unit = {
        when {
            ContextCompat.checkSelfPermission(
                context,
                recordAudioPermission
            ) == PackageManager.PERMISSION_GRANTED -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Voice recording will start when available."
                    )
                    startSpeechRecognition()
                }
            }

            context.findActivity()?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    recordAudioPermission
                )
            } == true -> {
                showPermissionRationale = true
            }

            else -> {
                permissionLauncher.launch(recordAudioPermission)
            }
        }
    }

    val horizontalPadding = when {
        isTablet -> if (isLandscape) screenWidth * 0.12f else screenWidth * 0.08f
        screenWidth >= 480.dp -> 28.dp
        else -> 24.dp
    }

    val maxContentWidth = when {
        isTablet -> 720.dp
        screenWidth >= 480.dp -> 560.dp
        else -> Dp.Unspecified
    }

    val topSpacing = if (isTablet) 24.dp else 12.dp
    val verticalSpacing = if (isTablet) 28.dp else 24.dp
    val minBodyHeight = if (isTablet) 320.dp else 240.dp
    val fabSize = if (isTablet) 72.dp else 56.dp
    val fabIconSize = if (isTablet) 32.dp else 24.dp
    val fabBottomPadding = if (isTablet) 48.dp else 32.dp
    val contentBottomPadding = fabSize + fabBottomPadding

    val titleTextStyle = if (isTablet) {
        MaterialTheme.typography.headlineMedium.copy(color = WhiteText)
    } else {
        MaterialTheme.typography.headlineSmall.copy(color = WhiteText)
    }

    val bodyTextStyle = if (isTablet) {
        MaterialTheme.typography.bodyLarge.copy(
            color = WhiteText,
            lineHeight = 28.sp
        )
    } else {
        MaterialTheme.typography.bodyLarge.copy(
            color = WhiteText,
            lineHeight = 24.sp
        )
    }

    val saveButtonPadding = if (isTablet) {
        PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    } else {
        PaddingValues(horizontal = 18.dp, vertical = 10.dp)
    }

    val placeholderColor = DarkGrayText.copy(alpha = 0.65f)
    val dividerThickness = if (isTablet) 1.5.dp else 1.dp

    val scrollState = rememberScrollState()
    
    // Load note data when noteId changes
    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
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
                        text = if (uiState.isNewNote) "New Note" else "Edit Note",
                        color = WhiteText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = WhiteText
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.saveNote { success ->
                                if (success) {
                                    navigationActions.navigateBack()
                                }
                            }
                        },
                        enabled = !uiState.isSaving,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = saveButtonPadding,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueAccent,
                            contentColor = Color.White,
                            disabledContainerColor = BlueAccent.copy(alpha = 0.4f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Save",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkNavyBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BlueAccent)
                }
            } else {
                val widthConstraint = if (maxContentWidth != Dp.Unspecified) {
                    Modifier.widthIn(max = maxContentWidth)
                } else {
                    Modifier
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .then(widthConstraint)
                        .padding(horizontal = horizontalPadding)
                        .padding(top = topSpacing)
                        .padding(bottom = contentBottomPadding)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                ) {


                    NoteInputField(
                        value = uiState.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        placeholder = "Title",
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = titleTextStyle,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        placeholderColor = placeholderColor
                    )



                    NoteInputFieldWithSelection(
                        value = uiState.contentField,
                        onValueChange = { viewModel.updateContentField(it) },
                        placeholder = "Start writing or speaking...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = minBodyHeight),
                        textStyle = bodyTextStyle,
                        singleLine = false,
                        minLines = 8,
                        placeholderColor = placeholderColor
                    )

                    // Show error message if any
                    uiState.error?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

      Row(
          modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.BottomCenter)
              .padding(bottom = fabBottomPadding),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
      ) {
          // Summarize Button
          Button(
              onClick = { /* TODO: Implement summarize functionality */ },
              modifier = Modifier
                  .padding(end = 16.dp),
              shape = RoundedCornerShape(24.dp),
              colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF2D3748),
                  contentColor = WhiteText
              ),
              contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
          ) {
              Icon(
                  painter = painterResource(id = R.drawable.auto_awesome_24dp),
                  contentDescription = "Summarize",
                  modifier = Modifier.size(20.dp),
                  tint = WhiteText
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                  text = "Summarize",
                  style = MaterialTheme.typography.labelLarge,
                  color = WhiteText
              )
          }
          
          // Floating Microphone Button
          FloatingActionButton(
              onClick = onMicrophoneClick,
              modifier = Modifier.size(fabSize),
              containerColor = BlueAccent,
              contentColor = Color.White
          ) {
              Icon(
                  painter = painterResource(id = R.drawable.mic_24dp),
                  contentDescription = "Voice Recording",
                  modifier = Modifier.size(fabIconSize)
              )
          }
      }

        }
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = {
                Text(text = "Microphone permission required", color = WhiteText)
            },
            text = {
                Text(
                    text = "AINotes needs access to your microphone so you can record voice notes.",
                    color = WhiteText.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionRationale = false
                        permissionLauncher.launch(recordAudioPermission)
                    }
                ) {
                    Text("Allow", color = BlueAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Not now", color = WhiteText)
                }
            },
            containerColor = DarkNavyBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteEditorScreenPreview() {
    AINotesTheme {
        NoteEditorScreen(
            noteId = null,
            navigationActions = object : NavigationActions {
                override fun navigateToHome() {}
                override fun navigateToOnboarding() {}
                override fun navigateBack(): Boolean = true
                override fun navigateUp(): Boolean = true
                override fun navigateToNoteEditor(noteId: String?) {}
                override fun navigateToNoteDetails(noteId: String) {}
                override fun navigateToSettings() {}
                override fun navigateToSearch() {}
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteEditorScreenEditModePreview() {
    AINotesTheme {
        NoteEditorScreen(
            noteId = "existing-note-id",
            navigationActions = object : NavigationActions {
                override fun navigateToHome() {}
                override fun navigateToOnboarding() {}
                override fun navigateBack(): Boolean = true
                override fun navigateUp(): Boolean = true
                override fun navigateToNoteEditor(noteId: String?) {}
                override fun navigateToNoteDetails(noteId: String) {}
                override fun navigateToSettings() {}
                override fun navigateToSearch() {}
            }
        )
    }
}

@Composable
private fun NoteInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    singleLine: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    minLines: Int = 1,
    placeholderColor: Color = DarkGrayText
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(BlueAccent),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle,
                        color = placeholderColor
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun NoteInputFieldWithSelection(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    singleLine: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    minLines: Int = 1,
    placeholderColor: Color = DarkGrayText
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(BlueAccent),
        decorationBox = { innerTextField ->
            Box {
                if (value.text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle,
                        color = placeholderColor
                    )
                }
                innerTextField()
            }
        }
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}