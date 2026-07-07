# 📍 NavDisplay

A real-time Google Maps navigation display built using **ESP32**, **2.8" TFT Display**, and an **Android application**.

NavDisplay captures Google Maps navigation instructions from an Android device and wirelessly transmits them over **Bluetooth Low Energy (BLE)** to an ESP32-powered TFT display, creating a dedicated navigation screen for motorcycles, bicycles, or custom dashboards.

---

## 🎥 Demo

attached videos and photos


---

## ✨ Features

- 📡 Real-time Google Maps navigation
- 📲 Bluetooth Low Energy (BLE) communication
- 🛣 Live turn-by-turn directions
- ↩ Left, Right, Straight, U-Turn and First Exit arrows
- 📍 Distance to next turn
- 🕒 ETA
- 📏 Remaining trip distance
- 🌙 Dark themed TFT interface
- 🔄 Automatic BLE reconnection
- 📢 Background navigation updates using Android Accessibility Service and Notification Listener
- 🚦 Status screen when navigation is inactive
- 📱 Lightweight Android companion app

---

# Hardware

- ESP32 Development Board
- 2.8" SPI TFT Display (ILI9341)
- USB Power Supply / Power Bank

---

# Software

### Android

- Kotlin
- Android Studio
- Accessibility Service
- Notification Listener Service
- BLE GATT Client

### ESP32

- Arduino Framework
- TFT_eSPI
- ESP32 BLE Library

---

# Communication

```
Google Maps
        │
Accessibility Service
        │
Notification Listener (Fallback)
        │
Android BLE Client
        │
Bluetooth Low Energy
        │
ESP32 BLE Server
        │
TFT Display
```

---

# TFT Screens

### Startup

```
Ready to pair
Open NavDisplay App
```

### Connected

```
Good Evening

Where to?
```

### Navigation

```
LEFT

250 m

← Arrow

MG Road

8:42 PM              3.4 km
```

### Disconnected

```
Bluetooth connection failed

Open NavDisplay App
```

---

# BLE Packet Format

```
DIRECTION|DISTANCE|ROAD|ETA|REMAINING
```

Example

```
RIGHT|250 m|MG Road|8:42 PM|3.4 km
```

---

# Supported Directions

- Straight
- Left
- Right
- U-Turn
- First Exit (Roundabout)

---

# Project Structure

```
Android/
│
├── Accessibility Service
├── Notification Listener
├── BLE Manager
├── Foreground BLE Service
└── UI

ESP32/
│
├── BLE Server
├── TFT Graphics
├── Navigation Renderer
└── Arrow Drawing Engine
```

---

# Future Improvements

- Roundabout exit numbers
- Lane guidance
- Auto brightness
- Speed display
- Compass mode
- Offline map support
- Wireless firmware updates
- OLED version
- Android Auto integration (future)

---

# Installation

## ESP32

1. Install Arduino IDE
2. Install ESP32 Board Package
3. Install TFT_eSPI library
4. Configure User_Setup.h
5. Upload firmware

---

## Android

1. Install APK
2. Enable Accessibility Service
3. Enable Notification Access
4. Allow Bluetooth permissions
5. Disable Battery Optimization
6. Open NavDisplay and connect

---

# Why I Built This

As a QA Engineer, I wanted to challenge myself by building an embedded + Android project from scratch using AI-assisted development.

Although I had very little prior experience with Android or ESP32 development, this project allowed me to learn and apply:

- Android development with Kotlin
- BLE communication
- Embedded programming
- ESP32 firmware development
- TFT graphics rendering
- Accessibility Services
- Android Notification APIs
- Google Maps navigation parsing
- UI/UX design
- Debugging hardware/software integration

It also demonstrates how modern AI tools can accelerate learning and enable rapid prototyping of real-world engineering projects.

---

# Author

**Nandu Shajahan (K S)**

LinkedIn  
https://www.linkedin.com/in/nandu-shajahan

GitHub  
https://github.com/nandushajahan

---

# License

MIT License

Feel free to fork, improve and contribute.

⭐ If you found this project useful, consider giving it a star!
