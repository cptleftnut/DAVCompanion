package com.smartcarrobot.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartcarrobot.ui.components.RobotFace
import com.smartcarrobot.viewmodel.RobotViewModel
import kotlinx.coroutines.delay

@Composable
fun VoiceScreen(
    navController: NavController,
    viewModel: RobotViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isRecording by remember { mutableStateOf(false) }
    var commandText by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }

    val sampleCommands = listOf(
        "Hey Mochi, how's traffic?",
        "Play my driving playlist",
        "What's the weather like?",
        "Call Mom",
        "Navigate to the nearest gas station",
        "Tell me a joke",
        "I'm feeling sleepy",
        "Emergency! Call 911"
    )

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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    text = "Voice Interaction",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Robot face listening
            RobotFace(state = state, size = 160.dp)

            Spacer(modifier = Modifier.height(24.dp))

            // Voice wave animation
            if (isRecording || state.isListening) {
                VoiceWaveAnimation(
                    color = state.emotion.eyeColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status text
            AnimatedContent(
                targetState = when {
                    isRecording -> "Listening..."
                    responseText.isNotEmpty() -> responseText
                    commandText.isNotEmpty() -> "You said: $commandText"
                    else -> "Tap the mic and talk to me!"
                },
                transitionSpec = { fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically() }
            ) { text ->
                Text(
                    text = text,
                    color = if (isRecording) state.emotion.eyeColor else Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mic button
            val micScale by animateFloatAsState(
                targetValue = if (isRecording) 1.2f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(micScale)
                    .clip(CircleShape)
                    .background(
                        if (isRecording) state.emotion.eyeColor.copy(alpha = 0.3f)
                        else Color(0xFF1A1A2E)
                    )
                    .clickable {
                        if (isRecording) {
                            isRecording = false
                            // Simulate processing
                            responseText = "Got it! Processing..."
                        } else {
                            isRecording = true
                            commandText = sampleCommands.random()
                            responseText = ""
                            viewModel.startListening()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                    contentDescription = if (isRecording) "Stop" else "Record",
                    tint = if (isRecording) state.emotion.eyeColor else Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sample commands
            Text(
                text = "Try saying:",
                color = Color(0xFF888888),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            sampleCommands.forEach { cmd ->
                SampleCommandChip(
                    command = cmd,
                    onClick = {
                        commandText = cmd
                        responseText = when {
                            cmd.contains("traffic") -> "Traffic is light ahead. Smooth sailing!"
                            cmd.contains("playlist") -> "Playing 'Road Trip Vibes' on Spotify!"
                            cmd.contains("weather") -> "It's 22°C and sunny. Perfect driving weather!"
                            cmd.contains("Call Mom") -> "Calling Mom..."
                            cmd.contains("gas") -> "Nearest gas station is 2.3km ahead on your right."
                            cmd.contains("joke") -> "Why did the car go to therapy? It had too many breakdowns!"
                            cmd.contains("sleepy") -> "Alert! Let's pull over for a coffee break."
                            cmd.contains("Emergency") -> "🚨 Calling emergency services now!"
                            else -> "I heard you! Let me help with that."
                        }
                    }
                )
            }
        }
    }

    // Auto-clear response after delay
    LaunchedEffect(responseText) {
        if (responseText.isNotEmpty() && responseText != "Got it! Processing...") {
            delay(4000)
            responseText = ""
        }
    }
}

@Composable
private fun VoiceWaveAnimation(
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            val delay = index * 100
            val height by infiniteTransition.animateFloat(
                initialValue = 10f,
                targetValue = 50f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = delay, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave_$index"
            )

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(height.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color.copy(alpha = 0.7f))
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun SampleCommandChip(
    command: String,
    onClick: () -> Unit
) {
    Text(
        text = "• $command",
        color = Color(0xFFAAAAAA),
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 8.dp)
    )
}