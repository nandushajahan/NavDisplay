# 📍 NavDisplay

A real-time Google Maps navigation display built using **ESP32**, **2.8" TFT Display**, and an **Android application**.

NavDisplay captures Google Maps navigation instructions from an Android device and wirelessly transmits them over **Bluetooth Low Energy (BLE)** to an ESP32-powered TFT display, creating a dedicated navigation screen for motorcycles, bicycles, or custom dashboards.

---

## 🎥 Demo

<img width="1728" height="2304" alt="IMG_20260704_172856" src="https://github.com/user-attachments/assets/1e1b70cd-812f-4651-b193-ba1988fa8d9b" />
<img width="1689" height="2252" alt="IMG_20260705_234219" src="https://github.com/user-attachments/assets/18d13101-c383-4901-be63-53d1f2af9a7d" />
<img width="2017" height="1513" alt="IMG_20260706_213529" src="https://github.com/user-attachments/assets/936dc3af-5405-4d4e-a659-fb16c0b1505e" />
<img width="2614" height="1960" alt="IMG_20260706_213532" src="https://github.com/user-attachments/assets/f4c47b6d-b684-4116-b2e2-1c88d55a40cd" />
<img width="1220" height="2712" alt="Screenshot_2026-07-07-14-09-12-261_com nandu navdisplay" src="https://github.com/user-attachments/assets/898a639b-2c34-4ccd-ac91-17121e380176" />
<img width="1220" height="2712" alt="Screenshot_2026-07-07-14-09-14-934_com nandu navdisplay" src="https://github.com/user-attachments/assets/9d723eb0-edc6-43fb-8708-8aaaab6af9bd" />
<img width="1220" height="2712" alt="Screenshot_2026-07-07-14-09-18-588_com nandu navdisplay" src="https://github.com/user-attachments/assets/46467544-36ed-45e7-90e1-a92987b708a6" />
<img width="1220" height="2712" alt="Screenshot_2026-07-07-14-09-22-932_com nandu navdisplay" src="https://github.com/user-attachments/assets/1c2290db-e49c-4058-826c-adcf219f244b" />







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
