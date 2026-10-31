#!/bin/bash
# AeroScan v1.5 - Severe Weather PC Desktop Core Engine Launcher
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "========================================================================="
echo "   AEROSCAN v1.5 - SEVERE WEATHER DESKTOP ARCHITECTURAL SUITE (BUILD 7)"
echo "========================================================================="
echo " [1] GPU Vector Tiling: Locked 60 FPS Default"
echo " [2] SAT + AI Predictive: Optical Flow Neural Net + DBSCAN Clustering"
echo " [3] Ground-Truth Validation: BMKG Indonesia Telemetry Verification"
echo "========================================================================="

if command -v java >/dev/null 2>&1; then
    echo "Starting AeroScan Desktop GUI via Java runtime..."
    java -jar "$DIR/AeroScanDesktop.jar" &
fi

if command -v xdg-open >/dev/null 2>&1; then
    xdg-open "$DIR/index.html" >/dev/null 2>&1 &
elif command -v open >/dev/null 2>&1; then
    open "$DIR/index.html" >/dev/null 2>&1 &
fi

echo "AeroScan PC Version initialized successfully."
