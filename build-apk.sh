#!/bin/bash
# AeroScan 2026 - Instant APK Build Script
set -e
echo "============================================="
echo "   AeroScan 2026: Building Debug APK...      "
echo "============================================="

if command -v ./gradlew &> /dev/null; then
    ./gradlew assembleDebug
elif command -v gradle &> /dev/null; then
    gradle assembleDebug
else
    echo "Error: Neither ./gradlew nor gradle was found on your system."
    echo "Please open this project in Android Studio or install Gradle."
    exit 1
fi

echo ""
echo "Build complete! Your APK is located at:"
echo "  app/build/outputs/apk/debug/app-debug.apk"
echo "============================================="
