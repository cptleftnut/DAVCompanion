package com.smartcarrobot.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
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
import com.smartcarrobot.ui.components.RobotFace
import com.smartcarrobot.viewmodel.RobotViewModel

@Composable
fun ChargingScreen(
    navController: NavController,
    viewModel: RobotViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "charge")

    // Magnetic field animation
    val fieldRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "field"
    )

    // Pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0F),
                        Color(0xFF0D1B0D),
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
                    text = "Magnetic Charging",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Charging dock visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                // Magnetic field rings
                repeat(3) { index ->
                    val scale = 0.5f + index * 0.25f
                    val alpha = 0.3f - index * 0.08f
                    Box(
                        modifier = Modifier
                            .size((120 * scale * pulseScale).dp)
                            .clip(CircleShape)
                            .background(Color(0xFF76FF03).copy(alpha = alpha))
                    )
                }

                // Charging pad base
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF1B5E20),
                                    Color(0xFF0D1B0D)
                                )
                            )
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Magnetic coil pattern
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2E7D32))
                    ) {
                        // Coil lines
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            repeat(4) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(2.dp)
                                        .background(Color(0xFF76FF03).copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }

                // Robot floating above
                Box(
                    modifier = Modifier
                        .offset(y = (-80).dp)
                        .size(100.dp)
                ) {
                    RobotFace(state = state, size = 100.dp)
                }

                // Lightning bolts
                if (state.isCharging) {
                    repeat(3) { i ->
                        val offsetX = (-30 + i * 30).dp
                        val offsetY = (-20 + i * 10).dp
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = Color(0xFF76FF03).copy(alpha = 0.6f),
                            modifier = Modifier
                                .offset(x = offsetX, y = offsetY)
                                .size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Battery info
            Text(
                text = if (state.batteryLevel >= 100) "Fully Charged!" else "Charging... ${state.batteryLevel}%",
                style = MaterialTheme.typography.displayMedium,
                color = if (state.batteryLevel >= 100) Color(0xFF76FF03) else Color(0xFF76FF03),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Battery bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1A2E))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(state.batteryLevel / 100f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF2E7D32),
                                    Color(0xFF76FF03)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Magnetic wireless charging active",
                color = Color(0xFF888888),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Charging stats
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Voltage", "5.0V", Color(0xFF00E5FF))
                StatItem("Current", "2.1A", Color(0xFFFFEA00))
                StatItem("Power", "10.5W", Color(0xFF76FF03))
                StatItem("Temp", "32°C", Color(0xFFFF6D00))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color(0xFF888888),
            fontSize = 12.sp
        )
    }
}