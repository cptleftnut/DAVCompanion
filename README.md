# 🤖 Smart Car AI Robot - Android App

> Your AI Companion for Car, Home & Kids

![Android](https://img.shields.io/badge/Android-Jetpack%20Compose-4285F4?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin)
![License](https://img.shields.io/badge/License-MIT-green)

A beautiful Android companion app inspired by the cute AI dashboard robots found on AliExpress (like Dasai Mochi, Eilik, etc.). Features dynamic emotions, voice interaction simulation, magnetic charging animations, and an interactive pet mode.

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🎭 **Dynamic Emotions** | 8 animated emotions: Happy, Sad, Surprised, Sleepy, Angry, Love, Neutral, Charging |
| 🎙️ **Voice Interaction** | Simulated voice commands with visual wave animations |
| 🔋 **Magnetic Charging** | Animated wireless charging dock with real-time stats |
| 🐾 **Pet Mode** | Tap to pet, wake up, sleep, and shake interactions |
| 🚗 **Dashboard Theme** | Dark car-dashboard inspired UI with neon accents |
| 📊 **Live Stats** | Speed, battery, temperature monitoring |

## 🎬 Screenshots

```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│   ⚡ 75%        │  │   🎭 Emotions   │  │   🎙️ Voice      │
│   🚗 0 km/h     │  │   [Robot Face]  │  │   [Wave Anim]   │
│   🌡️ 22°C       │  │   Happy 😊      │  │   "Listening..."│
│                 │  │   Sad 😢        │  │                 │
│   ┌─────────┐   │  │   Surprised 😲│  │   [Mic Button]  │
│   │  ◉   ◉  │   │  │   ...         │  │                 │
│   │    ⌣    │   │  │   Choose one! │  │   Try saying:   │
│   │  Mochi  │   │  │                 │  │   • Hey Mochi   │
│   └─────────┘   │  │                 │  │   • Play music  │
│                 │  │                 │  │                 │
│ [Voice][Emo]    │  │                 │  │                 │
│ [Charge][Pet]   │  │                 │  │                 │
└─────────────────┘  └─────────────────┘  └─────────────────┘
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer
- JDK 17+
- Android SDK 35

### Option 1: Open in Android Studio (Recommended)

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/SmartCarRobot.git
cd SmartCarRobot

# Open in Android Studio
# File -> Open -> Select the SmartCarRobot folder
# Click "Sync Project with Gradle Files" (elephant icon)
# Click Run ▶️
```

### Option 2: Command Line

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/SmartCarRobot.git
cd SmartCarRobot

# Generate Gradle wrapper (first time only)
gradle wrapper --gradle-version 8.9

# Build debug APK
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug
```

## 🏗️ Architecture

```
SmartCarRobot/
├── app/src/main/java/com/smartcarrobot/
│   ├── MainActivity.kt
│   ├── model/
│   │   ├── RobotEmotion.kt      # Emotion enum with colors
│   │   └── RobotState.kt         # UI state data class
│   ├── viewmodel/
│   │   └── RobotViewModel.kt     # Business logic & state management
│   └── ui/
│       ├── theme/
│       │   ├── Theme.kt           # Dark/Light themes
│       │   └── Type.kt            # Typography
│       ├── components/
│       │   └── RobotFace.kt      # 🎨 Animated robot face (Canvas)
│       ├── navigation/
│       │   └── RobotNavHost.kt   # Navigation setup
│       └── screens/
│           ├── HomeScreen.kt      # Main dashboard
│           ├── EmotionsScreen.kt  # Emotion picker
│           ├── VoiceScreen.kt     # Voice interaction
│           ├── ChargingScreen.kt  # Magnetic charging
│           └── PetModeScreen.kt   # Interactive pet mode
```

## 🎨 The Robot Face

The core of the app is a custom Canvas-drawn robot face inspired by real dashboard robots:

- **Animated eyes** with blinking, scaling, and color transitions
- **Dynamic mouth** that curves based on emotion
- **Glow effects** using blur and alpha layers
- **Particle effects** for hearts (Love mode) and sound waves (Listening mode)
- **Breathing animation** for lifelike idle state

## 🛠️ Tech Stack

- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Latest Material Design
- **Navigation Compose** - Type-safe navigation
- **StateFlow** - Reactive state management
- **Canvas API** - Custom drawings and animations

## 📱 Compatible With

- Android 8.0+ (API 26+)
- All screen sizes (phones & tablets)
- Portrait orientation optimized

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first.

## 📄 License

[MIT](LICENSE) - Feel free to use this for your own projects!

---

> **Note:** This is a demo app inspired by AliExpress smart car robots. The actual hardware products connect via Bluetooth/WiFi - this app simulates the companion experience.

**Inspired by:** [Smart Car AI Robot on AliExpress](https://a.aliexpress.com/_Exg5zv8)
