package com.smartcarrobot.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartcarrobot.model.RobotEmotion
import com.smartcarrobot.ui.components.RobotFace
import com.smartcarrobot.viewmodel.RobotViewModel
import kotlinx.coroutines.delay

@Composable
fun PetModeScreen(
    navController: NavController,
    viewModel: RobotViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showHeart by remember { mutableStateOf(false) }
    var heartPosition by remember { mutableStateOf(Offset.Zero) }

    val infiniteTransition = rememberInfiniteTransition(label = "pet")

    // Idle bounce
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0F),
                        Color(0xFF2D0A1F),
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
                    text = "Pet Mode",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pet stats
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    icon = Icons.Filled.Favorite,
                    label = "Happiness",
                    value = "${(state.petCount * 5 + 50).coerceAtMost(100)}",
                    color = Color(0xFFFF4081)
                )
                StatCard(
                    icon = Icons.Filled.Pets,
                    label = "Pet Count",
                    value = "${state.petCount}",
                    color = Color(0xFF00E5FF)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Robot in pet mode - bigger and more interactive
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Floating hearts background
                if (state.emotion == RobotEmotion.LOVE) {
                    FloatingHearts()
                }

                // Robot face with bounce
                Box(
                    modifier = Modifier.offset(y = bounce.dp)
                ) {
                    var isPressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.9f else 1f,
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
                                        viewModel.petRobot()
                                        showHeart = true
                                    }
                                )
                            }
                    ) {
                        RobotFace(state = state, size = 200.dp)
                    }
                }

                // Heart burst on pet
                AnimatedVisibility(
                    visible = showHeart,
                    enter = scaleIn(initialScale = 0.3f) + fadeIn(),
                    exit = scaleOut(targetScale = 2f) + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFFF4081),
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Interaction hint
            Text(
                text = when (state.emotion) {
                    RobotEmotion.LOVE -> "Mochi loves you! ❤️"
                    RobotEmotion.HAPPY -> "Mochi is happy! Keep petting!"
                    RobotEmotion.SURPRISED -> "Oh! That tickles!"
                    else -> "Tap Mochi to pet!"
                },
                color = state.emotion.eyeColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Interaction buttons
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InteractionButton(
                    label = "Pet",
                    color = Color(0xFFFF4081),
                    onClick = {
                        viewModel.petRobot()
                        showHeart = true
                    }
                )
                InteractionButton(
                    label = "Wake",
                    color = Color(0xFF00E5FF),
                    onClick = { viewModel.wakeUp() }
                )
                InteractionButton(
                    label = "Sleep",
                    color = Color(0xFFB39DDB),
                    onClick = { viewModel.sleep() }
                )
                InteractionButton(
                    label = "Shake",
                    color = Color(0xFFFFEA00),
                    onClick = { viewModel.shakeDetected() }
                )
            }
        }
    }

    // Auto-hide heart
    LaunchedEffect(showHeart) {
        if (showHeart) {
            delay(800)
            showHeart = false
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1A2E))
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = label,
            color = Color(0xFF888888),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun InteractionButton(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FloatingHearts() {
    val infiniteTransition = rememberInfiniteTransition(label = "hearts")

    Box(modifier = Modifier.fillMaxSize()) {
        repeat(6) { index ->
            val offset by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000 + index * 500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "heart_$index"
            )

            val xPos = 0.1f + index * 0.15f

            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = Color(0xFFFF4081).copy(alpha = 0.3f * (1f - offset)),
                modifier = Modifier
                    .offset(
                        x = (xPos * 300).dp,
                        y = (offset * 250).dp
                    )
                    .size((16 + index * 4).dp)
            )
        }
    }
}