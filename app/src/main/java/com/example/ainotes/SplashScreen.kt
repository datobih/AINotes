package com.example.ainotes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

// Colors for the splash screen
private val DarkNavyBackground = Color(0xFF1E293B)
private val BlueAccent = Color(0xFF3B82F6)
private val WhiteText = Color(0xFFFFFFFF)
private val LightGrayText = Color(0xFFCBD5E1)

@Composable
fun SplashScreen(
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

@Preview(showBackground = true, widthDp = 660, heightDp = 640)
@Composable
fun SplashScreenPreview() {
    AINotesTheme {
        SplashScreen()
    }
}