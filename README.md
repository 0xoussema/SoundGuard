# 🎧 SoundGuard

\<h3 align="center">Your Sound. Your Safety. Your Awareness.\</h3>

\<p align="center">   A modern Android application for monitoring noise exposure, visualizing sound levels, and understanding daily acoustic environments. \</p>

\<p align="center">   \<img src="[https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge\&logo=android\&logoColor=white)" />   \<img src="[https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge\&logo=kotlin\&logoColor=white)" />   \<img src="[https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge\&logo=jetpackcompose\&logoColor=white)" />   \<img src="[https://img.shields.io/badge/Database-Room-00BCD4?style=for-the-badge](https://img.shields.io/badge/Database-Room-00BCD4?style=for-the-badge)" /> \</p>

---

## 🌌 About the Project

SoundGuard is an Android application designed to make noise exposure easier to understand through real-time-style monitoring, visual analytics, and organized measurement history.

The application combines a clean, dark-themed interface with local data storage and interactive charts to provide users with a simple overview of their acoustic environment.

> **The goal:** Turn noise readings into understandable information.

---

## ✨ Features

\<table> \<tr> \<td width="50%">

### 📡 Noise Monitoring

- Live simulated noise readings
- Decibel (dB) display
- Automatic noise classification
- Visual noise-level indicator

\</td> \<td width="50%">

### ⏱️ Exposure Tracking

- Exposure duration counter
- Continuous tracking during navigation
- Daily exposure overview

\</td> \</tr> \<tr> \<td width="50%">

### 📊 Data Visualization

- Interactive noise history chart
- Average noise level
- Peak noise level
- High-level reading statistics

\</td> \<td width="50%">

### 🗂️ History & Reports

- Persistent local measurement history
- Date-based organization
- Today, 7 Days, 30 Days and All filters
- CSV export and report sharing

\</td> \</tr> \</table>

---

## 🎨 Interface

SoundGuard features a dark navy interface with cyan accents, designed to keep important information readable and accessible.

\<p align="center">   \<i>Dashboard · Noise Analytics · Exposure Statistics · History\</i> \</p>

\<!-- Add your application screenshots here -->

---

## 🛠️ Built With

| Technology                   | Purpose                                   |
| ---------------------------- | ----------------------------------------- |
| **Kotlin**                   | Main programming language                 |
| **Jetpack Compose**          | Modern declarative UI                     |
| **Material 3**               | UI components and design                  |
| **Room Database**            | Local data persistence                    |
| **Kotlin Coroutines & Flow** | Asynchronous operations and reactive data |
| **MPAndroidChart**           | Noise history visualization               |
| **Android Studio**           | Development environment                   |

---

## 🏗️ Application Architecture

SoundGuard separates its main responsibilities into different layers:

```text
SoundGuard
    │
    ├── UI Layer
    │     └── Jetpack Compose Screens
    │
    ├── ViewModel
    │     └── Application State
    │
    ├── Repository
    │     └── Data Management
    │
    ├── Database
    │     └── Room
    │
    └── Utilities
          └── CSV Export
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio
- Android SDK
- Kotlin support
- Android emulator or compatible Android device

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/0xoussema/SoundGuard.git
```

**2. Open the project**

Open the cloned folder in Android Studio.

**3. Synchronize Gradle**

Allow Android Studio to download and configure the required dependencies.

**4. Run SoundGuard**

Launch the application on an emulator or Android device.

---

## ⚠️ Current Development Status

SoundGuard currently uses **simulated noise readings** for demonstration and development.

It does not yet use a physical microphone or external sensor to measure actual environmental sound levels.

The application currently demonstrates the monitoring interface, exposure tracking, statistics, local history management, and CSV reporting workflow.

---

## 👨‍💻 Developer

\<p align="center">
&#x20; \<b>Oussema Ayari\</b>\<br/>
&#x20; Embedded Systems & Android Development Enthusiast
&#x20; \<br/>\<br/>
&#x20; \<a href="[https://github.com/0xoussema](https://github.com/0xoussema)">
&#x20;   \<img src="[https://img.shields.io/badge/GitHub-0xoussema-181717?style=for-the-badge&logo=github](https://img.shields.io/badge/GitHub-0xoussema-181717?style=for-the-badge\&logo=github)" />
&#x20; \</a>
\</p>

---

\<p align="center">   \<b>SoundGuard\</b>\<br/>   \<i>Listen to your environment. Understand your exposure.\</i> \</p>
