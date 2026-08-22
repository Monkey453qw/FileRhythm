<div align="center">

# 📁 FileRhythm

**Your files, your rhythm.**

A modern, open-source file manager for Android — built with Material 3 Expressive design.

[![Platform](https://img.shields.io/badge/Android-34A853?style=flat-square&logo=android&logoColor=white)](https://android.com)
[![API Level](https://img.shields.io/badge/API-26%2B-4285f4?style=flat-square&logo=android&logoColor=white)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7c4dff?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-GPL_v3-4285f4?style=flat-square)](LICENSE)
[![Build APK](https://img.shields.io/github/actions/workflow/status/<your-username>/FileRhythm/build.yml?style=flat-square&logo=github&label=Build)](https://github.com/<your-username>/FileRhythm/actions/workflows/build.yml)

[📥 Download latest APK](https://github.com/<your-username>/FileRhythm/releases/latest) ·
[📦 Browse artifacts](https://github.com/<your-username>/FileRhythm/actions) ·
[🐛 Report an issue](https://github.com/<your-username>/FileRhythm/issues)

</div>

---

## ✨ Why FileRhythm?

FileRhythm is a Google Files alternative for Android, designed to look and feel like the [Rhythm music player](https://github.com/cromaguy/Rhythm) — Material 3 Expressive design language, organic shapes, pill chips, rounded corners, and dynamic color support.

| 🎨 **Design** | ⚡ **Performance** |
| :--- | :--- |
| **Material 3 Expressive** — rounded shapes, pill chips, 28dp bottom sheets | Built with Jetpack Compose — fast, native, no webviews |
| **Dynamic color** — wallpaper-based palette on Android 12+ | MediaStore-backed — fast media browsing without file scans |
| **Light / Dark / System** themes with on-the-fly switching | Edge-to-edge UI with proper inset handling |
| **Material Symbols** icons throughout | State persistence via DataStore |

| 🛠️ **Features** | 🔒 **Privacy** |
| :--- | :--- |
| Browse files & folders, copy/move/delete/rename | 100% offline — no telemetry, no network calls |
| Search files by name across all categories | No analytics, no ads, no tracking |
| Categories: Images, Videos, Audio, Docs, APKs, Archives | FOSS — GPL-3.0 licensed |
| Storage analyzer with per-category breakdown | Your files never leave your device |

> **System Requirements:** Android 8.0+ (API 26) • ~50 MB storage

---

## 📥 Download & Install

### Option 1 — Pre-built APK from GitHub Releases

1. Go to the [Releases page](https://github.com/<your-username>/FileRhythm/releases/latest)
2. Download `app-debug.apk` (or `app-release-unsigned.apk` if a release exists)
3. On your phone: enable **"Install unknown apps"** for your browser/Files app
4. Tap the APK file to install

### Option 2 — Latest build from GitHub Actions

1. Go to the [Actions tab → Build APK workflow](https://github.com/<your-username>/FileRhythm/actions/workflows/build.yml)
2. Click the most recent successful run
3. Scroll to **Artifacts** → download `FileRhythm-debug-apk` (zip containing the APK)
4. Unzip and install the APK as above

### Option 3 — Build it yourself

See [Building from source](#-building-from-source) below.

---

## 🏗️ Building from source

### Prerequisites
- **Android Studio** Ladybug or newer (or just JDK 17 + Android SDK CLI)
- **Android SDK** with `compileSdk = 35` and `minSdk = 26` platform components
- **JDK 17**

### Steps

```bash
# Clone
git clone https://github.com/<your-username>/FileRhythm.git
cd FileRhythm

# (Optional) Create local.properties pointing to your Android SDK
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# Build a debug APK
./gradlew assembleDebug

# The APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

### Build variants
- `./gradlew assembleDebug` → debug APK (with `.debug` applicationId suffix)
- `./gradlew assembleRelease` → release APK (unsigned unless you provide a keystore)
- `./gradlew installDebug` → install debug APK on a connected device via `adb`

### Adding a signing config (for release builds)
1. Create a keystore: `keytool -genkey -v -keystore filerhythm.jks -keyalg RSA -keysize 2048 -validity 10000 -alias filerhythm`
2. Create `.config/keystore.properties` (do NOT commit this file — it's in `.gitignore`):
   ```properties
   key_alias=filerhythm
   key_password=your_key_password
   store_password=your_store_password
   store_file=/absolute/path/to/filerhythm.jks
   ```
3. Update `app/build.gradle.kts` to read this config (the project already supports this pattern).

---

## 🚀 GitHub Actions CI/CD

This repo includes a GitHub Actions workflow at [`.github/workflows/build.yml`](.github/workflows/build.yml) that:

- Triggers on every push to `main` / `master`, on every PR, and on version tags (`v1.0.0`, etc.)
- Builds both debug and release APKs
- Uploads the APKs as **workflow artifacts** (downloadable from the Actions tab)
- When you push a `v*` tag, automatically creates a GitHub Release with the APKs attached

### To create your first release
```bash
git tag v1.0.0
git push origin v1.0.0
```
GitHub will build the APK and publish a release automatically.

---

## 🧱 Tech stack

- **Language:** Kotlin 2.0
- **UI:** Jetpack Compose + Material 3 (with `ExperimentalMaterial3ExpressiveApi`)
- **Architecture:** Single-activity + Compose Navigation
- **Persistence:** Jetpack DataStore (preferences)
- **File access:** `MediaStore` (for media) + `DocumentFile`/SAF (for full tree) + `java.io.File`
- **Image loading:** Coil
- **Splash:** `androidx.core.splashscreen`
- **Min Android:** 8.0 (API 26)
- **Target Android:** 15 (API 35)

---

## 🗂️ Project structure

```
FileRhythm/
├── app/
│   ├── build.gradle.kts              # App module build config
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/filerhythm/app/
│       │   ├── FileRhythmApp.kt       # Application class
│       │   ├── MainActivity.kt        # Single-activity host
│       │   ├── ui/
│       │   │   ├── FileRhythmApp.kt   # Root composable + NavHost
│       │   │   ├── theme/
│       │   │   │   ├── Color.kt       # M3 color palette (light/dark)
│       │   │   │   ├── Shape.kt       # Expressive shape system
│       │   │   │   ├── Type.kt        # M3 typography scale
│       │   │   │   ├── Theme.kt       # FileRhythmTheme composable
│       │   │   │   ├── SettingsRepository.kt  # DataStore-backed settings
│       │   │   │   └── SettingsViewModel.kt
│       │   │   ├── navigation/
│       │   │   │   └── Destinations.kt
│       │   │   └── screens/
│       │   │       ├── home/          # Home screen (quick access, categories, storage)
│       │   │       ├── categories/    # File categories grid
│       │   │       ├── storage/       # Storage analyzer
│       │   │       ├── search/        # Global file search
│       │   │       ├── filebrowser/   # Generic file browser (path or category)
│       │   │       └── settings/      # App settings
│       │   ├── data/
│       │   │   ├── model/FileItem.kt
│       │   │   └── repository/FileRepository.kt
│       │   └── util/                  # Helpers (empty for now)
│       └── res/
│           ├── values/                # Strings, colors, themes
│           ├── values-night/           # Dark theme overrides
│           ├── drawable/              # Launcher icon (vector)
│           ├── mipmap-anydpi-v26/      # Adaptive launcher icon
│           └── xml/                    # Backup rules, data extraction rules
├── gradle/
│   ├── libs.versions.toml              # Version catalog (central deps)
│   └── wrapper/                        # Gradle wrapper
├── .github/workflows/build.yml         # CI: build APK, attach to releases
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── LICENSE                             # GPL-3.0
└── README.md                           # This file
```

---

## 🤝 Contributing

Pull requests welcome! Please:

1. Fork the repo and create a feature branch (`git checkout -b feat/my-feature`)
2. Make sure `./gradlew assembleDebug` still builds cleanly
3. Open a PR with a clear description of what changed and why

For major changes, please open an issue first to discuss what you'd like to change.

---

## 📜 License

FileRhythm is licensed under the **GNU General Public License v3.0** — see [LICENSE](LICENSE).

The Material 3 Expressive design language used here is inspired by (but does not copy code from) the [Rhythm music player](https://github.com/cromaguy/Rhythm) by Anjishnu Nandi, also GPL-3.0.

---

<div align="center">

**Made with 🧡 for the Android FOSS community**

</div>
