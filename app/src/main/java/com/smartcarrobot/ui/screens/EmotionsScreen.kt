package com.smartcarrobot.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartcarrobot.model.RobotEmotion
import com.smartcarrobot.ui.components.RobotFace
import com.smartcarrobot.viewmodel.RobotViewModel

@Composable
fun EmotionsScreen(
    navController: NavController,
    viewModel: RobotViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0F),
                        Color(0xFF1A1A2E),
                        Color(0xFF0A0A0F)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Dynamic Emotions",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preview area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF12121A)),
                contentAlignment = Alignment.Center
            ) {
                RobotFace(state = state, size = 180.dp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.emotion.displayName,
                style = MaterialTheme.typography.displayMedium,
                color = state.emotion.eyeColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = state.emotion.description,
                color = Color(0xFF888888),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Emotion selector
            Text(
                text = "Choose Emotion",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(RobotEmotion.entries.toList()) { emotion ->
                    EmotionCard(
                        emotion = emotion,
                        isSelected = state.emotion == emotion,
                        onClick = { viewModel.setEmotion(emotion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionCard(
    emotion: RobotEmotion,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        emotion.eyeColor.copy(alpha = 0.15f)
    } else {
        Color(0xFF1A1A2E)
    }

    val borderColor = if (isSelected) emotion.eyeColor else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color indicator
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(emotion.eyeColor.copy(alpha = 0.2f))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(emotion.eyeColor)
            )
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = emotion.displayName,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = emotion.description,
                color = Color(0xFF888888),
                fontSize = 12.sp
            )
        }
    }
}