package com.smartcarrobot.model

import androidx.compose.ui.graphics.Color

enum class RobotEmotion(
    val displayName: String,
    val eyeColor: Color,
    val glowColor: Color,
    val description: String
) {
    HAPPY(
        "Happy",
        Color(0xFF00E5FF),
        Color(0xFF00E5FF).copy(alpha = 0.3f),
        "Feeling great! Ready for the road!"
    ),
    SAD(
        "Sad",
        Color(0xFF2979FF),
        Color(0xFF2979FF).copy(alpha = 0.3f),
        "Aww, something's wrong..."
    ),
    SURPRISED(
        "Surprised",
        Color(0xFFFFEA00),
        Color(0xFFFFEA00).copy(alpha = 0.3f),
        "Whoa! Did you see that?!"
    ),
    SLEEPY(
        "Sleepy",
        Color(0xFFB39DDB),
        Color(0xFFB39DDB).copy(alpha = 0.3f),
        "Zzz... Just resting my circuits..."
    ),
    ANGRY(
        "Angry",
        Color(0xFFFF3D00),
        Color(0xFFFF3D00).copy(alpha = 0.3f),
        "Hey! Watch where you're driving!"
    ),
    LOVE(
        "Love",
        Color(0xFFFF4081),
        Color(0xFFFF4081).copy(alpha = 0.3f),
        "You're the best driver ever!"
    ),
    NEUTRAL(
        "Neutral",
        Color(0xFF00BCD4),
        Color(0xFF00BCD4).copy(alpha = 0.3f),
        "Cruising along smoothly..."
    ),
    CHARGING(
        "Charging",
        Color(0xFF76FF03),
        Color(0xFF76FF03).copy(alpha = 0.3f),
        "Yum! Magnetic charging is delicious!"
    )
}