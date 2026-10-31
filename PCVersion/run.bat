@echo off
title AeroScan v1.5 - Severe Weather PC Desktop Core Engine
color 0B
echo =========================================================================
echo    AEROSCAN v1.5 - SEVERE WEATHER DESKTOP ARCHITECTURAL SUITE (BUILD 7)
echo =========================================================================
echo  [1] GPU Vector Tiling: Locked 60 FPS Default (Dynamic 30/60/120 FPS)
echo  [2] SAT + AI Predictive: Optical Flow Neural Net + DBSCAN Clustering
echo  [3] Ground-Truth Validation: BMKG Indonesia Telemetry Verification
echo =========================================================================
echo.
echo Launching AeroScan Windows Native Executable (AeroScan.exe)...
start "" "%~dp0AeroScan.exe"

REM Also launch Java Desktop interface if Java is present
where java >nul 2>nul
if %errorlevel% equ 0 (
    echo Java runtime detected. Initializing AeroScan Desktop Swing Canvas...
    start "" javaw -jar "%~dp0AeroScanDesktop.jar"
) else (
    echo Opening WebGL/HTML5 Tactical Radar Canvas in Default Browser...
    start "" "%~dp0index.html"
)
echo.
echo AeroScan Desktop Subsystems Initialized.
