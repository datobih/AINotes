package com.example.ainotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ainotes.ui.theme.AINotesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordTranscribeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Top bar with Skip button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { /* Handle skip */ }
            ) {
                Text(
                    text = "Skip",
                    color = Color(0xFF2196F3),
                    fontSize = 16.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Voice recording section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Voice recording box
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8EAF0)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎤",
                        fontSize = 32.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Voice Recording",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            Text(
                text = "Record & Transcribe",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            Text(
                text = "Capture your thoughts with voice recordings and let AI transcribe them into organized notes automatically.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Permission items
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Microphone Access
            PermissionItem(
                iconText = "🎤",
                title = "Microphone Access",
                subtitle = "Required for voice recording",
                isEnabled = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Storage Access
            PermissionItem(
                iconText = "💾",
                title = "Storage Access",
                subtitle = "To save your notes locally",
                isEnabled = false
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Bottom buttons
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Allow Permissions button
            Button(
                onClick = { /* Handle allow permissions */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Allow Permissions",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Not Now button
            TextButton(
                onClick = { /* Handle not now */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Not Now",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun PermissionItem(
    iconText: String,
    title: String,
    subtitle: String,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = iconText,
            fontSize = 24.sp,
            color = if (isEnabled) Color.Black else Color.Gray
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        // Status indicator (dot)
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(if (isEnabled) Color.Green else Color.Gray)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecordTranscribeScreenPreview() {
    AINotesTheme {
        RecordTranscribeScreen()
    }
}