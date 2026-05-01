package com.smartcarrobot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcarrobot.model.RobotEmotion
import com.smartcarrobot.model.RobotState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RobotViewModel : ViewModel() {

    private val _state = MutableStateFlow(RobotState())
    val state: StateFlow<RobotState> = _state.asStateFlow()

    private val emotionCycle = listOf(
        RobotEmotion.HAPPY,
        RobotEmotion.NEUTRAL,
        RobotEmotion.SURPRISED,
        RobotEmotion.LOVE,
        RobotEmotion.SLEEPY
    )

    init {
        startIdleAnimation()
    }

    private fun startIdleAnimation() {
        viewModelScope.launch {
            var index = 0
            while (true) {
                delay(4000)
                if (!_state.value.isListening && !_state.value.isCharging && _state.value.isAwake) {
                    _state.value = _state.value.copy(
                        emotion = emotionCycle[index % emotionCycle.size]
                    )
                    index++
                }
            }
        }
    }

    fun wakeUp() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isAwake = true, emotion = RobotEmotion.SURPRISED)
            delay(800)
            _state.value = _state.value.copy(emotion = RobotEmotion.HAPPY)
        }
    }

    fun sleep() {
        _state.value = _state.value.copy(
            isAwake = false,
            emotion = RobotEmotion.SLEEPY
        )
    }

    fun petRobot() {
        viewModelScope.launch {
            val newCount = _state.value.petCount + 1
            _state.value = _state.value.copy(
                petCount = newCount,
                emotion = RobotEmotion.LOVE
            )
            delay(1500)
            if (_state.value.emotion == RobotEmotion.LOVE) {
                _state.value = _state.value.copy(emotion = RobotEmotion.HAPPY)
            }
        }
    }

    fun startListening() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isListening = true, emotion = RobotEmotion.SURPRISED)
            delay(2000)
            _state.value = _state.value.copy(
                isListening = false,
                emotion = RobotEmotion.HAPPY,
                lastVoiceCommand = ""Hey Mochi, play my driving playlist!""
            )
            delay(3000)
            _state.value = _state.value.copy(lastVoiceCommand = "")
        }
    }

    fun startCharging() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCharging = true, emotion = RobotEmotion.CHARGING)
            var level = _state.value.batteryLevel
            while (level < 100) {
                delay(800)
                level = (level + 5).coerceAtMost(100)
                _state.value = _state.value.copy(batteryLevel = level)
            }
            delay(1000)
            _state.value = _state.value.copy(
                isCharging = false,
                emotion = RobotEmotion.HAPPY
            )
        }
    }

    fun setEmotion(emotion: RobotEmotion) {
        _state.value = _state.value.copy(emotion = emotion)
    }

    fun updateSpeed(speed: Float) {
        _state.value = _state.value.copy(speed = speed)
        when {
            speed > 120 -> setEmotion(RobotEmotion.ANGRY)
            speed > 80 -> setEmotion(RobotEmotion.SURPRISED)
            speed > 0 -> setEmotion(RobotEmotion.HAPPY)
        }
    }

    fun shakeDetected() {
        viewModelScope.launch {
            _state.value = _state.value.copy(emotion = RobotEmotion.SURPRISED)
            delay(500)
            if (!_state.value.isAwake) {
                wakeUp()
            } else {
                _state.value = _state.value.copy(emotion = RobotEmotion.HAPPY)
            }
        }
    }
}