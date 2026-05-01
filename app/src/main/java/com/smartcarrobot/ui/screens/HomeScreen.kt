package com.smartcarrobot.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartcarrobot.model.RobotEmotion
import com.smartcarrobot.ui.components.RobotFace
import com.smartcarrobot.ui.navigation.Screen
import com.smartcarrobot.viewmodel.RobotViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: RobotViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showTapHint by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000)
        showTapHint = false
    }

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
            // Top bar - Status info
            StatusBar(state = state)

            Spacer(modifier = Modifier.height(24.dp))

            // Robot name and tagline
            Text(
                text = "Mochi",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            AnimatedVisibility(
                visible = state.lastVoiceCommand.isNotEmpty(),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    text = state.lastVoiceCommand,
                    style = MaterialTheme.typography.bodyLarge,
                    color = state.emotion.eyeColor,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (state.lastVoiceCommand.isEmpty()) {
                Text(
                    text = state.emotion.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Robot Face - Interactive
            var isPressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )

            Box(
                modifier = Modifier
                    .scale(scale)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            },
                            onTap = {
                                if (!state.isAwake) {
                                    viewModel.wakeUp()
                                } else {
                                    viewModel.petRobot()
                                }
                            }
                        )
                    }
            ) {
                RobotFace(
                    state = state,
                    size = 240.dp
                )

                // Tap hint
                AnimatedVisibility(
                    visible = showTapHint && state.isAwake,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = "Tap to pet me!",
                        color = Color(0xFF888888),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Feature buttons grid
            FeatureGrid(navController = navController, viewModel = viewModel, state = state)

            Spacer(modifier = Modifier.height(16.dp))

            // Pet counter
            if (state.petCount > 0) {
                Text(
                    text = "❤️ Petted ${state.petCount} times",
                    color = Color(0xFFFF4081),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun StatusBar(state: com.smartcarrobot.model.RobotState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Battery
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (state.isCharging) Icons.Filled.Bolt else Icons.Outlined.BatteryFull,
                contentDescription = "Battery",
                tint = if (state.isCharging) Color(0xFF76FF03) else Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${state.batteryLevel}%",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Speed
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Speed,
                contentDescription = "Speed",
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${state.speed.toInt()} km/h",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Temperature
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Thermostat,
                contentDescription = "Temperature",
                tint = Color(0xFFFF6D00),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${state.temperature.toInt()}°C",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun FeatureGrid(
    navController: NavController,
    viewModel: RobotViewModel,
    state: com.smartcarrobot.model.RobotState
) {
    val features = listOf(
        FeatureItem(
            "Voice",
            Icons.Filled.Mic,
            Color(0xFF00E5FF),
            Screen.Voice.route
        ) { viewModel.startListening() },
        FeatureItem(
            "Emotions",
            Icons.Filled.EmojiEmotions,
            Color(0xFFFFEA00),
            Screen.Emotions.route
        ) { },
        FeatureItem(
            "Charge",
            Icons.Filled.BatteryChargingFull,
            Color(0xFF76FF03),
            Screen.Charging.route
        ) { viewModel.startCharging() },
        FeatureItem(
            "Pet Mode",
            Icons.Filled.Pets,
            Color(0xFFFF4081),
            Screen.PetMode.route
        ) { viewModel.petRobot() }
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        features.forEach { feature ->
            FeatureButton(
                feature = feature,
                onClick = {
                    feature.onClick()
                    navController.navigate(feature.route)
                }
            )
        }
    }
}

private data class FeatureItem(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val route: String,
    val onClick: () -> Unit
)

@Composable
private fun FeatureButton(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(feature.color.copy(alpha = 0.15f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.label,
                tint = feature.color,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = feature.label,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}