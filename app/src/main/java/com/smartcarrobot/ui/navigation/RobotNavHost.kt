package com.smartcarrobot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartcarrobot.ui.screens.HomeScreen
import com.smartcarrobot.ui.screens.ChargingScreen
import com.smartcarrobot.ui.screens.EmotionsScreen
import com.smartcarrobot.ui.screens.VoiceScreen
import com.smartcarrobot.ui.screens.PetModeScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Charging : Screen("charging")
    data object Emotions : Screen("emotions")
    data object Voice : Screen("voice")
    data object PetMode : Screen("pet_mode")
}

@Composable
fun RobotNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Charging.route) {
            ChargingScreen(navController = navController)
        }
        composable(Screen.Emotions.route) {
            EmotionsScreen(navController = navController)
        }
        composable(Screen.Voice.route) {
            VoiceScreen(navController = navController)
        }
        composable(Screen.PetMode.route) {
            PetModeScreen(navController = navController)
        }
    }
}