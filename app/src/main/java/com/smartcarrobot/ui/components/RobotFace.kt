package com.smartcarrobot.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.smartcarrobot.model.RobotEmotion
import com.smartcarrobot.model.RobotState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RobotFace(
    state: RobotState,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 200.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "face_anim")

    // Breathing animation
    val breathe by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Blink animation
    val blink by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4000
                1f at 0
                1f at 3700
                0.1f at 3850
                1f at 4000
            }
        ),
        label = "blink"
    )

    // Emotion transition
    val emotionTransition = updateTransition(state.emotion, label = "emotion")
    val eyeScale by emotionTransition.animateFloat(
        transitionSpec = { tween(400, easing = FastOutSlowInEasing) },
        label = "eye_scale"
    ) { emotion ->
        when (emotion) {
            RobotEmotion.SURPRISED -> 1.3f
            RobotEmotion.SLEEPY -> 0.3f
            RobotEmotion.ANGRY -> 1.1f
            else -> 1f
        }
    }

    val eyeOffsetY by emotionTransition.animateFloat(
        transitionSpec = { tween(400) },
        label = "eye_offset"
    ) { emotion ->
        when (emotion) {
            RobotEmotion.SAD -> 15f
            RobotEmotion.ANGRY -> -10f
            else -> 0f
        }
    }

    val mouthCurve by emotionTransition.animateFloat(
        transitionSpec = { tween(400) },
        label = "mouth_curve"
    ) { emotion ->
        when (emotion) {
            RobotEmotion.HAPPY, RobotEmotion.LOVE -> 30f
            RobotEmotion.SAD -> -20f
            RobotEmotion.SURPRISED -> 5f
            RobotEmotion.ANGRY -> -15f
            RobotEmotion.SLEEPY -> 0f
            else -> 10f
        }
    }

    // Glow pulse for charging
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val glowAlpha = if (state.isCharging) glowPulse else 0.4f

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
                .background(state.emotion.glowColor.copy(alpha = glowAlpha))
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize(0.85f)
                .graphicsLayer {
                    scaleX = breathe
                    scaleY = breathe
                }
        ) {
            val canvasWidth = size.width.toPx()
            val canvasHeight = size.height.toPx()
            val centerX = canvasWidth / 2
            val centerY = canvasHeight / 2

            // Robot body/helmet shape (Dasai Mochi style)
            drawRoundRect(
                color = Color(0xFF1A1A2E),
                topLeft = Offset(centerX - canvasWidth * 0.45f, centerY - canvasHeight * 0.45f),
                size = Size(canvasWidth * 0.9f, canvasHeight * 0.9f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(40f, 40f),
                style = Stroke(width = 3f)
            )

            // Screen area (face)
            drawRoundRect(
                color = Color(0xFF050508),
                topLeft = Offset(centerX - canvasWidth * 0.38f, centerY - canvasHeight * 0.35f),
                size = Size(canvasWidth * 0.76f, canvasHeight * 0.7f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(25f, 25f)
            )

            // Draw eyes based on emotion
            val eyeSpacing = canvasWidth * 0.22f
            val eyeY = centerY - canvasHeight * 0.08f + eyeOffsetY
            val eyeRadius = canvasWidth * 0.12f * eyeScale * blink

            // Left eye
            drawRobotEye(
                centerX = centerX - eyeSpacing,
                centerY = eyeY,
                radius = eyeRadius,
                emotion = state.emotion,
                isLeft = true
            )

            // Right eye
            drawRobotEye(
                centerX = centerX + eyeSpacing,
                centerY = eyeY,
                radius = eyeRadius,
                emotion = state.emotion,
                isLeft = false
            )

            // Draw mouth
            drawRobotMouth(
                centerX = centerX,
                centerY = centerY + canvasHeight * 0.18f,
                width = canvasWidth * 0.35f,
                curve = mouthCurve,
                emotion = state.emotion,
                eyeColor = state.emotion.eyeColor
            )

            // Charging indicator
            if (state.isCharging) {
                drawChargingIndicator(
                    centerX = centerX,
                    centerY = centerY + canvasHeight * 0.38f,
                    batteryLevel = state.batteryLevel,
                    color = state.emotion.eyeColor
                )
            }

            // Listening indicator
            if (state.isListening) {
                drawListeningWaves(
                    centerX = centerX,
                    centerY = centerY,
                    maxRadius = canvasWidth * 0.5f,
                    color = state.emotion.eyeColor
                )
            }

            // Heart particles for LOVE emotion
            if (state.emotion == RobotEmotion.LOVE) {
                drawHearts(
                    centerX = centerX,
                    centerY = centerY,
                    canvasWidth = canvasWidth
                )
            }
        }
    }
}

private fun DrawScope.drawRobotEye(
    centerX: Float,
    centerY: Float,
    radius: Float,
    emotion: RobotEmotion,
    isLeft: Boolean
) {
    when (emotion) {
        RobotEmotion.SLEEPY -> {
            // Closed eye (line)
            drawLine(
                color = emotion.eyeColor,
                start = Offset(centerX - radius, centerY),
                end = Offset(centerX + radius, centerY),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        RobotEmotion.ANGRY -> {
            // Angry eye (slanted)
            val slant = if (isLeft) 8f else -8f
            drawLine(
                color = emotion.eyeColor,
                start = Offset(centerX - radius, centerY - slant),
                end = Offset(centerX + radius, centerY + slant),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
            // Eye glow
            drawCircle(
                color = emotion.eyeColor.copy(alpha = 0.3f),
                radius = radius * 1.5f,
                center = Offset(centerX, centerY)
            )
        }
        else -> {
            // Normal round eye
            // Outer glow
            drawCircle(
                color = emotion.eyeColor.copy(alpha = 0.2f),
                radius = radius * 1.8f,
                center = Offset(centerX, centerY)
            )
            // Eye base
            drawCircle(
                color = emotion.eyeColor,
                radius = radius,
                center = Offset(centerX, centerY)
            )
            // Pupil (white highlight)
            val pupilOffset = radius * 0.25f
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = radius * 0.35f,
                center = Offset(centerX - pupilOffset, centerY - pupilOffset)
            )
            // Small highlight
            drawCircle(
                color = Color.White,
                radius = radius * 0.15f,
                center = Offset(centerX + radius * 0.3f, centerY - radius * 0.3f)
            )
        }
    }
}

private fun DrawScope.drawRobotMouth(
    centerX: Float,
    centerY: Float,
    width: Float,
    curve: Float,
    emotion: RobotEmotion,
    eyeColor: Color
) {
    val path = Path().apply {
        moveTo(centerX - width / 2, centerY)
        when (emotion) {
            RobotEmotion.SURPRISED -> {
                // Open mouth (oval)
                addOval(
                    Rect(
                        centerX - width * 0.2f,
                        centerY - width * 0.15f,
                        centerX + width * 0.2f,
                        centerY + width * 0.25f
                    )
                )
            }
            RobotEmotion.SLEEPY -> {
                // Small "o" mouth
                addOval(
                    Rect(
                        centerX - width * 0.08f,
                        centerY - width * 0.06f,
                        centerX + width * 0.08f,
                        centerY + width * 0.1f
                    )
                )
            }
            else -> {
                // Curved smile/frown
                quadraticBezierTo(
                    centerX,
                    centerY + curve,
                    centerX + width / 2,
                    centerY
                )
                if (emotion == RobotEmotion.HAPPY || emotion == RobotEmotion.LOVE) {
                    // Fill the smile
                    lineTo(centerX + width / 2, centerY + 5f)
                    quadraticBezierTo(
                        centerX,
                        centerY + curve + 5f,
                        centerX - width / 2,
                        centerY + 5f
                    )
                    close()
                }
            }
        }
    }

    drawPath(
        path = path,
        color = eyeColor,
        style = if (emotion == RobotEmotion.HAPPY || emotion == RobotEmotion.LOVE) {
            Fill
        } else {
            Stroke(width = 4f, cap = StrokeCap.Round)
        }
    )
}

private fun DrawScope.drawChargingIndicator(
    centerX: Float,
    centerY: Float,
    batteryLevel: Int,
    color: Color
) {
    val barWidth = 80f
    val barHeight = 8f
    val filledWidth = barWidth * (batteryLevel / 100f)

    // Background bar
    drawRoundRect(
        color = Color(0xFF333333),
        topLeft = Offset(centerX - barWidth / 2, centerY),
        size = Size(barWidth, barHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )

    // Filled portion
    drawRoundRect(
        color = color,
        topLeft = Offset(centerX - barWidth / 2, centerY),
        size = Size(filledWidth, barHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )

    // Lightning bolt icon
    val boltPath = Path().apply {
        moveTo(centerX - 6f, centerY - 12f)
        lineTo(centerX + 2f, centerY - 12f)
        lineTo(centerX - 2f, centerY - 2f)
        lineTo(centerX + 4f, centerY - 2f)
        lineTo(centerX - 6f, centerY + 8f)
        lineTo(centerX - 2f, centerY - 2f)
        lineTo(centerX - 8f, centerY - 2f)
        close()
    }
    drawPath(path = boltPath, color = color)
}

private fun DrawScope.drawListeningWaves(
    centerX: Float,
    centerY: Float,
    maxRadius: Float,
    color: Color
) {
    val time = (System.currentTimeMillis() % 2000) / 2000f

    for (i in 0..2) {
        val progress = (time + i * 0.33f) % 1f
        val radius = maxRadius * progress
        val alpha = (1f - progress) * 0.5f

        drawCircle(
            color = color.copy(alpha = alpha),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2f)
        )
    }
}

private fun DrawScope.drawHearts(
    centerX: Float,
    centerY: Float,
    canvasWidth: Float
) {
    val time = (System.currentTimeMillis() % 3000) / 3000f

    for (i in 0..4) {
        val offset = (time + i * 0.2f) % 1f
        val x = centerX + (i - 2) * canvasWidth * 0.15f + sin(offset * 2 * PI).toFloat() * 20f
        val y = centerY - canvasWidth * 0.3f - offset * canvasWidth * 0.3f
        val scale = 0.5f + offset * 0.5f
        val alpha = if (offset > 0.7f) (1f - offset) / 0.3f else 1f

        val heartPath = Path().apply {
            val s = 8f * scale
            moveTo(x, y + s * 0.3f)
            cubicTo(x - s, y - s * 0.5f, x - s * 1.5f, y + s * 0.5f, x, y + s * 1.5f)
            cubicTo(x + s * 1.5f, y + s * 0.5f, x + s, y - s * 0.5f, x, y + s * 0.3f)
            close()
        }

        drawPath(
            path = heartPath,
            color = Color(0xFFFF4081).copy(alpha = alpha)
        )
    }
}