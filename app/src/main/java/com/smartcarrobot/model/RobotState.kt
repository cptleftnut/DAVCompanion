package com.smartcarrobot.model

data class RobotState(
    val emotion: RobotEmotion = RobotEmotion.NEUTRAL,
    val isListening: Boolean = false,
    val isCharging: Boolean = false,
    val batteryLevel: Int = 75,
    val lastVoiceCommand: String = "",
    val isAwake: Boolean = true,
    val petCount: Int = 0,
    val speed: Float = 0f,
    val temperature: Float = 22.0f
)