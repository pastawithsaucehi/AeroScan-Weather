# AeroScan 2026 - Tactical Weather Scanner & Cyclone Tracker

A high-performance Android Jetpack Compose application designed for tracking tropical cyclones, super typhoons, gale-force winds, oceanic storm surges, and orbital weather satellites.

---

## 📱 How to Get the APK File

### Option 1: Direct Download from AI Studio (Recommended - No Building Required!)
1. In Google AI Studio, look at the **top-right corner** above the emulator preview.
2. Click the **Settings** (gear icon ⚙️) or **Export** menu.
3. Select **"Generate APK / AAB"** (or **"Export APK"**).
4. AI Studio will build and immediately provide a download link for `app-debug.apk` directly to your computer or phone!

> **Why wasn't the `.apk` in the "Versions" `.zip`?**  
> In Google AI Studio, the **Versions** menu creates **source code snapshots** (Kotlin, Gradle, and Android resources). To keep project history lightweight and fast, binary executables like `.apk` and `.aab` are intentionally stripped from version code archives. The dedicated **"Generate APK / AAB"** feature in the Settings menu is specifically designed for downloading ready-to-install binaries.

---

### Option 2: Build the APK Locally from this ZIP

If you downloaded the project source `.zip`:

#### On macOS / Linux:
Run the included build script in your terminal:
```bash
./build-apk.sh
```
Or with Gradle:
```bash
gradle assembleDebug
```
The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

#### On Windows:
Double-click `build-apk.bat` or run:
```cmd
build-apk.bat
```

#### In Android Studio:
1. Open this unzipped folder in **Android Studio**.
2. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
3. Click the **locate** popup when the build finishes.
