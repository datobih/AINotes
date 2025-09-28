package com.example.ainotes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ainotes.ui.theme.AINotesTheme

// Onboarding state enum
enum class OnboardingState {
    INITIAL,
    WELCOME
}

// Colors for the onboarding screen
private val DarkNavyBackground = Color(0xFF1E293B)
private val BlueAccent = Color(0xFF3B82F6)
private val WhiteText = Color(0xFFFFFFFF)
private val LightGrayText = Color(0xFFCBD5E1)
private val CardBackground = Color(0xFF2D3748)
private val FeatureIconBackground = Color(0x52137DEC)

private val FeatureIconColor = Color(0xFF137FEC)

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit = {}
) {
    var currentState by remember { mutableStateOf(OnboardingState.INITIAL) }
    
    when (currentState) {
        OnboardingState.INITIAL -> InitialOnboardingContent(
            onGetStartedClick = { currentState = OnboardingState.WELCOME }
        )
        OnboardingState.WELCOME -> WelcomeOnboardingContent(
            onGetStartedClick = onGetStartedClick
        )
    }
}

@Composable
private fun InitialOnboardingContent(
    onGetStartedClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    // Adaptive values based on screen size
    val isTablet = screenWidth >= 600.dp
    val isLandscape = screenWidth > screenHeight
    
    val horizontalPadding = when {
        isTablet -> if (isLandscape) screenWidth * 0.15f else screenWidth * 0.1f
        screenWidth >= 480.dp -> 48.dp
        else -> 24.dp
    }
    
    val iconSize = when {
        isTablet -> 120.dp
        screenWidth >= 480.dp -> 100.dp
        else -> 80.dp
    }
    
    val titleFontSize = when {
        isTablet -> 56.sp
        screenWidth >= 480.dp -> 48.sp
        else -> 36.sp
    }
    
    val subtitleFontSize = when {
        isTablet -> 20.sp
        screenWidth >= 480.dp -> 18.sp
        else -> 14.sp
    }
    
    val buttonTextFontSize = when {
        isTablet -> 22.sp
        screenWidth >= 480.dp -> 20.sp
        else -> 16.sp
    }
    
    val verticalSpacing = when {
        isTablet -> 48.dp
        screenWidth >= 480.dp -> 40.dp
        else -> 24.dp
    }
    
    val buttonHeight = when {
        isTablet -> 72.dp
        screenWidth >= 480.dp -> 64.dp
        else -> 56.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding)
                .let { 
                    if (isLandscape && !isTablet) {
                        it.padding(vertical = 16.dp)
                    } else {
                        it
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (isLandscape && !isTablet) Arrangement.SpaceEvenly else Arrangement.Center
        ) {
            if (!isLandscape || isTablet) {
                // Spacer to push content up a bit (only in portrait or tablet)
                Spacer(modifier = Modifier.weight(0.3f))
            }
            
            // Document Icon (adaptive size)
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .background(
                        color = BlueAccent,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Simple document icon representation (proportional to icon size)
                val lineWidth1 = iconSize * 0.45f
                val lineWidth2 = iconSize * 0.3f
                val lineHeight = iconSize * 0.05f
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(lineWidth1)
                            .height(lineHeight)
                            .background(
                                color = DarkNavyBackground,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(lineWidth2)
                            .height(lineHeight)
                            .background(
                                color = DarkNavyBackground,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(if (isLandscape && !isTablet) 16.dp else verticalSpacing))
            
            // Title
            Text(
                text = "NoteWise",
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = WhiteText,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(if (isLandscape && !isTablet) 8.dp else 16.dp))
            
            // Subtitle
            Text(
                text = "Your smart companion for capturing\nand organizing thoughts.",
                fontSize = subtitleFontSize,
                color = LightGrayText,
                textAlign = TextAlign.Center,
                lineHeight = subtitleFontSize * 1.5f
            )
            
            if (!isLandscape || isTablet) {
                // Spacer to push button to bottom (only in portrait or tablet)
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Get Started Button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .let { 
                        if (isTablet) {
                            it.widthIn(max = 400.dp)
                        } else {
                            it.fillMaxWidth()
                        }
                    }
                    .height(buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueAccent,
                    contentColor = WhiteText
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = buttonTextFontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(if (isLandscape && !isTablet) 16.dp else 32.dp))
        }
    }
}

@Composable
private fun WelcomeOnboardingContent(
    onGetStartedClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    // Adaptive values based on screen size
    val isTablet = screenWidth >= 600.dp
    val isLandscape = screenWidth > screenHeight
    
    val horizontalPadding = when {
        isTablet -> if (isLandscape) screenWidth * 0.15f else screenWidth * 0.1f
        screenWidth >= 480.dp -> 32.dp
        else -> 24.dp
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        if (isLandscape && !isTablet) {
            // Landscape layout for phones
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Phone mockup
                Column(
                    modifier = Modifier.weight(0.4f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PhoneMockup(
                        modifier = Modifier.size(180.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(32.dp))
                
                // Right side - Content
                Column(
                    modifier = Modifier.weight(0.6f)
                ) {
                    WelcomeContent(
                        isCompact = true,
                        onGetStartedClick = onGetStartedClick
                    )
                }
            }
        } else {
            // Portrait layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                    Spacer(modifier = Modifier.height(if (isTablet) 48.dp else 32.dp))
                    
                    // Phone mockup
                    PhoneMockup(
                        modifier = Modifier.size(if (isTablet) 240.dp else 200.dp)
                    )

                

                    WelcomeContent(
                        isCompact = false,
                        onGetStartedClick = onGetStartedClick
                    )

                

                    Spacer(modifier = Modifier.height(32.dp))

            }
        }
    }
}

@Composable
private fun PhoneMockup(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4F46E5),
                        Color(0xFF7C3AED)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // App logo/icon in the phone
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = WhiteText.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Simple document icon representation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(3.dp)
                        .background(
                            color = WhiteText,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .height(3.dp)
                        .background(
                            color = WhiteText,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun WelcomeContent(
    isCompact: Boolean,
    onGetStartedClick: () -> Unit
) {
    val titleFontSize = if (isCompact) 24.sp else 32.sp
    val subtitleFontSize = if (isCompact) 14.sp else 16.sp
    val spacing = if (isCompact) 16.dp else 24.dp
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome title
        Text(
            text = "Welcome to NoteWise",
            fontSize = titleFontSize,
            fontWeight = FontWeight.Bold,
            lineHeight  = titleFontSize * 1.2f,
            color = WhiteText,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Subtitle
        Text(
            text = "Discover powerful features designed to enhance your note-taking experience",
            fontSize = subtitleFontSize,
            color = LightGrayText,
            textAlign = TextAlign.Center,
            lineHeight = subtitleFontSize * 1.4f
        )
        
        Spacer(modifier = Modifier.height(spacing))
        
        // Feature cards
        FeatureCard(
            title = "Text & Voice Input",
            description = "Write or speak your thoughts naturally",
            icon = { Icon(painter = painterResource(id = R.drawable.mic_24dp),tint = FeatureIconColor, contentDescription = null) },
            isCompact = isCompact
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        FeatureCard(
            title = "AI Summarization",
            description = "Get intelligent summaries of your notes",
            icon = { Icon(painter = painterResource(id = R.drawable.auto_awesome_24dp),tint = FeatureIconColor, contentDescription = null)  },
            isCompact = isCompact
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        FeatureCard(
            title = "PDF Export",
            description = "Export and share your notes easily",
            icon = { Icon(painter = painterResource(id = R.drawable.pdf_24dp),tint = FeatureIconColor, contentDescription = null)  },
            isCompact = isCompact
        )
        
        Spacer(modifier = Modifier.height(spacing))
        
        // Get Started Button
        Button(
            onClick = {onGetStartedClick()},
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isCompact) 48.dp else 56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueAccent,
                contentColor = WhiteText
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = if (isCompact) 16.sp else 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    isCompact: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 12.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(if (isCompact) 36.dp else 40.dp)
                    .background(
                        color = FeatureIconBackground,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = if (isCompact) 14.sp else 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhiteText
                )
                Text(
                    text = description,
                    fontSize = if (isCompact) 12.sp else 14.sp,
                    color = LightGrayText
                )
            }
        }
    }
}

@Composable
private fun TextInputIcon() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(2.dp)
                .background(
                    color = WhiteText,
                    shape = RoundedCornerShape(1.dp)
                )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .width(8.dp)
                .height(2.dp)
                .background(
                    color = WhiteText,
                    shape = RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
private fun AIIcon() {
    Box(
        modifier = Modifier
            .size(16.dp)
            .background(
                color = WhiteText,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = FeatureIconBackground,
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun ExportIcon() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = WhiteText,
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(2.dp)
                .background(
                    color = WhiteText,
                    shape = RoundedCornerShape(1.dp)
                )
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 940)
@Composable
fun WelcomeScreenPreview() {
    AINotesTheme {
        WelcomeOnboardingContent {  }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun OnboardingScreenPreview() {
    AINotesTheme {
        OnboardingScreen()
    }
}

@Preview(showBackground = true, widthDp = 640, heightDp = 360)
@Composable
fun OnboardingScreenLandscapePreview() {
    AINotesTheme {
        OnboardingScreen()
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
fun OnboardingScreenTabletPreview() {
    AINotesTheme {
        OnboardingScreen()
    }
}