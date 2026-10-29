@echo off
REM AeroScan 2026 - Instant APK Build Script for Windows
echo =============================================
echo    AeroScan 2026: Building Debug APK...
echo =============================================

if exist "gradlew.bat" (
    call gradlew.bat assembleDebug
) else (
    call gradle assembleDebug
)

echo.
echo Build complete! Your APK is located at:
echo   app\build\outputs\apk\debug\app-debug.apk
echo =============================================
pause
