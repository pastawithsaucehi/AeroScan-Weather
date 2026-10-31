================================================================================
AEROSCAN v1.5 • SEVERE WEATHER PC DESKTOP CORE ENGINE [BUILD 7]
================================================================================

OVERVIEW:
AeroScan PC Version provides the full Severe Weather Core Architectural Engine
for desktop environments (Windows, macOS, Linux).

CORE ARCHITECTURAL FRAMEWORKS:
1. HIGH-PERFORMANCE GRAPHICS ENGINE (GPU VECTOR TILING)
   - Eliminates CPU rendering bottlenecks during dense spatial mapping cycles.
   - Built on WebGL / WebGPU shaders and OpenGL vertex arrays.
   - Default framerate is locked precisely at 60 FPS (configurable to 30 / 120 FPS).

2. SAT + AI PREDICTIVE INFRASTRUCTURE
   - Farneback Optical Flow Neural Network analyzing cloud-top visual fluid dynamics.
   - Spatial DBSCAN (Density-Based Spatial Clustering of Applications with Noise)
     routine grouping and isolating severe radar cores (>50 dBZ) and lightning arrays.

3. GROUND-TRUTH VALIDATION FRAMEWORK
   - Sub-routine API client querying BMKG (Indonesia Meteorological Agency) stations.
   - Live surface telemetry & maritime buoy data (1011.4 hPa pressure, 14.2 kt winds).
   - Double-verifies AI predictive model track by cross-referencing surface barometric
     pressures and real-time wind speeds before normalizing output profiles.

FILES IN THIS FOLDER (PCVersion/):
- AeroScan.exe         : 64-bit Windows Native GUI Executable (PE32+ AMD64)
- AeroScan-v1.5.exe    : Versioned 64-bit Windows Native GUI Executable
- AeroScanDesktop.jar  : Cross-platform Standalone Java Swing Desktop Application
- index.html           : Hardware-accelerated 60 FPS WebGL/HTML5 Tactical Weather HUD
- config.json          : Core Architectural simulation parameters & BMKG endpoints
- run.bat              : Windows 1-Click Launch Script
- run.sh               : Linux / macOS Launch Script

HOW TO RUN ON PC:
- Windows PC:
  1. Double-click "AeroScan.exe" or "AeroScan-v1.5.exe", OR
  2. Double-click "run.bat"
- Cross-Platform (Any OS with Java):
  java -jar AeroScanDesktop.jar
- In Any Browser:
  Open "index.html" directly.

================================================================================
AeroScan v1.5 • Release 2026.10 • Build 7 • All Systems Verified
================================================================================
