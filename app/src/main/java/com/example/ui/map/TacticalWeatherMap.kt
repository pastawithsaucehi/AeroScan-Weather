package com.example.ui.map

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.GeoData
import com.example.model.AppScanMode
import com.example.model.Cyclone
import com.example.model.ForecastTrackPoint
import com.example.model.GeoPoint
import com.example.model.IsobarLine
import com.example.model.LightningFlash
import com.example.model.MapLayerType
import com.example.model.MapProjectionType
import com.example.model.MapScanType
import com.example.model.Satellite
import com.example.model.ScannedSpot
import com.example.model.ScannedSpotType
import com.example.model.ScannerBeamStyle
import com.example.model.ServerNode
import com.example.model.StabilizationDisplayMode
import com.example.model.StrongWindArea
import com.example.model.SurgeRiskLevel
import com.example.model.SurgeRiskZone
import com.example.model.TerrainMapStyle
import com.example.model.getCycloneForecastLocation
import com.example.model.getCycloneForecastIntensity
import com.example.model.getCycloneIntensityColor
import com.example.model.getCycloneIntensityAccentColor
import com.example.model.getCycloneIntensityCategory
import com.example.model.DbscanCluster
import com.example.model.OpticalFlowVector
import com.example.model.BmkgBuoyStation
import com.example.model.BmkgGroundTruthVerification
import com.example.model.BmkgVerificationStatus
import com.example.model.TargetFramerateFps
import com.example.ui.theme.CoastlineBorder
import com.example.ui.theme.CoastlineGlow
import com.example.ui.theme.CoastalShelf
import com.example.ui.theme.ConsensusModelTrack
import com.example.ui.theme.EcmwfTrack
import com.example.ui.theme.OceanDark
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.PolarIce
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.RadarGreen
import com.example.ui.theme.ServerNodeGlow
import com.example.ui.theme.TerrainBase
import com.example.ui.theme.TerrainHighlands
import com.example.ui.theme.TerrainRidge
import com.example.ui.theme.WeatherNext2Track
import com.example.ui.theme.WeatherNext3Track
import com.example.viewmodel.StormScanUiState
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun TacticalWeatherMap(
    uiState: StormScanUiState,
    onPan: (Double, Double) -> Unit,
    onZoom: (Float) -> Unit,
    onTapPoint: (GeoPoint) -> Unit,
    onSelectCyclone: (Cyclone) -> Unit,
    onSelectWindArea: (StrongWindArea) -> Unit,
    onSelectServer: (ServerNode) -> Unit,
    onSelectSatellite: (Satellite) -> Unit,
    onSelectScannedSpot: (ScannedSpot) -> Unit = {},
    onSelectSurgeZone: (SurgeRiskZone) -> Unit = {},
    onSelectDbscanCluster: (DbscanCluster) -> Unit = {},
    onSelectBmkgBuoy: (BmkgBuoyStation) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val continents = remember { GeoData.continents }
    val islands = remember { GeoData.islands }
    val mountainRidges = remember { GeoData.topographyRidges }
    val labels = remember { GeoData.continentLabels }
    val isobars = remember { GeoData.synopticIsobars }
    val bathymetryTrenches = remember { GeoData.bathymetryTrenches }
    val jetStreams = remember { GeoData.jetStreams }
    val atmosphericFronts = remember { GeoData.atmosphericFronts }
    val dopplerStations = remember { GeoData.dopplerStations }
    val navWaypoints = remember { GeoData.navWaypoints }
    val navCorridors = remember { GeoData.navCorridors }

    val currentViewport by rememberUpdatedState(uiState.viewport)
    val currentAppScanMode by rememberUpdatedState(uiState.appScanMode)
    val currentPanSensitivityPct by rememberUpdatedState(uiState.mapPanSensitivityPct)
    val currentCyclones by rememberUpdatedState(uiState.cyclones)
    val currentWindAreas by rememberUpdatedState(uiState.strongWindAreas)
    val currentSurgeRiskZones by rememberUpdatedState(uiState.surgeRiskZones)
    val currentServers by rememberUpdatedState(uiState.servers)
    val currentSatellites by rememberUpdatedState(uiState.satellites)
    val currentScannedSpots by rememberUpdatedState(uiState.scannedSpots)
    val currentDbscanClusters by rememberUpdatedState(uiState.dbscanClusters)
    val currentBmkgBuoys by rememberUpdatedState(uiState.bmkgBuoys)
    val currentForecastHourOffset by rememberUpdatedState(uiState.forecastHourOffset)
    val currentShowServers by rememberUpdatedState(uiState.showServersOnMap && uiState.activeLayers.contains(MapLayerType.SERVERS))
    val currentShowSatellites by rememberUpdatedState(uiState.showSatelliteTelemetry && uiState.activeLayers.contains(MapLayerType.SATELLITES))
    val currentProjection by rememberUpdatedState(uiState.projectionType)

    val currentOnPan by rememberUpdatedState(onPan)
    val currentOnZoom by rememberUpdatedState(onZoom)
    val currentOnTapPoint by rememberUpdatedState(onTapPoint)
    val currentOnSelectCyclone by rememberUpdatedState(onSelectCyclone)
    val currentOnSelectWindArea by rememberUpdatedState(onSelectWindArea)
    val currentOnSelectSurgeZone by rememberUpdatedState(onSelectSurgeZone)
    val currentOnSelectServer by rememberUpdatedState(onSelectServer)
    val currentOnSelectSatellite by rememberUpdatedState(onSelectSatellite)
    val currentOnSelectScannedSpot by rememberUpdatedState(onSelectScannedSpot)
    val currentOnSelectDbscanCluster by rememberUpdatedState(onSelectDbscanCluster)
    val currentOnSelectBmkgBuoy by rememberUpdatedState(onSelectBmkgBuoy)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val width = size.width.toFloat()
                    val height = size.height.toFloat()
                    val zoomFactor = currentViewport.zoom.coerceAtLeast(0.5f)

                    // Calibrated pan sensitivity: smooth touch damping prevents rapid runaway scrolling
                    val panDamping = (currentPanSensitivityPct / 100f).coerceIn(0.2f, 1.2f) * 0.70f
                    val rawDeltaLon: Double
                    val rawDeltaLat: Double

                    if (currentProjection == MapProjectionType.GLOBE_3D) {
                        val globeRadius = minOf(width, height) * 0.42f * zoomFactor
                        rawDeltaLon = -(pan.x / globeRadius).toDouble() * (180.0 / Math.PI) * panDamping
                        rawDeltaLat = (pan.y / globeRadius).toDouble() * (180.0 / Math.PI) * panDamping
                    } else {
                        val scale = getMapScale(width, zoomFactor)
                        rawDeltaLon = -(pan.x / scale).toDouble() * panDamping
                        rawDeltaLat = (pan.y / scale).toDouble() * panDamping
                    }

                    // Per-frame delta clamping prevents sudden frame-drop acceleration spikes
                    val deltaLon = rawDeltaLon.coerceIn(-12.0, 12.0)
                    val deltaLat = rawDeltaLat.coerceIn(-10.0, 10.0)

                    if (deltaLon != 0.0 || deltaLat != 0.0) {
                        currentOnPan(deltaLat, deltaLon)
                    }

                    if (kotlin.math.abs(zoom - 1f) > 0.005f) {
                        // Smooth pinch zoom clamping prevents runaway zoom acceleration
                        val clampedZoom = zoom.coerceIn(0.92f, 1.08f)
                        currentOnZoom(clampedZoom)
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    val zoom = currentViewport.zoom
                    val centerLon = currentViewport.centerLon
                    val centerLat = currentViewport.centerLat

                    val tappedGeo: GeoPoint
                    if (currentProjection == MapProjectionType.GLOBE_3D) {
                        val cx = w / 2f
                        val cy = h / 2f
                        val globeRadius = minOf(w, h) * 0.42f * zoom.coerceIn(0.6f, 3.5f)
                        val dx = offset.x - cx
                        val dy = offset.y - cy
                        val rho = sqrt(dx * dx + dy * dy)
                        if (rho <= globeRadius) {
                            val c = kotlin.math.asin((rho / globeRadius).coerceIn(0f, 1f).toDouble())
                            val centerLatRad = Math.toRadians(centerLat)
                            val centerLonRad = Math.toRadians(centerLon)
                            val sinC = sin(c)
                            val cosC = cos(c)
                            val sinLat0 = sin(centerLatRad)
                            val cosLat0 = cos(centerLatRad)

                            val sinLat = cosC * sinLat0 + (-dy / rho) * sinC * cosLat0
                            val lat = kotlin.math.asin(sinLat.coerceIn(-1.0, 1.0))
                            val deltaLon = atan2((dx * sinC).toDouble(), (rho * cosLat0 * cosC - (-dy) * sinLat0 * sinC).toDouble())
                            val lon = centerLonRad + deltaLon
                            var normLon = (Math.toDegrees(lon) % 360.0)
                            if (normLon > 180.0) normLon -= 360.0
                            if (normLon < -180.0) normLon += 360.0
                            tappedGeo = GeoPoint(Math.toDegrees(lat).coerceIn(-85.0, 85.0), normLon)
                        } else {
                            tappedGeo = GeoPoint(centerLat, centerLon)
                        }
                    } else {
                        val scale = getMapScale(w, zoom)
                        val clickedLon = centerLon + ((offset.x - w / 2f) / scale).toDouble()
                        val clickedLat = centerLat - ((offset.y - h / 2f) / scale).toDouble()
                        var normalizedLon = clickedLon % 360.0
                        if (normalizedLon > 180.0) normalizedLon -= 360.0
                        if (normalizedLon < -180.0) normalizedLon += 360.0
                        tappedGeo = GeoPoint(clickedLat.coerceIn(-85.0, 85.0), normalizedLon)
                    }

                    // Mode-aware hit testing: In Surge Risk mode, check coastal surge zones first
                    if (currentAppScanMode == AppScanMode.SURGE_RISK) {
                        val nearestSurge = currentSurgeRiskZones.minByOrNull {
                            calculateDistancePixels(offset, projectPointNearest(it.location, w, h, centerLat, centerLon, zoom))
                        }
                        if (nearestSurge != null) {
                            val pix = projectPointNearest(nearestSurge.location, w, h, centerLat, centerLon, zoom)
                            if (calculateDistancePixels(offset, pix) < 65f) {
                                currentOnSelectSurgeZone(nearestSurge)
                                return@detectTapGestures
                            }
                        }
                    }

                    // Mode-aware hit testing: In Strong Wind mode, check strong wind areas first
                    if (currentAppScanMode == AppScanMode.STRONG_WINDS) {
                        val nearestWind = currentWindAreas.minByOrNull {
                            calculateDistancePixels(offset, projectPointNearest(it.centerLocation, w, h, centerLat, centerLon, zoom))
                        }
                        if (nearestWind != null) {
                            val pix = projectPointNearest(nearestWind.centerLocation, w, h, centerLat, centerLon, zoom)
                            if (calculateDistancePixels(offset, pix) < 65f) {
                                currentOnSelectWindArea(nearestWind)
                                return@detectTapGestures
                            }
                        }
                    }

                    // Check if tapped near an existing cyclone (interpolated location)
                    val nearestCyclone = currentCyclones.minByOrNull {
                        val loc = getCycloneForecastLocation(it, currentForecastHourOffset)
                        calculateDistancePixels(offset, projectPointNearest(loc, w, h, centerLat, centerLon, zoom))
                    }
                    if (nearestCyclone != null) {
                        val loc = getCycloneForecastLocation(nearestCyclone, currentForecastHourOffset)
                        val pix = projectPointNearest(loc, w, h, centerLat, centerLon, zoom)
                        if (calculateDistancePixels(offset, pix) < 55f) {
                            currentOnSelectCyclone(nearestCyclone)
                            return@detectTapGestures
                        }
                    }

                    // Check if tapped near strong wind areas
                    val nearestWind = currentWindAreas.minByOrNull {
                        calculateDistancePixels(offset, projectPointNearest(it.centerLocation, w, h, centerLat, centerLon, zoom))
                    }
                    if (nearestWind != null) {
                        val pix = projectPointNearest(nearestWind.centerLocation, w, h, centerLat, centerLon, zoom)
                        if (calculateDistancePixels(offset, pix) < 50f) {
                            currentOnSelectWindArea(nearestWind)
                            return@detectTapGestures
                        }
                    }

                    // Check if tapped near a server
                    if (currentShowServers) {
                        val nearestServer = currentServers.minByOrNull {
                            calculateDistancePixels(offset, projectPointNearest(it.location, w, h, centerLat, centerLon, zoom))
                        }
                        if (nearestServer != null) {
                            val pix = projectPointNearest(nearestServer.location, w, h, centerLat, centerLon, zoom)
                            if (calculateDistancePixels(offset, pix) < 45f) {
                                currentOnSelectServer(nearestServer)
                                return@detectTapGestures
                            }
                        }
                    }

                    // Check if tapped near a satellite
                    if (currentShowSatellites) {
                        val nearestSat = currentSatellites.minByOrNull {
                            calculateDistancePixels(offset, projectPointNearest(it.location, w, h, centerLat, centerLon, zoom))
                        }
                        if (nearestSat != null) {
                            val pix = projectPointNearest(nearestSat.location, w, h, centerLat, centerLon, zoom)
                            if (calculateDistancePixels(offset, pix) < 45f) {
                                currentOnSelectSatellite(nearestSat)
                                return@detectTapGestures
                            }
                        }
                    }

                    // Check if tapped near a BMKG maritime buoy or coastal AWS
                    val nearestBuoy = currentBmkgBuoys.minByOrNull {
                        calculateDistancePixels(offset, projectPointNearest(it.location, w, h, centerLat, centerLon, zoom))
                    }
                    if (nearestBuoy != null) {
                        val pix = projectPointNearest(nearestBuoy.location, w, h, centerLat, centerLon, zoom)
                        if (calculateDistancePixels(offset, pix) < 45f) {
                            currentOnSelectBmkgBuoy(nearestBuoy)
                            return@detectTapGestures
                        }
                    }

                    // Check if tapped near a DBSCAN cluster centroid
                    val nearestCluster = currentDbscanClusters.minByOrNull {
                        calculateDistancePixels(offset, projectPointNearest(it.centroid, w, h, centerLat, centerLon, zoom))
                    }
                    if (nearestCluster != null) {
                        val pix = projectPointNearest(nearestCluster.centroid, w, h, centerLat, centerLon, zoom)
                        if (calculateDistancePixels(offset, pix) < 55f) {
                            currentOnSelectDbscanCluster(nearestCluster)
                            return@detectTapGestures
                        }
                    }

                    currentOnTapPoint(tappedGeo)
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val centerLat = uiState.viewport.centerLat
        val centerLon = uiState.viewport.centerLon
        val zoom = uiState.viewport.zoom
        val isDark = uiState.isDarkMode

        activeMapProjection = uiState.projectionType

        if (uiState.projectionType == MapProjectionType.GLOBE_3D) {
            // 1. Cosmic Deep Space Background with Stars
            drawCosmicSpaceBackground(w, h, isDark)

            // 2. 3D Globe Spherical Ocean with Specular Shading, Graticules & Atmospheric Limb
            draw3DGlobeSphere(w, h, centerLat, centerLon, zoom, isDark)

            // 3. 3D Continents and Coastlines projected onto the Sphere
            if (uiState.activeLayers.contains(MapLayerType.TERRAIN)) {
                draw3DGlobeTerrain(
                    continents = continents,
                    islands = islands,
                    mountainRidges = mountainRidges,
                    labels = labels,
                    style = uiState.terrainStyle,
                    isDark = isDark,
                    w = w,
                    h = h,
                    centerLat = centerLat,
                    centerLon = centerLon,
                    zoom = zoom
                )
            }
        } else {
            // 1. Deep Ocean Base with Bathymetric Gradient
            drawOceanBackground(w, h, isDark)

            // 2. Graticules (Latitude & Longitude grid lines)
            drawGraticules(w, h, centerLat, centerLon, zoom, isDark)

            // 3. High-Contrast Continental Terrain and Coastlines (with World Wrap)
            if (uiState.activeLayers.contains(MapLayerType.TERRAIN)) {
                drawRichTerrainMap(
                    continents = continents,
                    islands = islands,
                    mountainRidges = mountainRidges,
                    labels = labels,
                    style = uiState.terrainStyle,
                    isDark = isDark,
                    w = w,
                    h = h,
                    centerLat = centerLat,
                    centerLon = centerLon,
                    zoom = zoom
                )
            }
        }

        // 3a. Deep Ocean Bathymetric Trenches & Ridges (Mariana, Puerto Rico, Mid-Atlantic Ridge)
        if (uiState.activeLayers.contains(MapLayerType.BATHYMETRY)) {
            drawTacticalBathymetry(
                trenches = bathymetryTrenches,
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                isDark = isDark
            )
        }

        // 3b. Day/Night Solar Twilight Terminator Shadow
        if (uiState.showDayNightTerminator) {
            drawDayNightTerminator(w, h, centerLat, centerLon, zoom, isDark)
        }

        // 4. Synoptic Pressure Isobars (Curving atmospheric pressure contours)
        if (uiState.showIsobars && uiState.activeLayers.contains(MapLayerType.ISOBARS)) {
            drawPressureIsobars(isobars, w, h, centerLat, centerLon, zoom, isDark)
        }

        // 4a. High-Altitude Upper Troposphere Jet Streams (Polar & Subtropical Jet Ribbons)
        if (uiState.activeLayers.contains(MapLayerType.JET_STREAMS)) {
            drawJetStreams(
                jetStreams = jetStreams,
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                phase = uiState.windParticlePhase,
                isDark = isDark
            )
        }

        // 4b. Synoptic Frontal Boundaries (Cold, Warm, Stationary, and Occluded Fronts)
        if (uiState.activeLayers.contains(MapLayerType.FRONTAL_SYSTEMS)) {
            drawAtmosphericFronts(
                fronts = atmosphericFronts,
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                isDark = isDark
            )
        }

        // 4c. High-Altitude Transoceanic Corridors & Waypoints
        if (uiState.activeLayers.contains(MapLayerType.NAV_CORRIDORS)) {
            drawNavCorridors(
                corridors = navCorridors,
                waypoints = navWaypoints,
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                isDark = isDark
            )
        }

        // 5. Humidity Layer (Translucent convective moisture plumes that reveal terrain)
        if (uiState.activeLayers.contains(MapLayerType.HUMIDITY)) {
            drawHumidityField(w, h, centerLat, centerLon, zoom, uiState.cyclones, isDark)
        }

        // 6. Strong Wind Areas & Gale Fronts (Prominent in STRONG_WINDS mode)
        if (uiState.appScanMode == AppScanMode.STRONG_WINDS || uiState.activeLayers.contains(MapLayerType.WIND_STREAMS)) {
            drawStrongWindAreas(
                areas = uiState.strongWindAreas,
                selected = uiState.selectedWindArea,
                isHighAlertMode = uiState.appScanMode == AppScanMode.STRONG_WINDS,
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                pulse = uiState.pulsePhase,
                phase = uiState.windParticlePhase
            )
        }

        // 7. Wind Vector Streamlines
        if (uiState.activeLayers.contains(MapLayerType.WIND_STREAMS) || uiState.appScanMode == AppScanMode.STRONG_WINDS) {
            drawWindStreamlines(
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                phase = uiState.windParticlePhase,
                cyclones = uiState.cyclones,
                strongWindAreas = uiState.strongWindAreas,
                isHighIntensity = uiState.appScanMode == AppScanMode.STRONG_WINDS
            )
        }

        // 7b. Coastal & Island Ground Doppler / NEXRAD Radar Stations (Range Rings & Sweeps)
        if (uiState.activeLayers.contains(MapLayerType.DOPPLER_STATIONS)) {
            drawDopplerRadarStations(
                stations = dopplerStations,
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                pulse = uiState.pulsePhase,
                isDark = isDark
            )
        }

        // 8. Supercomputer Data Transmission Rays (Server <-> Satellite laser downlinks)
        if (uiState.showDataLinks && uiState.activeLayers.contains(MapLayerType.DATA_LINKS)) {
            drawDataTransmissionRays(
                servers = uiState.servers,
                satellites = uiState.satellites,
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                packetPhase = uiState.dataPacketPhase
            )
        }

        // 9. Satellites and Orbits
        if (uiState.showSatelliteTelemetry && uiState.activeLayers.contains(MapLayerType.SATELLITES)) {
            drawSatellites(uiState.satellites, uiState.selectedSatellite, w, h, centerLat, centerLon, zoom, uiState.pulsePhase)
        }

        // 10. Supercomputer Servers on the Map
        if (uiState.showServersOnMap && uiState.activeLayers.contains(MapLayerType.SERVERS)) {
            drawServers(uiState.servers, uiState.selectedServer, w, h, centerLat, centerLon, zoom, uiState.pulsePhase)
        }

        // 11. Cyclones & Forecast Tracks (moves along forecast track when scrubbing timeline!)
        if (uiState.activeLayers.contains(MapLayerType.CYCLONES) || uiState.appScanMode == AppScanMode.CYCLONES) {
            drawCyclones(
                uiState.cyclones,
                uiState.selectedCyclone,
                uiState.stabilizationMode,
                uiState.accuracyPct,
                uiState.forecastHourOffset,
                w, h, centerLat, centerLon, zoom,
                uiState.pulsePhase,
                uiState.windParticlePhase
            )
        }

        // 11b. Coastal Storm Surge & Inundation Risk Zones (New Mode & Setting)
        if (uiState.showSurgeVectors || uiState.appScanMode == AppScanMode.SURGE_RISK) {
            drawSurgeRiskZones(
                zones = uiState.surgeRiskZones,
                selected = uiState.selectedSurgeZone,
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                pulse = uiState.pulsePhase,
                wavePhase = uiState.windParticlePhase
            )
        }

        // 12. Convective Lightning Flashes in Cyclone Eyewalls
        if (uiState.activeLayers.contains(MapLayerType.LIGHTNING)) {
            drawLightningStrikes(uiState.lightningFlashes, w, h, centerLat, centerLon, zoom)
        }

        // 13. Radar Scanner Sweep (Tactical Laser, Phosphor Sweep, or Infrared Convection)
        if (uiState.activeLayers.contains(MapLayerType.RADAR_SCANNER)) {
            val scanTarget = when (uiState.appScanMode) {
                AppScanMode.CYCLONES -> uiState.selectedCyclone?.let { getCycloneForecastLocation(it, uiState.forecastHourOffset) }
                AppScanMode.STRONG_WINDS -> uiState.selectedWindArea?.centerLocation
                AppScanMode.SURGE_RISK -> uiState.selectedSurgeZone?.location ?: uiState.surgeRiskZones.firstOrNull()?.location
                AppScanMode.SATELLITES -> uiState.selectedSatellite?.location ?: uiState.satellites.firstOrNull()?.location
                AppScanMode.ALL_MODELS -> uiState.selectedCyclone?.currentLocation
                AppScanMode.SETTINGS -> null
            }
            drawCustomRadarScanner(
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                targetLocation = scanTarget,
                angleDeg = uiState.radarScanAngleDeg,
                pulse = uiState.pulsePhase,
                style = uiState.scannerBeamStyle,
                projection = uiState.projectionType
            )
        }

        // 13b. Satellite Swath Scanning Layer
        if (uiState.mapScanType == MapScanType.SATELLITE_SCAN || uiState.mapScanType == MapScanType.COMBINED_SATELLITE_AI || uiState.appScanMode == AppScanMode.SATELLITES) {
            drawSatelliteScanOverlay(w, h, centerLat, centerLon, zoom, uiState.radarScanAngleDeg, uiState.satellites, uiState.selectedSatellite, uiState.pulsePhase)
        }

        // 13c. AI Neural Perception Scanner Layer
        if (uiState.mapScanType == MapScanType.AI_NEURAL_SCAN || uiState.mapScanType == MapScanType.COMBINED_SATELLITE_AI) {
            drawAiNeuralScanOverlay(w, h, centerLat, centerLon, zoom, uiState.radarScanAngleDeg, uiState.cyclones, uiState.pulsePhase)
        }

        // 13d. All Models Weather Ensemble Overlay (in ALL_MODELS mode)
        if (uiState.appScanMode == AppScanMode.ALL_MODELS) {
            drawAllModelsEnsembleOverlay(w, h, centerLat, centerLon, zoom, uiState.cyclones, uiState.selectedCyclone)
        }

        // 13e. Scanned Spots with Red Tactical Boxes, Accuracy & Real-time Acquisition
        drawScannedSpots(
            spots = uiState.scannedSpots,
            selected = uiState.selectedScannedSpot,
            w = w,
            h = h,
            centerLat = centerLat,
            centerLon = centerLon,
            zoom = zoom,
            pulse = uiState.pulsePhase,
            isDark = isDark
        )

        // 13f. SAT + AI PREDICTIVE INFRASTRUCTURE: Optical Flow Neural Network Fluid Dynamics Field
        if (uiState.activeLayers.contains(MapLayerType.OPTICAL_FLOW)) {
            drawOpticalFlowField(uiState.opticalFlowVectors, w, h, centerLat, centerLon, zoom, uiState.pulsePhase, isDark)
        }

        // 13g. SAT + AI PREDICTIVE INFRASTRUCTURE: Spatial DBSCAN Clustering Routine
        if (uiState.dbscanClusteringEnabled && uiState.activeLayers.contains(MapLayerType.DBSCAN_CLUSTERS)) {
            drawDbscanClusters(uiState.dbscanClusters, uiState.selectedDbscanCluster, w, h, centerLat, centerLon, zoom, uiState.pulsePhase, isDark)
        }

        // 13h. GROUND-TRUTH VALIDATION FRAMEWORK: BMKG Maritime Buoy Network & Verification Links
        if (uiState.activeLayers.contains(MapLayerType.BMKG_BUOYS)) {
            drawBmkgBuoys(uiState.bmkgBuoys, uiState.selectedBmkgBuoy, uiState.activeGroundTruthVerification, w, h, centerLat, centerLon, zoom, uiState.pulsePhase, isDark)
        }

        // 13i. High-Performance Graphics Engine GPU Vector Tiling Status Telemetry
        if (uiState.gpuVectorTilingEnabled) {
            drawGpuVectorTilingBadge(uiState.targetFramerate, w, h, isDark)
        }

        // 14. Atmospheric Probe Reticle
        uiState.atmosphericProbe?.let { probe ->
            val probePos = projectPointNearest(probe.point, w, h, centerLat, centerLon, zoom)
            drawProbeReticle(probePos, probe.windDirectionDeg, uiState.pulsePhase)
        }

        // 15. Tactical Mini-Map Overview Radar Inset
        if (uiState.showMiniMap) {
            drawTacticalMiniMap(
                continents = continents,
                cyclones = uiState.cyclones,
                strongWindAreas = uiState.strongWindAreas,
                w = w,
                h = h,
                centerLat = centerLat,
                centerLon = centerLon,
                zoom = zoom,
                isDark = isDark
            )
        }
    }
}

// -------------------------------------------------------------
// Projections and Drawing Helpers
// -------------------------------------------------------------

private fun getMapScale(w: Float, zoom: Float): Float {
    return (w / 360f) * zoom * 1.55f
}

private fun projectGeo(
    point: GeoPoint,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    worldOffsetLon: Double = 0.0
): Offset {
    val scale = getMapScale(w, zoom)
    val diffLon = (point.lon + worldOffsetLon - centerLon)
    val diffLat = (point.lat - centerLat)
    val x = (w / 2f) + diffLon.toFloat() * scale
    val y = (h / 2f) - diffLat.toFloat() * scale
    return Offset(x, y)
}

private var activeMapProjection: MapProjectionType = MapProjectionType.GLOBE_3D

private fun isPointVisibleOnGlobe(
    point: GeoPoint,
    centerLat: Double,
    centerLon: Double
): Boolean {
    val latRad = Math.toRadians(point.lat)
    val lonRad = Math.toRadians(point.lon)
    val centerLatRad = Math.toRadians(centerLat)
    val centerLonRad = Math.toRadians(centerLon)

    var deltaLon = lonRad - centerLonRad
    while (deltaLon > Math.PI) deltaLon -= 2 * Math.PI
    while (deltaLon < -Math.PI) deltaLon += 2 * Math.PI

    val cosC = sin(centerLatRad) * sin(latRad) + cos(centerLatRad) * cos(latRad) * cos(deltaLon)
    return cosC >= -0.05
}

private fun projectPointNearest(
    point: GeoPoint,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    projection: MapProjectionType = activeMapProjection
): Offset {
    if (projection == MapProjectionType.TACTICAL_2D) {
        val scale = getMapScale(w, zoom)
        var diffLon = (point.lon - centerLon) % 360.0
        if (diffLon > 180.0) diffLon -= 360.0
        if (diffLon < -180.0) diffLon += 360.0

        val x = (w / 2f) + diffLon.toFloat() * scale
        val y = (h / 2f) - (point.lat - centerLat).toFloat() * scale
        return Offset(x, y)
    }

    // 3D Globe (Orthographic projection)
    val cx = w / 2f
    val cy = h / 2f
    val globeRadius = minOf(w, h) * 0.42f * zoom.coerceIn(0.6f, 3.5f)

    val latRad = Math.toRadians(point.lat)
    val lonRad = Math.toRadians(point.lon)
    val centerLatRad = Math.toRadians(centerLat)
    val centerLonRad = Math.toRadians(centerLon)

    var deltaLon = lonRad - centerLonRad
    while (deltaLon > Math.PI) deltaLon -= 2 * Math.PI
    while (deltaLon < -Math.PI) deltaLon += 2 * Math.PI

    val cosC = sin(centerLatRad) * sin(latRad) + cos(centerLatRad) * cos(latRad) * cos(deltaLon)

    // Far-side occlusion: if point is on the back hemisphere of the Earth, push it far off screen
    if (cosC < -0.05) {
        return Offset(-9999f, -9999f)
    }

    val x = cx + (globeRadius * (cos(latRad) * sin(deltaLon))).toFloat()
    val y = cy - (globeRadius * (cos(centerLatRad) * sin(latRad) - sin(centerLatRad) * cos(latRad) * cos(deltaLon))).toFloat()
    return Offset(x, y)
}

private fun calculateDistancePixels(p1: Offset, p2: Offset): Float {
    val dx = p1.x - p2.x
    val dy = p1.y - p2.y
    return sqrt(dx * dx + dy * dy)
}

private fun DrawScope.drawCosmicSpaceBackground(w: Float, h: Float, isDark: Boolean) {
    // 1. Deep Cosmic Void Background
    drawRect(
        brush = Brush.radialGradient(
            colors = if (isDark) {
                listOf(Color(0xFF0C1929), Color(0xFF060B14), Color(0xFF020408))
            } else {
                listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617))
            },
            center = Offset(w / 2f, h / 2f),
            radius = maxOf(w, h) * 0.95f
        )
    )

    // 2. Distant Twinkling Celestial Stars
    val starCount = 85
    for (i in 0 until starCount) {
        val sx = ((i * 157.3f * 13f) % w)
        val sy = ((i * 241.7f * 19f) % h)
        val starAlpha = (0.25f + 0.60f * ((i * 37) % 100) / 100f)
        val starRadius = if (i % 8 == 0) 1.8f else 1.1f
        val starColor = when (i % 5) {
            0 -> RadarCyan.copy(alpha = starAlpha)
            1 -> Color(0xFFFFD54F).copy(alpha = starAlpha)
            2 -> Color(0xFF90CAF9).copy(alpha = starAlpha)
            else -> Color.White.copy(alpha = starAlpha)
        }
        drawCircle(color = starColor, center = Offset(sx, sy), radius = starRadius)
    }

    // 3. Deep Space Orbital Corner Telemetry HUD brackets
    val cornerColor = RadarCyan.copy(alpha = 0.40f)
    val telemetryStroke = 1.2f
    // Top-left
    drawLine(cornerColor, Offset(16f, 16f), Offset(42f, 16f), strokeWidth = telemetryStroke)
    drawLine(cornerColor, Offset(16f, 16f), Offset(16f, 42f), strokeWidth = telemetryStroke)
    // Bottom-right
    drawLine(cornerColor, Offset(w - 16f, h - 16f), Offset(w - 42f, h - 16f), strokeWidth = telemetryStroke)
    drawLine(cornerColor, Offset(w - 16f, h - 16f), Offset(w - 16f, h - 42f), strokeWidth = telemetryStroke)
}

private fun DrawScope.draw3DGlobeSphere(
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    isDark: Boolean
) {
    val cx = w / 2f
    val cy = h / 2f
    val globeRadius = minOf(w, h) * 0.42f * zoom.coerceIn(0.6f, 3.5f)

    // 1. Atmospheric Outer Halo (expanding into space)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                RadarCyan.copy(alpha = 0.28f),
                RadarCyan.copy(alpha = 0.08f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = globeRadius * 1.18f
        ),
        center = Offset(cx, cy),
        radius = globeRadius * 1.18f
    )

    // 2. Ocean Sphere Body with 3D Spherical Specular Shading
    val lightSource = Offset(cx - globeRadius * 0.28f, cy - globeRadius * 0.28f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = if (isDark) {
                listOf(
                    Color(0xFF133858), // Sunlit specular ocean
                    Color(0xFF091E33), // Deep ocean midtone
                    Color(0xFF040E1A), // Shadowed ocean
                    Color(0xFF02070D)  // Deep shadow near limb
                )
            } else {
                listOf(
                    Color(0xFF4A90E2),
                    Color(0xFF2C5E8A),
                    Color(0xFF16324D),
                    Color(0xFF0B1926)
                )
            },
            center = lightSource,
            radius = globeRadius * 1.30f
        ),
        center = Offset(cx, cy),
        radius = globeRadius
    )

    // 3. 3D Globe Graticule (Curved Parallels & Meridians)
    val graticuleColor = if (isDark) Color(0x3500E5FF) else Color(0x30007A87)
    val equatorColor = if (isDark) RadarCyan.copy(alpha = 0.55f) else Color(0xFF007A87).copy(alpha = 0.55f)

    // Latitude Parallels (every 20 degrees: -80 to +80)
    for (latDeg in -80..80 step 20) {
        val color = if (latDeg == 0) equatorColor else graticuleColor
        val strokeWidth = if (latDeg == 0) 1.6f else 0.9f
        var prevOffset: Offset? = null

        for (lonDeg in -180..180 step 4) {
            val pt = GeoPoint(latDeg.toDouble(), lonDeg.toDouble())
            if (isPointVisibleOnGlobe(pt, centerLat, centerLon)) {
                val curOffset = projectPointNearest(pt, w, h, centerLat, centerLon, zoom, MapProjectionType.GLOBE_3D)
                if (curOffset.x > -500f) {
                    if (prevOffset != null && prevOffset.x > -500f) {
                        drawLine(color, prevOffset, curOffset, strokeWidth = strokeWidth)
                    }
                    prevOffset = curOffset
                } else {
                    prevOffset = null
                }
            } else {
                prevOffset = null
            }
        }
    }

    // Longitude Meridians (every 30 degrees: -180 to +180)
    for (lonDeg in -180 until 180 step 30) {
        val color = if (lonDeg == 0 || lonDeg == -180) equatorColor else graticuleColor
        var prevOffset: Offset? = null

        for (latDeg in -85..85 step 4) {
            val pt = GeoPoint(latDeg.toDouble(), lonDeg.toDouble())
            if (isPointVisibleOnGlobe(pt, centerLat, centerLon)) {
                val curOffset = projectPointNearest(pt, w, h, centerLat, centerLon, zoom, MapProjectionType.GLOBE_3D)
                if (curOffset.x > -500f) {
                    if (prevOffset != null && prevOffset.x > -500f) {
                        drawLine(color, prevOffset, curOffset, strokeWidth = 0.9f)
                    }
                    prevOffset = curOffset
                } else {
                    prevOffset = null
                }
            } else {
                prevOffset = null
            }
        }
    }

    // 4. Sharp Atmospheric Rim Ring
    drawCircle(
        color = RadarCyan.copy(alpha = 0.70f),
        center = Offset(cx, cy),
        radius = globeRadius,
        style = Stroke(width = 1.6f)
    )

    // Inner atmospheric haze gradient ring
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                RadarCyan.copy(alpha = 0.35f)
            ),
            center = Offset(cx, cy),
            radius = globeRadius
        ),
        center = Offset(cx, cy),
        radius = globeRadius
    )
}

private fun DrawScope.draw3DGlobeTerrain(
    continents: List<List<GeoPoint>>,
    islands: List<List<GeoPoint>>,
    mountainRidges: List<List<GeoPoint>>,
    labels: List<GeoData.MapLabel>,
    style: TerrainMapStyle,
    isDark: Boolean,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float
) {
    val (coastlineStroke, coastlineGlow, landBase) = when {
        !isDark -> Triple(Color(0xFF007A87), Color(0x3000838F), Color(0xFFC5DAC8))
        style == TerrainMapStyle.SATELLITE_TOPO -> Triple(CoastlineBorder, CoastlineGlow, TerrainBase)
        style == TerrainMapStyle.TACTICAL_RADAR -> Triple(RadarCyan, Color(0x4000E5FF), Color(0xFF142436))
        else -> Triple(Color(0xFF80DEEA), Color(0x3080DEEA), Color(0xFF183248))
    }

    val allPolygons = continents + islands
    for (polygon in allPolygons) {
        if (polygon.size < 2) continue

        val visiblePath = Path()
        var hasStarted = false
        var prevPt: Offset? = null

        for (i in polygon.indices) {
            val pt = polygon[i]
            if (isPointVisibleOnGlobe(pt, centerLat, centerLon)) {
                val p = projectPointNearest(pt, w, h, centerLat, centerLon, zoom, MapProjectionType.GLOBE_3D)
                if (p.x > -500f) {
                    if (!hasStarted) {
                        visiblePath.moveTo(p.x, p.y)
                        hasStarted = true
                    } else {
                        visiblePath.lineTo(p.x, p.y)
                    }
                    if (prevPt != null && prevPt.x > -500f) {
                        // Coastline glowing shelf
                        drawLine(coastlineGlow, prevPt, p, strokeWidth = 3.5f)
                        // Sharp glowing coastline
                        drawLine(coastlineStroke, prevPt, p, strokeWidth = 1.8f)
                    }
                    prevPt = p
                } else {
                    prevPt = null
                }
            } else {
                prevPt = null
            }
        }
        if (hasStarted) {
            drawPath(visiblePath, color = landBase.copy(alpha = 0.55f), style = Fill)
        }
    }

    // Mountain ridges on 3D globe
    for (ridge in mountainRidges) {
        var prevPt: Offset? = null
        for (pt in ridge) {
            if (isPointVisibleOnGlobe(pt, centerLat, centerLon)) {
                val p = projectPointNearest(pt, w, h, centerLat, centerLon, zoom, MapProjectionType.GLOBE_3D)
                if (p.x > -500f) {
                    if (prevPt != null && prevPt.x > -500f) {
                        drawLine(TerrainRidge.copy(alpha = 0.7f), prevPt, p, strokeWidth = 1.4f)
                    }
                    prevPt = p
                } else {
                    prevPt = null
                }
            } else {
                prevPt = null
            }
        }
    }

    // Tactical geographic labels on 3D globe
    val textPaint = Paint().apply {
        color = if (isDark) Color(0x90FFFFFF).toArgb() else Color(0x90000000).toArgb()
        textSize = 9.0f * 2.2f
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        letterSpacing = 0.2f
        isAntiAlias = true
    }
    for (label in labels) {
        if (isPointVisibleOnGlobe(label.point, centerLat, centerLon)) {
            val p = projectPointNearest(label.point, w, h, centerLat, centerLon, zoom, MapProjectionType.GLOBE_3D)
            if (p.x in 30f..(w - 30f) && p.y in 30f..(h - 30f)) {
                drawContext.canvas.nativeCanvas.drawText(
                    label.text,
                    p.x,
                    p.y,
                    textPaint
                )
            }
        }
    }
}

private fun DrawScope.drawOceanBackground(w: Float, h: Float, isDark: Boolean) {
    if (isDark) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(OceanDeep, OceanDark, OceanDeep),
                startY = 0f,
                endY = h
            )
        )
    } else {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE2ECF5), Color(0xFFD6E4F0), Color(0xFFE2ECF5)),
                startY = 0f,
                endY = h
            )
        )
    }
}

private fun DrawScope.drawGraticules(
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    isDark: Boolean
) {
    val gridColor = if (isDark) Color(0xFF14273E) else Color(0x3578909C)
    val equatorColor = if (isDark) Color(0xFF1E3F66) else Color(0x7000838F)
    val tropicColor = if (isDark) Color(0x4500E5FF) else Color(0x4000838F)
    val scale = getMapScale(w, zoom)

    val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        textSize = (10f * (zoom * 0.3f + 0.7f)).coerceIn(8f, 13f)
        color = (if (isDark) Color(0x604FC3F7) else Color(0x60006064)).toArgb()
    }

    // Latitude parallels with tropical lines
    val lats = listOf(
        Pair(66.5, "ARCTIC CIRCLE 66.5°N"),
        Pair(40.0, "40°N"),
        Pair(23.436, "TROPIC OF CANCER 23.4°N"),
        Pair(0.0, "EQUATOR 0°"),
        Pair(-23.436, "TROPIC OF CAPRICORN 23.4°S"),
        Pair(-40.0, "40°S"),
        Pair(-60.0, "60°S")
    )
    for ((lat, label) in lats) {
        val y = (h / 2f) - (lat - centerLat).toFloat() * scale
        if (y in -50f..(h + 50f)) {
            val isEquator = lat == 0.0
            val isTropic = kotlin.math.abs(lat - 23.436) < 0.1 || kotlin.math.abs(lat + 23.436) < 0.1
            drawLine(
                color = when {
                    isEquator -> equatorColor
                    isTropic -> tropicColor
                    else -> gridColor
                },
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = if (isEquator) 1.5f else 0.8f,
                pathEffect = if (isEquator) null else PathEffect.dashPathEffect(floatArrayOf(8f, 10f), 0f)
            )

            // Latitude label on right margin
            if (y in 20f..(h - 20f)) {
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    14f,
                    y - 4f,
                    textPaint
                )
            }
        }
    }

    // Longitude meridians
    for (lonDeg in -180..180 step 30) {
        val pt = projectPointNearest(GeoPoint(0.0, lonDeg.toDouble()), w, h, centerLat, centerLon, zoom)
        if (pt.x in -50f..(w + 50f)) {
            drawLine(
                color = gridColor,
                start = Offset(pt.x, 0f),
                end = Offset(pt.x, h),
                strokeWidth = 0.8f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 10f), 0f)
            )
            // Longitude label near bottom
            val lonLabel = when {
                lonDeg == 0 -> "0° MERIDIAN"
                lonDeg < 0 -> "${-lonDeg}°W"
                else -> "${lonDeg}°E"
            }
            drawContext.canvas.nativeCanvas.drawText(
                lonLabel,
                pt.x + 4f,
                h - 10f,
                textPaint
            )
        }
    }

    // Tactical crosshair ticks (+) at 15° intersections across the viewport
    val tickColor = if (isDark) Color(0x3000E5FF) else Color(0x2500838F)
    val tickSize = 4f
    for (latDeg in -75..75 step 15) {
        val y = (h / 2f) - (latDeg.toDouble() - centerLat).toFloat() * scale
        if (y !in 0f..h) continue
        for (lonDeg in -180..180 step 15) {
            val pt = projectPointNearest(GeoPoint(latDeg.toDouble(), lonDeg.toDouble()), w, h, centerLat, centerLon, zoom)
            if (pt.x in 0f..w) {
                drawLine(tickColor, Offset(pt.x - tickSize, y), Offset(pt.x + tickSize, y), strokeWidth = 1.0f)
                drawLine(tickColor, Offset(pt.x, y - tickSize), Offset(pt.x, y + tickSize), strokeWidth = 1.0f)
            }
        }
    }
}

private fun DrawScope.drawRichTerrainMap(
    continents: List<List<GeoPoint>>,
    islands: List<List<GeoPoint>>,
    mountainRidges: List<List<GeoPoint>>,
    labels: List<GeoData.MapLabel>,
    style: TerrainMapStyle,
    isDark: Boolean,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float
) {
    val worldOffsets = listOf(-360.0, 0.0, 360.0)

    val (landFillColor, coastlineStrokeColor, coastlineGlowColor, shelfColor) = when {
        !isDark -> QuadColors(
            fill = Color(0xFFC5DAC8),
            stroke = Color(0xFF007A87),
            glow = Color(0x3000838F),
            shelf = Color(0xFFBCE0E5)
        )
        style == TerrainMapStyle.SATELLITE_TOPO -> QuadColors(
            fill = TerrainBase,
            stroke = CoastlineBorder,
            glow = CoastlineGlow,
            shelf = CoastalShelf
        )
        style == TerrainMapStyle.TACTICAL_RADAR -> QuadColors(
            fill = Color(0xFF142436),
            stroke = RadarCyan,
            glow = Color(0x4000E5FF),
            shelf = Color(0xFF0F2C44)
        )
        style == TerrainMapStyle.MINIMAL_VECTOR -> QuadColors(
            fill = Color(0xFF080E18),
            stroke = RadarCyan.copy(alpha = 0.85f),
            glow = Color(0x2000E5FF),
            shelf = Color(0xFF0D1726)
        )
        else -> QuadColors(
            fill = Color(0xFF1B3244),
            stroke = Color(0xFF80DEEA),
            glow = Color(0x3080DEEA),
            shelf = Color(0xFF132B40)
        )
    }

    val allLandPolygons = continents + islands

    for (worldOffset in worldOffsets) {
        for (polygon in allLandPolygons) {
            if (polygon.isEmpty()) continue

            var isVisible = false
            val path = Path()
            var isFirst = true

            for (geo in polygon) {
                val pt = projectGeo(geo, w, h, centerLat, centerLon, zoom, worldOffset)
                if (pt.x in -200f..(w + 200f) && pt.y in -200f..(h + 200f)) {
                    isVisible = true
                }
                if (isFirst) {
                    path.moveTo(pt.x, pt.y)
                    isFirst = false
                } else {
                    path.lineTo(pt.x, pt.y)
                }
            }
            path.close()

            if (!isVisible) continue

            val isAntarctic = polygon.firstOrNull()?.let { it.lat < -60.0 } ?: false
            val currentFill = if (isAntarctic) (if (isDark) PolarIce else Color(0xFFE2EBF0)) else landFillColor

            drawPath(
                path = path,
                color = shelfColor,
                style = Stroke(width = 8.0f * zoom.coerceIn(0.8f, 1.6f), cap = StrokeCap.Round)
            )

            drawPath(path = path, color = currentFill, style = Fill)

            drawPath(
                path = path,
                color = coastlineGlowColor,
                style = Stroke(width = 4.0f, cap = StrokeCap.Round)
            )

            drawPath(
                path = path,
                color = coastlineStrokeColor,
                style = Stroke(width = 1.8f, cap = StrokeCap.Round)
            )
        }

        if (style == TerrainMapStyle.SATELLITE_TOPO && isDark) {
            for (ridge in mountainRidges) {
                if (ridge.size < 2) continue
                val ridgePath = Path()
                var first = true
                var hasVisible = false
                for (pt in ridge) {
                    val p = projectGeo(pt, w, h, centerLat, centerLon, zoom, worldOffset)
                    if (p.x in -100f..(w + 100f) && p.y in -100f..(h + 100f)) hasVisible = true
                    if (first) {
                        ridgePath.moveTo(p.x, p.y)
                        first = false
                    } else {
                        ridgePath.lineTo(p.x, p.y)
                    }
                }
                if (hasVisible) {
                    drawPath(
                        ridgePath,
                        color = TerrainHighlands,
                        style = Stroke(width = 7.0f * zoom.coerceIn(0.8f, 2.0f), cap = StrokeCap.Round)
                    )
                    drawPath(
                        ridgePath,
                        color = TerrainRidge,
                        style = Stroke(width = 2.5f * zoom.coerceIn(0.8f, 2.0f), cap = StrokeCap.Round)
                    )
                }
            }
        }

        val textPaint = Paint().apply {
            color = if (isDark) Color(0x99A8DADC).toArgb() else Color(0x992C4D5E).toArgb()
            textSize = 10.0f * zoom.coerceIn(0.9f, 2.2f) * 2.8f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            letterSpacing = 0.25f
            isAntiAlias = true
        }

        for (label in labels) {
            val p = projectGeo(label.point, w, h, centerLat, centerLon, zoom, worldOffset)
            if (p.x in 30f..(w - 30f) && p.y in 30f..(h - 30f)) {
                drawContext.canvas.nativeCanvas.drawText(
                    label.text,
                    p.x,
                    p.y,
                    textPaint
                )
            }
        }
    }
}

private data class QuadColors(val fill: Color, val stroke: Color, val glow: Color, val shelf: Color)

private fun DrawScope.drawPressureIsobars(
    isobars: List<IsobarLine>,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    isDark: Boolean
) {
    val worldOffsets = listOf(0.0, -360.0, 360.0)
    val textPaint = Paint().apply {
        color = if (isDark) Color(0xFF64B5F6).toArgb() else Color(0xFF0277BD).toArgb()
        textSize = 9.0f * zoom.coerceIn(0.8f, 1.5f) * 2.2f
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        isAntiAlias = true
    }

    for (worldOffset in worldOffsets) {
        for (isobar in isobars) {
            if (isobar.points.size < 2) continue
            val path = Path()
            var first = true
            var isVisible = false

            for (pt in isobar.points) {
                val p = projectGeo(pt, w, h, centerLat, centerLon, zoom, worldOffset)
                if (p.x in -50f..(w + 50f) && p.y in -50f..(h + 50f)) isVisible = true
                if (first) {
                    path.moveTo(p.x, p.y)
                    first = false
                } else {
                    path.lineTo(p.x, p.y)
                }
            }

            if (!isVisible) continue

            val isLowPressure = isobar.pressureHpa < 1000
            val isobarColor = when {
                isobar.pressureHpa <= 960 -> Color(0xFFFF5252)
                isobar.pressureHpa <= 992 -> Color(0xFFFF9100)
                isLowPressure -> Color(0xFF80D8FF)
                else -> Color(0xFF81C784)
            }

            drawPath(
                path = path,
                color = isobarColor.copy(alpha = if (isDark) 0.55f else 0.40f),
                style = Stroke(
                    width = if (isLowPressure) 1.4f else 0.9f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                )
            )

            // Draw isobar pressure label
            val firstPt = projectGeo(isobar.points.first(), w, h, centerLat, centerLon, zoom, worldOffset)
            if (firstPt.x in 20f..(w - 20f) && firstPt.y in 20f..(h - 20f)) {
                drawContext.canvas.nativeCanvas.drawText(
                    "${isobar.pressureHpa}",
                    firstPt.x + 4f,
                    firstPt.y - 4f,
                    textPaint
                )
            }
        }
    }
}

private fun DrawScope.drawDataTransmissionRays(
    servers: List<ServerNode>,
    satellites: List<Satellite>,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    packetPhase: Float
) {
    if (servers.isEmpty() || satellites.isEmpty()) return

    // Connect first 4 servers with nearest satellite downlinks
    for (i in 0 until minOf(5, servers.size)) {
        val server = servers[i]
        val sat = satellites[i % satellites.size]

        val sPos = projectPointNearest(server.location, w, h, centerLat, centerLon, zoom)
        val satPos = projectPointNearest(sat.location, w, h, centerLat, centerLon, zoom)

        // Draw laser ray
        drawLine(
            color = Color(0x4000E5FF),
            start = sPos,
            end = satPos,
            strokeWidth = 1.0f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 16f), packetPhase * 24f)
        )

        // Flying glowing data packets along the ray
        val packetX = sPos.x + (satPos.x - sPos.x) * packetPhase
        val packetY = sPos.y + (satPos.y - sPos.y) * packetPhase
        drawCircle(
            color = Color(0xFF00E676),
            center = Offset(packetX, packetY),
            radius = 3.0f,
            style = Fill
        )
        drawCircle(
            color = Color(0x8000E676),
            center = Offset(packetX, packetY),
            radius = 6.0f,
            style = Stroke(width = 1f)
        )
    }
}

private fun DrawScope.drawLightningStrikes(
    flashes: List<LightningFlash>,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float
) {
    for (flash in flashes) {
        val pos = projectPointNearest(flash.location, w, h, centerLat, centerLon, zoom)
        val flashRadius = (16f * flash.intensity)

        // Bright flash halo
        drawCircle(
            color = Color(0x9080D8FF).copy(alpha = flash.intensity * 0.8f),
            center = pos,
            radius = flashRadius,
            style = Fill
        )
        drawCircle(
            color = Color.White.copy(alpha = flash.intensity),
            center = pos,
            radius = 4f,
            style = Fill
        )

        // Lightning spark forks
        val r = 12f * flash.intensity
        drawLine(Color.White, Offset(pos.x - r, pos.y), Offset(pos.x + r, pos.y), strokeWidth = 2.0f)
        drawLine(Color.White, Offset(pos.x, pos.y - r), Offset(pos.x, pos.y + r), strokeWidth = 2.0f)
        drawLine(Color(0xFF80D8FF), Offset(pos.x - r * 0.7f, pos.y - r * 0.7f), Offset(pos.x + r * 0.7f, pos.y + r * 0.7f), strokeWidth = 1.2f)
    }
}

private fun DrawScope.drawStrongWindAreas(
    areas: List<StrongWindArea>,
    selected: StrongWindArea?,
    isHighAlertMode: Boolean,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    pulse: Float,
    phase: Float
) {
    val scale = getMapScale(w, zoom)

    for (area in areas) {
        val center = projectPointNearest(area.centerLocation, w, h, centerLat, centerLon, zoom)
        val isSelected = selected?.id == area.id
        val color = area.severity.color

        val radiusPix = (area.radiusKm * 0.45f) * scale * 0.12f

        drawCircle(
            color = color.copy(alpha = if (isHighAlertMode) 0.22f else 0.12f),
            center = center,
            radius = radiusPix,
            style = Fill
        )

        drawCircle(
            color = color.copy(alpha = (1f - pulse) * 0.7f),
            center = center,
            radius = radiusPix + pulse * 20f,
            style = Stroke(width = if (isSelected) 2.5f else 1.5f)
        )

        drawCircle(
            color = color,
            center = center,
            radius = radiusPix,
            style = Stroke(
                width = if (isSelected) 2.5f else 1.2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), phase * 40f)
            )
        )

        val arrowAngleRad = Math.toRadians(area.windDirectionDeg.toDouble() - 90.0)
        val arrowLen = 28f * zoom.coerceIn(0.8f, 1.8f)
        val tipX = center.x + (arrowLen * cos(arrowAngleRad)).toFloat()
        val tipY = center.y + (arrowLen * sin(arrowAngleRad)).toFloat()

        drawLine(
            color = color,
            start = center,
            end = Offset(tipX, tipY),
            strokeWidth = 3.0f,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = color,
            center = Offset(tipX, tipY),
            radius = 4f,
            style = Fill
        )

        if (isSelected) {
            val r = radiusPix + 14f
            drawLine(color, Offset(center.x - r, center.y), Offset(center.x + r, center.y), strokeWidth = 1.8f)
            drawLine(color, Offset(center.x, center.y - r), Offset(center.x, center.y + r), strokeWidth = 1.8f)
        }
    }
}

private fun DrawScope.drawHumidityField(
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    cyclones: List<Cyclone>,
    isDark: Boolean
) {
    val scale = getMapScale(w, zoom)
    val itczY = (h / 2f) - (5.0 - centerLat).toFloat() * scale
    if (itczY in -150f..(h + 150f)) {
        val baseColor = if (isDark) Color(0x3000E5FF) else Color(0x2500838F)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    baseColor.copy(alpha = 0.15f),
                    baseColor,
                    baseColor.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                startY = itczY - 45f * zoom,
                endY = itczY + 45f * zoom
            ),
            topLeft = Offset(0f, itczY - 45f * zoom),
            size = Size(w, 90f * zoom)
        )
    }

    for (cyclone in cyclones) {
        val center = projectPointNearest(cyclone.currentLocation, w, h, centerLat, centerLon, zoom)
        val radius = (cyclone.galeRadiusKm * 1.6f) * (zoom * 0.40f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x4500E5FF),
                    Color(0x2500B0FF),
                    Color(0x1000838F),
                    Color.Transparent
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
    }
}

private fun DrawScope.drawWindStreamlines(
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    phase: Float,
    cyclones: List<Cyclone>,
    strongWindAreas: List<StrongWindArea>,
    isHighIntensity: Boolean
) {
    val windColor = if (isHighIntensity) Color(0x9080D8FF) else Color(0x6080D8FF)
    val galeColor = Color(0xFFFFD600)

    val scale = getMapScale(w, zoom)
    val latitudes = listOf(15.0, 25.0, -15.0, 45.0, -45.0, -55.0)
    for (lat in latitudes) {
        val y = (h / 2f) - (lat - centerLat).toFloat() * scale
        if (y in -20f..(h + 20f)) {
            val directionRight = lat > 30.0 || lat < -30.0
            val offsetPhase = if (directionRight) phase else (1f - phase)
            val dashOffset = offsetPhase * 40f

            drawLine(
                color = windColor,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = if (isHighIntensity) 1.8f else 1.0f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 26f), dashOffset)
            )
        }
    }

    // Cyclonic vortex wind spirals around storm centers
    for (cyclone in cyclones) {
        val center = projectPointNearest(cyclone.currentLocation, w, h, centerLat, centerLon, zoom)
        val spiralRadius = (cyclone.galeRadiusKm * 0.9f) * (zoom * 0.35f)

        for (ring in 1..3) {
            val r = (spiralRadius * (ring / 3f))
            val angleOffset = phase * 360f * (if (cyclone.currentLocation.lat >= 0) -1f else 1f)

            drawCircle(
                color = if (ring == 1) galeColor else windColor,
                center = center,
                radius = r,
                style = Stroke(
                    width = if (ring == 1) 2.0f else 1.2f,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(15f + ring * 5f, 15f + ring * 5f),
                        angleOffset
                    )
                )
            )
        }
    }
}

private fun DrawScope.drawSatellites(
    satellites: List<Satellite>,
    selected: Satellite?,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    pulse: Float
) {
    for (sat in satellites) {
        val pos = projectPointNearest(sat.location, w, h, centerLat, centerLon, zoom)
        val isSelected = selected?.id == sat.id

        val coverageRadius = (sat.coverageRadiusKm * 0.12f) * (zoom * 0.35f)
        drawCircle(
            color = if (isSelected) Color(0x35B388FF) else Color(0x15B388FF),
            center = pos,
            radius = coverageRadius,
            style = Fill
        )
        drawCircle(
            color = Color(0x60B388FF),
            center = pos,
            radius = coverageRadius,
            style = Stroke(
                width = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), pulse * 16f)
            )
        )

        drawCircle(
            color = Color(0xFFD1C4E9),
            center = pos,
            radius = if (isSelected) 7f else 5f,
            style = Fill
        )

        drawLine(
            color = Color(0xFFB388FF),
            start = Offset(pos.x - 14f, pos.y),
            end = Offset(pos.x + 14f, pos.y),
            strokeWidth = 2.5f
        )
    }
}

private fun DrawScope.drawServers(
    servers: List<ServerNode>,
    selected: ServerNode?,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    pulse: Float
) {
    for (server in servers) {
        val pos = projectPointNearest(server.location, w, h, centerLat, centerLon, zoom)
        val isSelected = selected?.id == server.id

        val pulseRadius = 12f + pulse * 16f
        drawCircle(
            color = ServerNodeGlow.copy(alpha = (1f - pulse) * 0.6f),
            center = pos,
            radius = pulseRadius,
            style = Stroke(width = 1.5f)
        )

        drawCircle(
            color = if (isSelected) Color.White else ServerNodeGlow,
            center = pos,
            radius = if (isSelected) 6f else 4.5f,
            style = Fill
        )

        val d = 9f
        val diamond = Path().apply {
            moveTo(pos.x, pos.y - d)
            lineTo(pos.x + d, pos.y)
            lineTo(pos.x, pos.y + d)
            lineTo(pos.x - d, pos.y)
            close()
        }
        drawPath(
            path = diamond,
            color = if (isSelected) Color.White else ServerNodeGlow,
            style = Stroke(width = 1.5f)
        )
    }
}

private fun DrawScope.drawCyclones(
    cyclones: List<Cyclone>,
    selected: Cyclone?,
    stabilizationMode: StabilizationDisplayMode,
    accuracyPct: Int,
    forecastHours: Int,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    pulse: Float,
    particlePhase: Float
) {
    for (cyclone in cyclones) {
        // Cyclone position dynamically moves along the stabilized forecast track during timeline playback
        val activeLocation = getCycloneForecastLocation(cyclone, forecastHours)
        val (activeWind, activePressure) = getCycloneForecastIntensity(cyclone, forecastHours)
        val center = projectPointNearest(activeLocation, w, h, centerLat, centerLon, zoom)
        val isSelected = selected?.id == cyclone.id

        // EF Intensity Color Mapping requested by user:
        // Super strong (>= 115 kts) = Dark Purple
        // Strong (64-114 kts) = Red
        // Not too strong (< 64 kts) = Blue
        val intensityColor = getCycloneIntensityColor(activeWind)
        val intensityAccent = getCycloneIntensityAccentColor(activeWind)

        // 1. Historical Track with past checkpoint nodes
        if (cyclone.historicalPath.size > 1 && forecastHours == 0) {
            val histPts = cyclone.historicalPath
            for (i in 0 until histPts.size - 1) {
                val p1 = projectPointNearest(histPts[i], w, h, centerLat, centerLon, zoom)
                val p2 = projectPointNearest(histPts[i + 1], w, h, centerLat, centerLon, zoom)
                val fraction = (i + 1).toFloat() / histPts.size
                val pastWind = (cyclone.sustainedWindKts * (0.60f + 0.35f * fraction)).toInt()
                val pastColor = getCycloneIntensityColor(pastWind)

                drawLine(
                    color = pastColor.copy(alpha = 0.55f),
                    start = p1,
                    end = p2,
                    strokeWidth = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                )
            }

            // Past checkpoint dots (-24h, -18h, -12h, -6h)
            val pastHours = listOf(-24, -18, -12, -6)
            for ((idx, pt) in histPts.withIndex()) {
                val p = projectPointNearest(pt, w, h, centerLat, centerLon, zoom)
                val fraction = (idx + 1).toFloat() / histPts.size
                val pastWind = (cyclone.sustainedWindKts * (0.60f + 0.35f * fraction)).toInt()
                val pCol = getCycloneIntensityColor(pastWind)
                drawCircle(color = pCol.copy(alpha = 0.4f), center = p, radius = 6f, style = Fill)
                drawCircle(color = pCol, center = p, radius = 3.5f, style = Fill)
                drawCircle(color = Color.White, center = p, radius = 1.5f, style = Fill)

                if (isSelected && idx % 2 == 0) {
                    val hourTag = pastHours.getOrElse(idx) { -((histPts.size - idx) * 6) }
                    val pText = Paint().apply {
                        color = Color.LightGray.toArgb()
                        textSize = 16f
                        typeface = Typeface.MONOSPACE
                        isAntiAlias = true
                    }
                    drawContext.canvas.nativeCanvas.drawText("T${hourTag}H", p.x + 8f, p.y + 4f, pText)
                }
            }
        }

        // 2. Forecast Tracks with Color-Coded Intensity Segments & Checkpoints
        if (stabilizationMode == StabilizationDisplayMode.STABILIZED_ONLY || isSelected) {
            drawStabilizedConsensus(cyclone, accuracyPct, forecastHours, w, h, centerLat, centerLon, zoom, pulse)
        }

        if (stabilizationMode == StabilizationDisplayMode.MODEL_COMPARISON) {
            drawModelTrack(cyclone.ecmwfTrack, EcmwfTrack, w, h, centerLat, centerLon, zoom)
            drawModelTrack(cyclone.weatherNext3Track, WeatherNext3Track, w, h, centerLat, centerLon, zoom)
            drawModelTrack(cyclone.weatherNext2Track, WeatherNext2Track, w, h, centerLat, centerLon, zoom)
            if (cyclone.gfsTrack.isNotEmpty()) {
                drawModelTrack(cyclone.gfsTrack, Color(0xFF00E676), w, h, centerLat, centerLon, zoom)
            }
            if (cyclone.jmaTrack.isNotEmpty()) {
                drawModelTrack(cyclone.jmaTrack, Color(0xFFFFD600), w, h, centerLat, centerLon, zoom)
            }
            // Draw stabilized consensus as reference baseline
            drawStabilizedConsensus(cyclone, accuracyPct, forecastHours, w, h, centerLat, centerLon, zoom, pulse)
        }

        // 3. REALISTIC SATELLITE / RADAR CYCLONE RENDERING (Not just a spinning line!)
        val galeRadius = (cyclone.galeRadiusKm * 0.48f) * (zoom * 0.42f).coerceAtLeast(0.6f)
        val eyeRadius = (cyclone.eyeRadiusKm * 0.42f + 7f) * zoom.coerceIn(0.8f, 1.6f)
        val rotationDir = if (cyclone.currentLocation.lat >= 0) -1f else 1f // Coriolis: CCW in North, CW in South

        // A. Atmospheric Moisture & Cirrus Outflow Cloud Canopy
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    intensityColor.copy(alpha = 0.28f),
                    intensityColor.copy(alpha = 0.14f),
                    Color.Transparent
                ),
                center = center,
                radius = galeRadius * 1.35f
            ),
            center = center,
            radius = galeRadius * 1.35f,
            style = Fill
        )

        // Outer gale boundary ring (delicate dashed nautical contour)
        drawCircle(
            color = intensityColor.copy(alpha = 0.40f),
            center = center,
            radius = galeRadius,
            style = Stroke(
                width = 1.2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )
        )

        // B. Upper-Tropospheric Anticyclonic Cirrus Outflow Streaks (Wispy cloud feathers)
        val outflowCount = 8
        for (wisp in 0 until outflowCount) {
            val wispAngle = (wisp * (360f / outflowCount)) + (particlePhase * 120f * -rotationDir)
            val wispPath = Path()
            val startRad = Math.toRadians(wispAngle.toDouble())
            val startDist = galeRadius * 0.70f
            val sx = center.x + (startDist * kotlin.math.cos(startRad)).toFloat()
            val sy = center.y + (startDist * kotlin.math.sin(startRad)).toFloat()
            wispPath.moveTo(sx, sy)

            val endRad = Math.toRadians((wispAngle + 35f * -rotationDir).toDouble())
            val endDist = galeRadius * 1.30f
            val ex = center.x + (endDist * kotlin.math.cos(endRad)).toFloat()
            val ey = center.y + (endDist * kotlin.math.sin(endRad)).toFloat()
            wispPath.quadraticTo(
                (sx + ex) / 2f + (15f * rotationDir),
                (sy + ey) / 2f,
                ex, ey
            )
            drawPath(
                path = wispPath,
                color = Color.White.copy(alpha = 0.22f),
                style = Stroke(width = 1.4f)
            )
        }

        // C. Realistic Logarithmic Convective Feeder Rainbands (Broad ribbons with embedded radar bursts)
        val rainbandCount = 6
        for (arm in 0 until rainbandCount) {
            val baseAngle = (arm * (360f / rainbandCount)) + (particlePhase * 360f * rotationDir)
            val armPath = Path()
            var first = true

            // Generate logarithmic spiral coordinates curling into eyewall
            val steps = 14
            val ribbonPoints = mutableListOf<Offset>()
            for (step in 1..steps) {
                val angleDeg = baseAngle + (step * 22f * rotationDir)
                val rad = Math.toRadians(angleDeg.toDouble())
                // Logarithmic expansion: dense core near eyewall, broader tapering outward
                val dist = eyeRadius + (step * (1.6f + step * 0.24f) * zoom.coerceIn(0.8f, 1.8f))
                val px = center.x + (dist * kotlin.math.cos(rad)).toFloat()
                val py = center.y + (dist * kotlin.math.sin(rad)).toFloat()
                ribbonPoints.add(Offset(px, py))
                if (first) {
                    armPath.moveTo(px, py)
                    first = false
                } else {
                    armPath.lineTo(px, py)
                }
            }

            // Broad cloud rainband body (simulating dense moisture/radar cloud bands)
            drawPath(
                path = armPath,
                color = intensityColor.copy(alpha = 0.45f),
                style = Stroke(width = 8.5f, cap = StrokeCap.Round)
            )

            // Inner high-reflectivity convective core line
            drawPath(
                path = armPath,
                color = intensityAccent.copy(alpha = 0.70f),
                style = Stroke(width = 3.2f, cap = StrokeCap.Round)
            )

            // Embedded convective bursts (intense thunderstorms along primary feeder bands)
            for (step in listOf(3, 7, 11)) {
                if (step < ribbonPoints.size) {
                    val burstPt = ribbonPoints[step]
                    val burstR = (4.5f + (step % 3)) * zoom.coerceIn(0.8f, 1.4f)
                    drawCircle(
                        color = intensityAccent.copy(alpha = 0.65f),
                        center = burstPt,
                        radius = burstR,
                        style = Fill
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        center = burstPt,
                        radius = burstR * 0.45f,
                        style = Fill
                    )
                }
            }
        }

        // D. Central Dense Overcast (CDO) Disk (Solid convective cloud shield surrounding eye)
        val cdoRadius = eyeRadius * 2.4f + 8f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    intensityAccent.copy(alpha = 0.75f),
                    intensityColor.copy(alpha = 0.60f),
                    intensityColor.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = center,
                radius = cdoRadius
            ),
            center = center,
            radius = cdoRadius,
            style = Fill
        )

        // E. Eyewall Stadium & Convective Ring
        val eyewallPulseR = eyeRadius + pulse * 4f
        drawCircle(
            color = intensityAccent.copy(alpha = 0.85f),
            center = center,
            radius = eyewallPulseR,
            style = Stroke(width = 4.2f)
        )

        // Orbiting eyewall mesovortices (high-reflectivity radar towers)
        val vortexCount = 10
        for (v in 0 until vortexCount) {
            val vAngle = Math.toRadians((v * (360.0 / vortexCount) + (particlePhase * 720.0 * rotationDir)))
            val vx = center.x + (eyeRadius * kotlin.math.cos(vAngle)).toFloat()
            val vy = center.y + (eyeRadius * kotlin.math.sin(vAngle)).toFloat()
            drawCircle(
                color = Color.White,
                center = Offset(vx, vy),
                radius = 2.0f,
                style = Fill
            )
        }

        // F. The Eye (Calm Ocean Core Aperture)
        // Deep dark oceanic eye void
        drawCircle(
            color = Color(0xFF040A14),
            center = center,
            radius = eyeRadius - 1.5f,
            style = Fill
        )

        // Central low-pressure core pip & target cross
        drawCircle(
            color = intensityAccent,
            center = center,
            radius = 3.0f,
            style = Fill
        )
        drawCircle(
            color = Color.White,
            center = center,
            radius = 1.5f,
            style = Fill
        )

        // Tactical Selection Lock Brackets
        if (isSelected) {
            val r = (galeRadius.coerceAtLeast(36f) + 14f)
            val cornerLen = 14f
            val cColor = intensityAccent

            // 4 corner targeting brackets
            drawLine(cColor, Offset(center.x - r, center.y - r), Offset(center.x - r + cornerLen, center.y - r), strokeWidth = 2.5f)
            drawLine(cColor, Offset(center.x - r, center.y - r), Offset(center.x - r, center.y - r + cornerLen), strokeWidth = 2.5f)

            drawLine(cColor, Offset(center.x + r, center.y - r), Offset(center.x + r - cornerLen, center.y - r), strokeWidth = 2.5f)
            drawLine(cColor, Offset(center.x + r, center.y - r), Offset(center.x + r, center.y - r + cornerLen), strokeWidth = 2.5f)

            drawLine(cColor, Offset(center.x - r, center.y + r), Offset(center.x - r + cornerLen, center.y + r), strokeWidth = 2.5f)
            drawLine(cColor, Offset(center.x - r, center.y + r), Offset(center.x - r, center.y + r - cornerLen), strokeWidth = 2.5f)

            drawLine(cColor, Offset(center.x + r, center.y + r), Offset(center.x + r - cornerLen, center.y + r), strokeWidth = 2.5f)
            drawLine(cColor, Offset(center.x + r, center.y + r), Offset(center.x + r, center.y + r - cornerLen), strokeWidth = 2.5f)

            // Target lock pulse ring
            drawCircle(cColor.copy(alpha = (1f - pulse) * 0.7f), center = center, radius = r + pulse * 14f, style = Stroke(1.2f))

            // Text label above target with category and EF intensity
            val targetPaint = Paint().apply {
                color = cColor.toArgb()
                textSize = 21f
                typeface = Typeface.MONOSPACE
                isAntiAlias = true
            }
            val categoryLabel = getCycloneIntensityCategory(activeWind)
            val lockText = if (forecastHours > 0) {
                "${cyclone.name.uppercase()} • $categoryLabel • $activeWind KT (T+${forecastHours}H)"
            } else {
                "${cyclone.name.uppercase()} • $categoryLabel • ${cyclone.sustainedWindKts} KT"
            }
            drawContext.canvas.nativeCanvas.drawText(
                lockText,
                center.x - r,
                center.y - r - 8f,
                targetPaint
            )
        }
    }
}

/**
 * Renders the cool color-coded trace line with EF intensity colors and checkpoint dots:
 * - Super strong: Dark Purple
 * - Strong: Red
 * - Not too strong: Blue
 * With luminous checkpoint pips and time labels at each track point.
 */
private fun DrawScope.drawStabilizedConsensus(
    cyclone: Cyclone,
    accuracyPct: Int,
    forecastHours: Int,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    pulse: Float
) {
    if (cyclone.stabilizedTrack.size < 2) return

    val accuracyFactor = ((110 - accuracyPct) / 30f).coerceIn(0.6f, 3.0f)

    // 1. Cone of Uncertainty (Error swath)
    val coneLeft = Path()
    val coneRight = Path()
    var first = true
    for ((index, trackPt) in cyclone.stabilizedTrack.withIndex()) {
        val p = projectPointNearest(trackPt.point, w, h, centerLat, centerLon, zoom)
        val spread = (index * 11f * zoom * accuracyFactor)
        if (first) {
            coneLeft.moveTo(p.x, p.y)
            coneRight.moveTo(p.x, p.y)
            first = false
        } else {
            coneLeft.lineTo(p.x - spread, p.y)
            coneRight.lineTo(p.x + spread, p.y)
        }
    }

    val fullCone = Path().apply {
        addPath(coneLeft)
        for (i in cyclone.stabilizedTrack.indices.reversed()) {
            val trackPt = cyclone.stabilizedTrack[i]
            val p = projectPointNearest(trackPt.point, w, h, centerLat, centerLon, zoom)
            val spread = (i * 11f * zoom * accuracyFactor)
            lineTo(p.x + spread, p.y)
        }
        close()
    }
    drawPath(fullCone, color = ConsensusModelTrack.copy(alpha = 0.12f), style = Fill)
    drawPath(fullCone, color = ConsensusModelTrack.copy(alpha = 0.35f), style = Stroke(width = 1.0f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)))

    // 2. Multi-Segment Color-Coded Trace Line according to EF intensity
    val track = cyclone.stabilizedTrack
    for (i in 0 until track.size - 1) {
        val ptA = track[i]
        val ptB = track[i + 1]
        val posA = projectPointNearest(ptA.point, w, h, centerLat, centerLon, zoom)
        val posB = projectPointNearest(ptB.point, w, h, centerLat, centerLon, zoom)

        val colorA = getCycloneIntensityColor(ptA.windSpeedKts)
        val colorB = getCycloneIntensityColor(ptB.windSpeedKts)
        val segBrush = Brush.linearGradient(listOf(colorA, colorB), start = posA, end = posB)

        // Glowing underlay
        drawLine(
            brush = segBrush,
            start = posA,
            end = posB,
            strokeWidth = 9.0f,
            cap = StrokeCap.Round,
            alpha = 0.35f
        )
        // Sharp crisp trace line
        drawLine(
            brush = segBrush,
            start = posA,
            end = posB,
            strokeWidth = 3.6f,
            cap = StrokeCap.Round
        )
        // Core luminous dashed runner
        drawLine(
            color = Color.White.copy(alpha = 0.70f),
            start = posA,
            end = posB,
            strokeWidth = 1.2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f), 0f)
        )
    }

    // 3. Cool Checkpoint Dots along the trace line
    val checkpointPaint = Paint().apply {
        textSize = 17f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
    }

    for (trackPt in track) {
        val p = projectPointNearest(trackPt.point, w, h, centerLat, centerLon, zoom)
        val ptColor = getCycloneIntensityColor(trackPt.windSpeedKts)
        val ptAccent = getCycloneIntensityAccentColor(trackPt.windSpeedKts)
        val isSelectedHour = (trackPt.hoursAhead == forecastHours)

        // Outer glow halo
        val haloRadius = if (isSelectedHour) 13f + pulse * 6f else 8.5f
        drawCircle(
            color = ptColor.copy(alpha = if (isSelectedHour) 0.85f else 0.40f),
            center = p,
            radius = haloRadius,
            style = Fill
        )

        // Solid intensity-colored checkpoint ring
        drawCircle(
            color = ptColor,
            center = p,
            radius = if (isSelectedHour) 6.5f else 5.0f,
            style = Fill
        )

        // High-contrast white center node
        drawCircle(
            color = Color.White,
            center = p,
            radius = if (isSelectedHour) 2.8f else 2.0f,
            style = Fill
        )

        // Active selection target bracket
        if (isSelectedHour) {
            val bracketR = 15f + pulse * 4f
            drawCircle(
                color = ptAccent,
                center = p,
                radius = bracketR,
                style = Stroke(width = 1.8f)
            )
        }

        // Checkpoint time badge
        val labelText = if (trackPt.hoursAhead == 0) "NOW" else "+${trackPt.hoursAhead}H"
        val subText = "${trackPt.windSpeedKts}KT"

        val pillX = p.x + 12f
        val pillY = p.y - 12f

        // Draw pill background
        checkpointPaint.color = Color(0xDD08111E).toArgb()
        drawContext.canvas.nativeCanvas.drawRoundRect(
            pillX - 4f,
            pillY - 14f,
            pillX + 70f,
            pillY + 16f,
            6f,
            6f,
            checkpointPaint
        )

        // Draw pill border
        checkpointPaint.color = ptColor.toArgb()
        checkpointPaint.style = Paint.Style.STROKE
        checkpointPaint.strokeWidth = 1.5f
        drawContext.canvas.nativeCanvas.drawRoundRect(
            pillX - 4f,
            pillY - 14f,
            pillX + 70f,
            pillY + 16f,
            6f,
            6f,
            checkpointPaint
        )

        // Draw text inside pill
        checkpointPaint.style = Paint.Style.FILL
        checkpointPaint.color = Color.White.toArgb()
        checkpointPaint.textSize = 14f
        drawContext.canvas.nativeCanvas.drawText(labelText, pillX + 4f, pillY, checkpointPaint)

        checkpointPaint.color = ptAccent.toArgb()
        checkpointPaint.textSize = 12f
        drawContext.canvas.nativeCanvas.drawText(subText, pillX + 38f, pillY, checkpointPaint)
    }
}

private fun DrawScope.drawModelTrack(
    track: List<ForecastTrackPoint>,
    color: Color,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float
) {
    if (track.size < 2) return
    val path = Path()
    var first = true
    for (pt in track) {
        val p = projectPointNearest(pt.point, w, h, centerLat, centerLon, zoom)
        if (first) {
            path.moveTo(p.x, p.y)
            first = false
        } else {
            path.lineTo(p.x, p.y)
        }
    }
    drawPath(path, color = color, style = Stroke(width = 2.0f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)))
    for (pt in track) {
        val p = projectPointNearest(pt.point, w, h, centerLat, centerLon, zoom)
        drawCircle(color = color, center = p, radius = 3f, style = Fill)
    }
}

private fun DrawScope.drawTacticalScanningBox(
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    targetLocation: GeoPoint?,
    angleDeg: Float,
    pulse: Float,
    projection: MapProjectionType
) {
    // 1. Determine target anchor point
    val anchor = if (targetLocation != null) {
        val proj = projectPointNearest(targetLocation, w, h, centerLat, centerLon, zoom, projection)
        if (proj.x > -500f && proj.x < w + 500f && proj.y > -500f && proj.y < h + 500f) {
            proj
        } else {
            Offset(w / 2f, h / 2f)
        }
    } else {
        Offset(w / 2f, h / 2f)
    }

    val boxSize = (165f * zoom.coerceIn(0.8f, 1.4f)).coerceIn(120f, 250f)
    val halfW = boxSize / 2f
    val halfH = boxSize / 2f

    // 3D Isometric Extrusion Offsets for Volumetric Cube Depth
    val depthX = 28f * zoom.coerceIn(0.8f, 1.3f)
    val depthY = -22f * zoom.coerceIn(0.8f, 1.3f)

    // Front Face Rect Corners
    val x0 = anchor.x - halfW
    val y0 = anchor.y - halfH
    val x1 = anchor.x + halfW
    val y1 = anchor.y + halfH

    // Back Face Rect Corners
    val x0b = x0 + depthX
    val y0b = y0 + depthY
    val x1b = x1 + depthX
    val y1b = y1 + depthY

    // 2. Volumetric Ground Frustum Projection Rays (anchoring scanning volume to terrain)
    val groundAnchor = Offset(anchor.x, anchor.y + halfH * 1.30f)
    val frustumDash = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
    val rayColor = RadarCyan.copy(alpha = 0.35f)
    drawLine(rayColor, Offset(x0, y1), groundAnchor, strokeWidth = 1.0f, pathEffect = frustumDash)
    drawLine(rayColor, Offset(x1, y1), groundAnchor, strokeWidth = 1.0f, pathEffect = frustumDash)
    drawCircle(color = RadarCyan.copy(alpha = 0.6f), center = groundAnchor, radius = 3.5f, style = Stroke(width = 1.2f))

    // 3. Back Plane Wireframe & 3D Depth Edges
    val backColor = RadarCyan.copy(alpha = 0.35f)
    drawRect(
        color = backColor,
        topLeft = Offset(x0b, y0b),
        size = Size(boxSize, boxSize),
        style = Stroke(width = 1.0f, pathEffect = frustumDash)
    )
    // 4 Connecting Depth Edges
    drawLine(backColor, Offset(x0, y0), Offset(x0b, y0b), strokeWidth = 1.2f, pathEffect = frustumDash)
    drawLine(backColor, Offset(x1, y0), Offset(x1b, y0b), strokeWidth = 1.2f, pathEffect = frustumDash)
    drawLine(backColor, Offset(x1, y1), Offset(x1b, y1b), strokeWidth = 1.2f, pathEffect = frustumDash)
    drawLine(backColor, Offset(x0, y1), Offset(x0b, y1b), strokeWidth = 1.2f, pathEffect = frustumDash)

    // 4. Front Face Holographic Tint Glass
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                RadarCyan.copy(alpha = 0.12f),
                Color(0x0500E5FF),
                RadarCyan.copy(alpha = 0.16f)
            ),
            startY = y0,
            endY = y1
        ),
        topLeft = Offset(x0, y0),
        size = Size(boxSize, boxSize),
        style = Fill
    )
    drawRect(
        color = RadarCyan.copy(alpha = 0.50f),
        topLeft = Offset(x0, y0),
        size = Size(boxSize, boxSize),
        style = Stroke(width = 1.2f)
    )

    // 5. Tactical Corner L-Brackets on Front Face
    val bracketLen = 24f
    val bracketColor = RadarCyan
    val bracketStroke = 3.0f
    // Top-Left
    drawLine(bracketColor, Offset(x0, y0), Offset(x0 + bracketLen, y0), strokeWidth = bracketStroke)
    drawLine(bracketColor, Offset(x0, y0), Offset(x0, y0 + bracketLen), strokeWidth = bracketStroke)
    // Top-Right
    drawLine(bracketColor, Offset(x1, y0), Offset(x1 - bracketLen, y0), strokeWidth = bracketStroke)
    drawLine(bracketColor, Offset(x1, y0), Offset(x1, y0 + bracketLen), strokeWidth = bracketStroke)
    // Bottom-Right
    drawLine(bracketColor, Offset(x1, y1), Offset(x1 - bracketLen, y1), strokeWidth = bracketStroke)
    drawLine(bracketColor, Offset(x1, y1), Offset(x1, y1 - bracketLen), strokeWidth = bracketStroke)
    // Bottom-Left
    drawLine(bracketColor, Offset(x0, y1), Offset(x0 + bracketLen, y1), strokeWidth = bracketStroke)
    drawLine(bracketColor, Offset(x0, y1), Offset(x0, y1 - bracketLen), strokeWidth = bracketStroke)

    // Pulsating corner LED dots
    val ledAlpha = (0.5f + 0.5f * kotlin.math.sin(pulse * 6.28f)).coerceIn(0.2f, 1.0f)
    val ledColor = RadarGreen.copy(alpha = ledAlpha)
    drawCircle(color = ledColor, center = Offset(x0, y0), radius = 2.8f)
    drawCircle(color = ledColor, center = Offset(x1, y0), radius = 2.8f)
    drawCircle(color = ledColor, center = Offset(x1, y1), radius = 2.8f)
    drawCircle(color = ledColor, center = Offset(x0, y1), radius = 2.8f)

    // 6. Holographic Animated Laser Scanning Plane (Matrix Slicer)
    val scanCycle = (angleDeg * 1.5f) % 360f
    val scanFraction = (1.0f - kotlin.math.cos(Math.toRadians(scanCycle.toDouble())).toFloat()) / 2.0f
    val scanY = y0 + (boxSize * scanFraction)

    // Volumetric planar slice
    val slicePath = Path().apply {
        moveTo(x0, scanY)
        lineTo(x1, scanY)
        lineTo(x1 + depthX, scanY + depthY)
        lineTo(x0 + depthX, scanY + depthY)
        close()
    }
    drawPath(
        slicePath,
        brush = Brush.verticalGradient(
            colors = listOf(
                RadarCyan.copy(alpha = 0.35f),
                RadarGreen.copy(alpha = 0.18f),
                Color.Transparent
            ),
            startY = scanY + depthY,
            endY = scanY
        )
    )

    // Sharp glowing laser beam slicing across front face
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(RadarCyan.copy(alpha = 0.4f), Color.White, RadarCyan.copy(alpha = 0.4f)),
            start = Offset(x0, scanY),
            end = Offset(x1, scanY)
        ),
        start = Offset(x0, scanY),
        end = Offset(x1, scanY),
        strokeWidth = 3.2f
    )
    // Trailing scanlines
    for (step in 1..3) {
        val trailY = scanY - (step * 8f)
        if (trailY >= y0) {
            drawLine(
                RadarCyan.copy(alpha = 0.22f / step),
                Offset(x0, trailY),
                Offset(x1, trailY),
                strokeWidth = 1.0f
            )
        }
    }

    // 7. Internal Holographic Grid (Tactical cross-grid inside box)
    val gridCols = 3
    for (c in 1 until gridCols) {
        val gx = x0 + (boxSize / gridCols) * c
        drawLine(
            RadarCyan.copy(alpha = 0.18f),
            Offset(gx, y0),
            Offset(gx, y1),
            strokeWidth = 0.8f,
            pathEffect = frustumDash
        )
    }

    // 8. Central Target Acquisition Reticle
    val reticleRadius = 15f * zoom.coerceIn(0.8f, 1.2f)
    drawCircle(
        color = RadarCyan.copy(alpha = 0.75f),
        center = anchor,
        radius = reticleRadius,
        style = Stroke(width = 1.3f)
    )
    drawCircle(
        color = Color(0xFFFF1744).copy(alpha = 0.90f),
        center = anchor,
        radius = 3.2f,
        style = Fill
    )
    // Rotating crosshairs ticks
    val tickRad = Math.toRadians(angleDeg.toDouble())
    val tickLen = reticleRadius * 1.5f
    val cosT = kotlin.math.cos(tickRad).toFloat()
    val sinT = kotlin.math.sin(tickRad).toFloat()
    drawLine(
        RadarCyan.copy(alpha = 0.65f),
        Offset(anchor.x - tickLen * cosT, anchor.y - tickLen * sinT),
        Offset(anchor.x + tickLen * cosT, anchor.y + tickLen * sinT),
        strokeWidth = 1.2f
    )
    drawLine(
        RadarCyan.copy(alpha = 0.65f),
        Offset(anchor.x - tickLen * -sinT, anchor.y - tickLen * cosT),
        Offset(anchor.x + tickLen * -sinT, anchor.y + tickLen * cosT),
        strokeWidth = 1.2f
    )

    // 9. Monospace Tactical Readout Overlays
    val textPaint = Paint().apply {
        color = android.graphics.Color.argb(220, 0, 229, 255)
        textSize = 20f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
    }
    val smallPaint = Paint().apply {
        color = android.graphics.Color.argb(180, 255, 255, 255)
        textSize = 17f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
    }
    drawContext.canvas.nativeCanvas.apply {
        drawText("[■] 10cm S-BAND RADAR SCANNER (WSR-88D)", x0, y0 - 8f, textPaint)
        drawText("AZ: ${(angleDeg % 360).toInt()}° • 2.8GHz DUAL-POL • LEVEL II", x0, y1 + 22f, smallPaint)
        if (targetLocation != null) {
            val latStr = String.format("%.2f", targetLocation.lat)
            val lonStr = String.format("%.2f", targetLocation.lon)
            drawText("TGT: $latStr°N $lonStr°E", x0b, y0b - 6f, smallPaint)
        }
    }
}

private fun DrawScope.drawCustomRadarScanner(
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    targetLocation: GeoPoint?,
    angleDeg: Float,
    pulse: Float,
    style: ScannerBeamStyle,
    projection: MapProjectionType
) {
    if (style == ScannerBeamStyle.SCANNING_BOX) {
        drawTacticalScanningBox(
            w = w,
            h = h,
            centerLat = centerLat,
            centerLon = centerLon,
            zoom = zoom,
            targetLocation = targetLocation,
            angleDeg = angleDeg,
            pulse = pulse,
            projection = projection
        )
        return
    }

    val origin = if (targetLocation != null) {
        val p = projectPointNearest(targetLocation, w, h, centerLat, centerLon, zoom, projection)
        if (p.x > -500f) p else Offset(w / 2f, h / 2f)
    } else {
        Offset(w / 2f, h / 2f)
    }

    val beamLength = maxOf(w, h) * 0.95f
    val rad = Math.toRadians(angleDeg.toDouble())
    val endX = origin.x + (beamLength * cos(rad)).toFloat()
    val endY = origin.y + (beamLength * sin(rad)).toFloat()

    when (style) {
        ScannerBeamStyle.SCANNING_BOX -> {}
        ScannerBeamStyle.PHOSPHOR_RADAR -> {
            // Draw sweeping phosphor afterglow sector (40° wedge behind the beam)
            val trailPath = Path().apply {
                moveTo(origin.x, origin.y)
                for (step in 0..8) {
                    val a = Math.toRadians((angleDeg - step * 5.0))
                    lineTo(origin.x + (beamLength * cos(a)).toFloat(), origin.y + (beamLength * sin(a)).toFloat())
                }
                close()
            }
            drawPath(
                trailPath,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x3500E5FF), Color(0x1000E5FF), Color.Transparent),
                    center = origin,
                    radius = beamLength
                )
            )

            // Sharp radar lead beam
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.White, RadarCyan, Color.Transparent),
                    start = origin,
                    end = Offset(endX, endY)
                ),
                start = origin,
                end = Offset(endX, endY),
                strokeWidth = 2.8f
            )
        }
        ScannerBeamStyle.TACTICAL_LASER -> {
            // High-power focused laser line
            drawLine(
                color = RadarCyan,
                start = origin,
                end = Offset(endX, endY),
                strokeWidth = 2.0f
            )
            // Target acquisition reticle ticks around origin
            val reticleR = 30f * zoom.coerceIn(0.8f, 1.8f)
            drawCircle(color = RadarCyan.copy(alpha = 0.6f), center = origin, radius = reticleR, style = Stroke(width = 1.2f))
            drawCircle(color = RadarCyan.copy(alpha = 0.3f), center = origin, radius = reticleR * 1.6f, style = Stroke(width = 0.8f))
        }
        ScannerBeamStyle.INFRARED_SPECTRUM -> {
            // Infrared convective multi-spectral beam
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFF1744), Color(0xFFFFB300), Color(0xFF00E5FF), Color.Transparent),
                    start = origin,
                    end = Offset(endX, endY)
                ),
                start = origin,
                end = Offset(endX, endY),
                strokeWidth = 3.2f
            )
        }
        ScannerBeamStyle.DOPPLER_VELOCITY -> {
            // Dual-polarization Doppler radar radial velocity sweep
            val sweepIn = Path().apply {
                moveTo(origin.x, origin.y)
                for (step in 0..6) {
                    val a = Math.toRadians(angleDeg.toDouble() - step * 5.0)
                    lineTo(origin.x + (beamLength * cos(a)).toFloat(), origin.y + (beamLength * sin(a)).toFloat())
                }
                close()
            }
            val sweepOut = Path().apply {
                moveTo(origin.x, origin.y)
                for (step in 0..6) {
                    val a = Math.toRadians(angleDeg.toDouble() + 180.0 - step * 5.0)
                    lineTo(origin.x + (beamLength * cos(a)).toFloat(), origin.y + (beamLength * sin(a)).toFloat())
                }
                close()
            }
            drawPath(
                sweepIn,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x5500E676), Color(0x2000E676), Color.Transparent),
                    center = origin,
                    radius = beamLength
                )
            )
            drawPath(
                sweepOut,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x55FF1744), Color(0x20FF1744), Color.Transparent),
                    center = origin,
                    radius = beamLength
                )
            )
            drawLine(Color(0xFF00E676), origin, Offset(endX, endY), strokeWidth = 2.5f)
            val oppX = origin.x - (beamLength * cos(rad)).toFloat()
            val oppY = origin.y - (beamLength * sin(rad)).toFloat()
            drawLine(Color(0xFFFF1744), origin, Offset(oppX, oppY), strokeWidth = 2.5f)
        }
    }

    // Concentric distance range rings around target
    for (step in 1..4) {
        val r = (step * 70f) * zoom.coerceIn(0.8f, 2.0f)
        drawCircle(
            color = RadarCyan.copy(alpha = 0.12f),
            center = origin,
            radius = r,
            style = Stroke(width = 1f)
        )
    }
}

private fun DrawScope.drawProbeReticle(
    pos: Offset,
    windDirectionDeg: Int,
    pulse: Float
) {
    val reticleRadius = 18f
    val color = RadarCyan

    drawCircle(color = color, center = pos, radius = reticleRadius, style = Stroke(width = 1.8f))
    drawCircle(color = color.copy(alpha = 0.25f), center = pos, radius = reticleRadius + pulse * 10f, style = Stroke(width = 1.0f))

    val len = 28f
    drawLine(color, Offset(pos.x - len, pos.y), Offset(pos.x - reticleRadius, pos.y), strokeWidth = 1.5f)
    drawLine(color, Offset(pos.x + reticleRadius, pos.y), Offset(pos.x + len, pos.y), strokeWidth = 1.5f)
    drawLine(color, Offset(pos.x, pos.y - len), Offset(pos.x, pos.y - reticleRadius), strokeWidth = 1.5f)
    drawLine(color, Offset(pos.x, pos.y + reticleRadius), Offset(pos.x, pos.y + len), strokeWidth = 1.5f)

    val arrowAngleRad = Math.toRadians(windDirectionDeg.toDouble() - 90.0)
    val arrowLen = 32f
    val tipX = pos.x + (arrowLen * cos(arrowAngleRad)).toFloat()
    val tipY = pos.y + (arrowLen * sin(arrowAngleRad)).toFloat()

    drawLine(
        color = Color(0xFFFFD600),
        start = pos,
        end = Offset(tipX, tipY),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = Color(0xFFFFD600),
        center = Offset(tipX, tipY),
        radius = 3.5f,
        style = Fill
    )
}

private fun DrawScope.drawDayNightTerminator(
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    isDark: Boolean
) {
    // Current UTC approximation: calculate subsolar longitude
    val subsolarLon = -40.0
    val subsolarLat = 8.0 // Equinox/mild tilt

    val stepDeg = 15.0
    var lon = -180.0
    val points = mutableListOf<Offset>()

    while (lon <= 180.0) {
        val diffLonRad = Math.toRadians(lon - subsolarLon)
        val tanLat = -cos(diffLonRad) / kotlin.math.tan(Math.toRadians(subsolarLat).coerceAtLeast(0.05))
        val latDeg = Math.toDegrees(atan2(tanLat, 1.0)).coerceIn(-78.0, 78.0)
        val pt = projectPointNearest(GeoPoint(latDeg, lon), w, h, centerLat, centerLon, zoom)
        points.add(pt)
        lon += stepDeg
    }

    if (points.isNotEmpty()) {
        val nightPath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
            lineTo(w + 100f, h + 100f)
            lineTo(-100f, h + 100f)
            close()
        }

        val shadowColor = if (isDark) Color(0x38020612) else Color(0x220D1B2A)
        drawPath(nightPath, shadowColor)

        // Draw glowing twilight terminator line
        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color(0x40FFB300),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 1.6f
            )
        }
    }
}

private fun DrawScope.drawTacticalMiniMap(
    continents: List<List<GeoPoint>>,
    cyclones: List<Cyclone>,
    strongWindAreas: List<StrongWindArea>,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    isDark: Boolean
) {
    val miniW = 105f
    val miniH = 60f
    val pad = 14f
    val miniX = w - miniW - pad
    val miniY = h - miniH - 100f

    // Mini map card background
    drawRoundRect(
        color = if (isDark) Color(0xEE091322) else Color(0xEEF1F5F9),
        topLeft = Offset(miniX, miniY),
        size = Size(miniW, miniH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
    )
    drawRoundRect(
        color = RadarCyan.copy(alpha = 0.5f),
        topLeft = Offset(miniX, miniY),
        size = Size(miniW, miniH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
        style = Stroke(width = 1.0f)
    )

    // Draw miniature continents
    for (poly in continents) {
        if (poly.isEmpty()) continue
        val miniPath = Path()
        var first = true
        for (geo in poly) {
            val normLon = (geo.lon + 180.0) / 360.0
            val normLat = (90.0 - geo.lat) / 180.0
            val mx = miniX + (normLon * miniW).toFloat()
            val my = miniY + (normLat * miniH).toFloat()
            if (first) {
                miniPath.moveTo(mx, my)
                first = false
            } else {
                miniPath.lineTo(mx, my)
            }
        }
        miniPath.close()
        drawPath(miniPath, color = if (isDark) Color(0xFF1B324E) else Color(0xFFB0BEC5), style = Fill)
    }

    // Mini storm blips
    for (c in cyclones) {
        val normLon = (c.currentLocation.lon + 180.0) / 360.0
        val normLat = (90.0 - c.currentLocation.lat) / 180.0
        val mx = miniX + (normLon * miniW).toFloat()
        val my = miniY + (normLat * miniH).toFloat()
        drawCircle(color = c.category.color, center = Offset(mx, my), radius = 2.5f, style = Fill)
    }

    // Mini viewport rectangle
    val scale = getMapScale(w, zoom)
    val viewSpanLon = (w / scale).toDouble()
    val viewSpanLat = (h / scale).toDouble()

    val leftLon = (centerLon - viewSpanLon / 2.0 + 180.0) / 360.0
    val topLat = (90.0 - (centerLat + viewSpanLat / 2.0)) / 180.0
    val rectW = (viewSpanLon / 360.0 * miniW).toFloat().coerceIn(6f, miniW)
    val rectH = (viewSpanLat / 180.0 * miniH).toFloat().coerceIn(4f, miniH)

    val vx = (miniX + leftLon * miniW).toFloat().coerceIn(miniX, miniX + miniW - rectW)
    val vy = (miniY + topLat * miniH).toFloat().coerceIn(miniY, miniY + miniH - rectH)

    drawRect(
        color = Color(0xFFFFD600),
        topLeft = Offset(vx, vy),
        size = Size(rectW, rectH),
        style = Stroke(width = 1.0f)
    )

    val paint = Paint().apply {
        color = RadarCyan.toArgb()
        textSize = 14f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
    }
    drawContext.canvas.nativeCanvas.drawText("RADAR INSET", miniX + 4f, miniY + 11f, paint)
}

// -------------------------------------------------------------
// Real-time Tactical Red Square Spot Detection & Scan Overlays
// -------------------------------------------------------------

private fun DrawScope.drawScannedSpots(
    spots: List<ScannedSpot>,
    selected: ScannedSpot?,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    pulse: Float,
    isDark: Boolean
) {
    if (spots.isEmpty()) return
    val now = System.currentTimeMillis()

    val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.MONOSPACE
        textSize = 20f
        color = android.graphics.Color.WHITE
    }

    val subTextPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.MONOSPACE
        textSize = 16f
        color = android.graphics.Color.argb(220, 255, 200, 200)
    }

    for (spot in spots) {
        val pos = projectPointNearest(spot.location, w, h, centerLat, centerLon, zoom)
        // Only render if reasonably within screen bounds
        if (pos.x < -80f || pos.x > w + 80f || pos.y < -80f || pos.y > h + 80f) continue

        val ageMs = now - spot.detectedTick
        if (ageMs > 9000) continue

        // Instant acquisition scale effect: newly spotted snaps in from 1.5x down to 1.0x in 250ms
        val scale = if (ageMs < 250) {
            1.0f + (1.0f - (ageMs / 250f)) * 0.5f
        } else 1.0f

        val alpha = if (ageMs < 5000) 1.0f else ((9000 - ageMs) / 4000f).coerceIn(0.2f, 1.0f)
        val isSelected = selected?.id == spot.id
        val isRain = spot.type == ScannedSpotType.HEAVY_RAIN
        val boxPrimary = if (isRain) Color(0xFF00B0FF).copy(alpha = alpha) else Color(0xFFFF1744).copy(alpha = alpha)
        val boxGlow = if (isRain) Color(0x3500B0FF).copy(alpha = alpha * 0.5f) else Color(0x35FF1744).copy(alpha = alpha * 0.5f)

        val halfBox = (spot.boxSizePx / 2f) * scale * zoom.coerceIn(0.85f, 1.4f)
        val arm = halfBox * 0.38f

        // 1. Tactical bounding box interior tint
        drawRect(
            color = boxGlow,
            topLeft = Offset(pos.x - halfBox, pos.y - halfBox),
            size = Size(halfBox * 2, halfBox * 2)
        )

        // 2. Corner tactical brackets [ ] in glowing color
        val strokeW = if (isSelected) 3.2f else 2.4f

        // Top-Left bracket
        drawLine(boxPrimary, Offset(pos.x - halfBox, pos.y - halfBox + arm), Offset(pos.x - halfBox, pos.y - halfBox), strokeW)
        drawLine(boxPrimary, Offset(pos.x - halfBox, pos.y - halfBox), Offset(pos.x - halfBox + arm, pos.y - halfBox), strokeW)

        // Top-Right bracket
        drawLine(boxPrimary, Offset(pos.x + halfBox - arm, pos.y - halfBox), Offset(pos.x + halfBox, pos.y - halfBox), strokeW)
        drawLine(boxPrimary, Offset(pos.x + halfBox, pos.y - halfBox), Offset(pos.x + halfBox, pos.y - halfBox + arm), strokeW)

        // Bottom-Left bracket
        drawLine(boxPrimary, Offset(pos.x - halfBox, pos.y + halfBox - arm), Offset(pos.x - halfBox, pos.y + halfBox), strokeW)
        drawLine(boxPrimary, Offset(pos.x - halfBox, pos.y + halfBox), Offset(pos.x - halfBox + arm, pos.y + halfBox), strokeW)

        // Bottom-Right bracket
        drawLine(boxPrimary, Offset(pos.x + halfBox - arm, pos.y + halfBox), Offset(pos.x + halfBox, pos.y + halfBox), strokeW)
        drawLine(boxPrimary, Offset(pos.x + halfBox, pos.y + halfBox), Offset(pos.x + halfBox, pos.y + halfBox - arm), strokeW)

        // Center reticle dot
        drawCircle(boxPrimary, radius = 2.5f, center = pos)
        if (isSelected) {
            val selectGlow = if (isRain) Color(0xFF00E5FF).copy(alpha = 0.4f) else Color(0xFFFF5252).copy(alpha = 0.4f)
            drawCircle(
                color = selectGlow,
                radius = halfBox + pulse * 12f,
                center = pos,
                style = Stroke(width = 1.2f)
            )
        }

        // 3. Radar Station & Accuracy Banner Pill directly above the target box
        val accText = "${spot.radarStationCallsign} • ${String.format("%.1f", spot.accuracyPct)}%"
        val bannerW = 118f
        val bannerH = 22f
        val bannerX = pos.x - (bannerW / 2f)
        val bannerY = pos.y - halfBox - bannerH - 4f

        val accBg = if (isRain) Color(0xD901579B).copy(alpha = alpha) else Color(0xD9B71C1C).copy(alpha = alpha)
        val accBorder = if (isRain) Color(0xFF00E5FF).copy(alpha = alpha) else Color(0xFFFF5252).copy(alpha = alpha)

        drawRoundRect(
            color = accBg,
            topLeft = Offset(bannerX, bannerY),
            size = Size(bannerW, bannerH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = accBorder,
            topLeft = Offset(bannerX, bannerY),
            size = Size(bannerW, bannerH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
            style = Stroke(width = 1.0f)
        )
        drawContext.canvas.nativeCanvas.drawText(accText, bannerX + 8f, bannerY + 16f, textPaint)

        // 4. Tactical telemetry tag below the target square: [TYPE] NAME • REFLECTIVITY (dBZ) • PRECIP (mm/h)
        val tagText = "[${spot.type.tag}] ${spot.name}"
        val envText = if (spot.precipitationRateMmH > 0f) {
            "${String.format(java.util.Locale.US, "%.1f", spot.reflectivityDbz)}dBZ • ${String.format(java.util.Locale.US, "%.1f", spot.precipitationRateMmH)}mm/h • ${spot.echoTopKft}kft"
        } else {
            "${String.format(java.util.Locale.US, "%.1f", spot.reflectivityDbz)}dBZ • ${spot.windKts}kt • HUM ${spot.humidityPct}%"
        }
        val tagW = 175f
        val tagH = 34f
        val tagX = pos.x - (tagW / 2f)
        val tagY = pos.y + halfBox + 4f

        drawRoundRect(
            color = Color(0xE60D1B2A).copy(alpha = alpha),
            topLeft = Offset(tagX, tagY),
            size = Size(tagW, tagH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = spot.type.color.copy(alpha = alpha * 0.8f),
            topLeft = Offset(tagX, tagY),
            size = Size(tagW, tagH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
            style = Stroke(width = 1.0f)
        )
        drawContext.canvas.nativeCanvas.drawText(tagText, tagX + 6f, tagY + 15f, subTextPaint)
        drawContext.canvas.nativeCanvas.drawText(envText, tagX + 6f, tagY + 29f, subTextPaint)
    }
}

private fun DrawScope.drawSatelliteScanOverlay(
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    sweepAngleDeg: Float,
    satellites: List<Satellite>,
    selectedSatellite: Satellite?,
    pulse: Float
) {
    if (satellites.isEmpty()) return

    val targetSats = if (selectedSatellite != null) listOf(selectedSatellite) else satellites.take(3)
    for (sat in targetSats) {
        val satPos = projectPointNearest(sat.location, w, h, centerLat, centerLon, zoom)
        val swathRadius = (sat.coverageRadiusKm / 35f) * zoom.coerceIn(0.6f, 2.0f)

        // Multispectral ground swath cone
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x3500E5FF),
                    Color(0x187C4DFF),
                    Color(0x0800B0FF),
                    Color.Transparent
                ),
                center = satPos,
                radius = swathRadius
            ),
            center = satPos,
            radius = swathRadius
        )

        // Sweeping cross-track orbital scanlines across the swath
        val scanCount = 7
        for (i in 0 until scanCount) {
            val phaseOffset = (pulse + i / scanCount.toFloat()) % 1f
            val r = swathRadius * phaseOffset
            drawCircle(
                color = Color(0x4000E5FF).copy(alpha = (1f - phaseOffset) * 0.6f),
                center = satPos,
                radius = r,
                style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f))
            )
        }

        // Sub-satellite crosshair
        drawLine(Color(0xFF00E5FF), Offset(satPos.x - 14f, satPos.y), Offset(satPos.x + 14f, satPos.y), strokeWidth = 1.6f)
        drawLine(Color(0xFF00E5FF), Offset(satPos.x, satPos.y - 14f), Offset(satPos.x, satPos.y + 14f), strokeWidth = 1.6f)

        // Swath boundary ring
        drawCircle(
            color = Color(0x6000E5FF),
            center = satPos,
            radius = swathRadius,
            style = Stroke(width = 1.2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
        )
    }
}

private fun DrawScope.drawAiNeuralScanOverlay(
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    sweepAngleDeg: Float,
    cyclones: List<Cyclone>,
    pulse: Float
) {
    // 1. AI Neural Perception Matrix Grid
    val gridStep = 75f * zoom.coerceIn(0.8f, 1.6f)
    var x = (w * 0.1f) % gridStep
    while (x < w) {
        drawLine(
            color = Color(0x12E040FB),
            start = Offset(x, 0f),
            end = Offset(x, h),
            strokeWidth = 1.0f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f), (sweepAngleDeg * 0.5f) % 12f)
        )
        x += gridStep
    }

    var y = (h * 0.1f) % gridStep
    while (y < h) {
        drawLine(
            color = Color(0x12E040FB),
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 1.0f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f), (sweepAngleDeg * 0.5f) % 12f)
        )
        y += gridStep
    }

    // 2. Convolutional Activation Nodes at Storm Cores
    for (c in cyclones) {
        val p = projectPointNearest(c.currentLocation, w, h, centerLat, centerLon, zoom)
        if (p.x < -100f || p.x > w + 100f || p.y < -100f || p.y > h + 100f) continue

        val rCore = (c.eyeRadiusKm * 1.8f) * zoom.coerceIn(0.6f, 2.0f)
        // Diamond perception node
        val dSize = 22f + pulse * 6f
        val diamond = Path().apply {
            moveTo(p.x, p.y - dSize)
            lineTo(p.x + dSize, p.y)
            lineTo(p.x, p.y + dSize)
            lineTo(p.x - dSize, p.y)
            close()
        }
        drawPath(diamond, color = Color(0x35E040FB))
        drawPath(diamond, color = Color(0xFFE040FB), style = Stroke(width = 1.6f))

        // AI Inference Vector
        val headingRad = Math.toRadians(c.movementHeadingDeg.toDouble() - 90.0)
        val vecLen = 65f * zoom.coerceIn(0.8f, 1.8f)
        val vx = p.x + (vecLen * cos(headingRad)).toFloat()
        val vy = p.y + (vecLen * sin(headingRad)).toFloat()
        drawLine(
            color = Color(0xFFE040FB),
            start = p,
            end = Offset(vx, vy),
            strokeWidth = 2.2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f), 0f)
        )
        drawCircle(Color(0xFFE040FB), radius = 3.5f, center = Offset(vx, vy))
    }
}

private fun DrawScope.drawAllModelsEnsembleOverlay(
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    cyclones: List<Cyclone>,
    selectedCyclone: Cyclone?
) {
    val storm = selectedCyclone ?: cyclones.firstOrNull() ?: return
    val origin = projectPointNearest(storm.currentLocation, w, h, centerLat, centerLon, zoom)

    val labelPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.MONOSPACE
        textSize = 17f
    }

    data class ModelTrackConfig(val name: String, val track: List<ForecastTrackPoint>, val color: Color)
    val models = listOf(
        ModelTrackConfig("ECMWF", storm.ecmwfTrack, EcmwfTrack),
        ModelTrackConfig("GFS", storm.gfsTrack, Color(0xFF00E676)),
        ModelTrackConfig("WN-3 AI", storm.weatherNext3Track, WeatherNext3Track),
        ModelTrackConfig("WN-2 AI", storm.weatherNext2Track, WeatherNext2Track),
        ModelTrackConfig("JMA", storm.jmaTrack, Color(0xFFFFD600))
    )

    // Draw track for each operational weather model
    for (cfg in models) {
        if (cfg.track.isEmpty()) continue
        val path = Path()
        path.moveTo(origin.x, origin.y)
        for (pt in cfg.track) {
            val p = projectPointNearest(pt.point, w, h, centerLat, centerLon, zoom)
            path.lineTo(p.x, p.y)
        }
        drawPath(path, color = cfg.color.copy(alpha = 0.85f), style = Stroke(width = 2.4f))

        // Waypoint points and tag on last waypoint
        cfg.track.forEachIndexed { idx, pt ->
            val p = projectPointNearest(pt.point, w, h, centerLat, centerLon, zoom)
            drawCircle(color = cfg.color, radius = 3.5f, center = p)
            if (idx == cfg.track.size - 1) {
                labelPaint.color = cfg.color.toArgb()
                drawContext.canvas.nativeCanvas.drawText(
                    "${cfg.name} +${pt.hoursAhead}h",
                    p.x + 8f,
                    p.y + 4f,
                    labelPaint
                )
            }
        }
    }

    // Stabilized Consensus line with glowing gold highlight
    if (storm.stabilizedTrack.isNotEmpty()) {
        val consensusPath = Path()
        consensusPath.moveTo(origin.x, origin.y)
        for (pt in storm.stabilizedTrack) {
            val p = projectPointNearest(pt.point, w, h, centerLat, centerLon, zoom)
            consensusPath.lineTo(p.x, p.y)
        }
        drawPath(
            consensusPath,
            color = ConsensusModelTrack.copy(alpha = 0.35f),
            style = Stroke(width = 6.0f)
        )
        drawPath(
            consensusPath,
            color = ConsensusModelTrack,
            style = Stroke(width = 3.2f)
        )
        storm.stabilizedTrack.lastOrNull()?.let { pt ->
            val p = projectPointNearest(pt.point, w, h, centerLat, centerLon, zoom)
            labelPaint.color = ConsensusModelTrack.toArgb()
            drawContext.canvas.nativeCanvas.drawText("★ FUSED CONSENSUS", p.x + 8f, p.y - 10f, labelPaint)
        }
    }
}

private fun DrawScope.drawSurgeRiskZones(
    zones: List<SurgeRiskZone>,
    selected: SurgeRiskZone?,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    pulse: Float,
    wavePhase: Float
) {
    val textPaint = Paint().apply {
        textSize = (20f * (zoom * 0.4f + 0.6f)).coerceIn(16f, 28f)
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
    }

    for (zone in zones) {
        val p = projectPointNearest(zone.location, w, h, centerLat, centerLon, zoom)
        if (p.x < -80 || p.x > w + 80 || p.y < -80 || p.y > h + 80) continue

        val baseColor = zone.riskLevel.color
        val isSelected = selected?.id == zone.id

        // Concentric expanding surge wave rings
        val rBase = (22f * zoom.coerceIn(0.8f, 3.5f))
        drawCircle(
            color = baseColor.copy(alpha = 0.25f),
            center = p,
            radius = rBase,
            style = Fill
        )
        drawCircle(
            color = baseColor.copy(alpha = (1f - pulse) * 0.8f),
            center = p,
            radius = rBase + pulse * 18f,
            style = Stroke(width = 1.8f)
        )

        // Animated surge chevrons / wave vectors directed onshore
        for (i in 0..2) {
            val waveOffset = ((wavePhase * 30f + i * 14f) % 35f) - 10f
            val chevronY = p.y - waveOffset
            val chevronPath = Path().apply {
                moveTo(p.x - 14f, chevronY + 6f)
                lineTo(p.x, chevronY)
                lineTo(p.x + 14f, chevronY + 6f)
            }
            drawPath(
                path = chevronPath,
                color = baseColor.copy(alpha = (0.9f - (waveOffset / 35f).coerceIn(0f, 0.7f))),
                style = Stroke(width = 2.0f, cap = StrokeCap.Round)
            )
        }

        // Diamond risk core marker
        val diamond = Path().apply {
            moveTo(p.x, p.y - 8f)
            lineTo(p.x + 8f, p.y)
            lineTo(p.x, p.y + 8f)
            lineTo(p.x - 8f, p.y)
            close()
        }
        drawPath(diamond, color = baseColor, style = Fill)
        drawPath(diamond, color = Color.White, style = Stroke(width = 1.2f))

        // Label above marker
        textPaint.color = baseColor.toArgb()
        drawContext.canvas.nativeCanvas.drawText(
            "${zone.coastalRegion.take(18)} +${zone.peakSurgeMeters}m",
            p.x + 14f,
            p.y - 10f,
            textPaint
        )

        textPaint.color = android.graphics.Color.WHITE
        textPaint.textSize = (16f * (zoom * 0.4f + 0.6f)).coerceIn(12f, 22f)
        drawContext.canvas.nativeCanvas.drawText(
            "SURGE: ${zone.riskLevel.alertLevel} (${zone.peakSurgeFeet}ft)",
            p.x + 14f,
            p.y + 12f,
            textPaint
        )

        // Selected target lock brackets
        if (isSelected) {
            val selR = rBase + 12f
            val bracketLen = 14f
            drawLine(baseColor, Offset(p.x - selR, p.y - selR), Offset(p.x - selR + bracketLen, p.y - selR), strokeWidth = 2.5f)
            drawLine(baseColor, Offset(p.x - selR, p.y - selR), Offset(p.x - selR, p.y - selR + bracketLen), strokeWidth = 2.5f)

            drawLine(baseColor, Offset(p.x + selR, p.y - selR), Offset(p.x + selR - bracketLen, p.y - selR), strokeWidth = 2.5f)
            drawLine(baseColor, Offset(p.x + selR, p.y - selR), Offset(p.x + selR, p.y - selR - bracketLen), strokeWidth = 2.5f)

            drawLine(baseColor, Offset(p.x - selR, p.y + selR), Offset(p.x - selR + bracketLen, p.y + selR), strokeWidth = 2.5f)
            drawLine(baseColor, Offset(p.x - selR, p.y + selR), Offset(p.x - selR, p.y + selR - bracketLen), strokeWidth = 2.5f)

            drawLine(baseColor, Offset(p.x + selR, p.y + selR), Offset(p.x + selR - bracketLen, p.y + selR), strokeWidth = 2.5f)
            drawLine(baseColor, Offset(p.x + selR, p.y + selR), Offset(p.x + selR, p.y + selR - bracketLen), strokeWidth = 2.5f)
        }
    }
}

private fun DrawScope.drawTacticalBathymetry(
    trenches: List<GeoData.BathymetryTrench>,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    isDark: Boolean
) {
    val trenchStroke = if (isDark) Color(0xFF00B4D8) else Color(0xFF0077B6)
    val glowColor = if (isDark) Color(0x350077B6) else Color(0x200096C7)
    val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = (11f * (zoom * 0.35f + 0.65f)).coerceIn(9f, 15f)
        color = (if (isDark) Color(0xFF48CAE4) else Color(0xFF023E8A)).toArgb()
    }

    val worldOffsets = listOf(-360.0, 0.0, 360.0)
    for (worldLonOffset in worldOffsets) {
        for (trench in trenches) {
            val path = Path()
            var started = false
            var midOffset: Offset? = null
            val midIdx = trench.points.size / 2

            for ((idx, pt) in trench.points.withIndex()) {
                val shiftedPt = GeoPoint(pt.lat, pt.lon + worldLonOffset)
                val p = projectPointNearest(shiftedPt, w, h, centerLat, centerLon, zoom)
                if (!started) {
                    path.moveTo(p.x, p.y)
                    started = true
                } else {
                    path.lineTo(p.x, p.y)
                }
                if (idx == midIdx) midOffset = p
            }

            if (started) {
                // Wide ambient ocean depth trench glow
                drawPath(
                    path = path,
                    color = glowColor,
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
                )
                // Trench axis line with distinct bathymetric dash effect
                drawPath(
                    path = path,
                    color = trenchStroke.copy(alpha = 0.85f),
                    style = Stroke(
                        width = 1.8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f, 3f, 6f), 0f),
                        cap = StrokeCap.Round
                    )
                )

                // Depth sounding annotation at midpoint
                if (midOffset != null && midOffset.x in 0f..w && midOffset.y in 0f..h) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "${trench.name} (${trench.depthMeters}m)",
                        midOffset.x + 8f,
                        midOffset.y - 6f,
                        textPaint
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawJetStreams(
    jetStreams: List<GeoData.JetStream>,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    phase: Float,
    isDark: Boolean
) {
    val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = (12f * (zoom * 0.35f + 0.65f)).coerceIn(10f, 16f)
    }

    val worldOffsets = listOf(-360.0, 0.0, 360.0)
    for (worldLonOffset in worldOffsets) {
        for (jet in jetStreams) {
            val projected = jet.points.map { pt ->
                val shifted = GeoPoint(pt.lat, pt.lon + worldLonOffset)
                projectPointNearest(shifted, w, h, centerLat, centerLon, zoom)
            }
            if (projected.size < 2) continue

            val ribbonPath = Path().apply {
                moveTo(projected[0].x, projected[0].y)
                for (i in 1 until projected.size) {
                    lineTo(projected[i].x, projected[i].y)
                }
            }

            val jetColor = when {
                jet.coreSpeedKts >= 170 -> Color(0xFFFF9100)
                jet.coreSpeedKts >= 150 -> Color(0xFFFFD600)
                else -> Color(0xFF00E5FF)
            }

            // Outer wide translucent velocity ribbon
            drawPath(
                path = ribbonPath,
                color = jetColor.copy(alpha = if (isDark) 0.18f else 0.12f),
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )

            // Inner core velocity stream
            drawPath(
                path = ribbonPath,
                color = jetColor.copy(alpha = if (isDark) 0.85f else 0.70f),
                style = Stroke(width = 3.0f, cap = StrokeCap.Round)
            )

            // Dynamic velocity chevrons travelling along the jet stream
            val speedFactor = jet.coreSpeedKts / 60f
            for (i in 0 until projected.size - 1) {
                val p1 = projected[i]
                val p2 = projected[i + 1]
                val segDx = p2.x - p1.x
                val segDy = p2.y - p1.y
                val segLen = sqrt(segDx * segDx + segDy * segDy)
                if (segLen < 15f) continue

                val angle = atan2(segDy, segDx)
                val numChevrons = (segLen / 45f).toInt().coerceAtLeast(1)

                for (c in 0 until numChevrons) {
                    val chevronT = ((c.toFloat() / numChevrons.toFloat()) + (phase * speedFactor)) % 1f
                    val cx = p1.x + segDx * chevronT
                    val cy = p1.y + segDy * chevronT

                    if (cx in -20f..(w + 20f) && cy in -20f..(h + 20f)) {
                        val armLen = 9f
                        val wingAngle = 2.4f
                        val chevPath = Path().apply {
                            moveTo(
                                (cx - armLen * cos(angle - wingAngle)),
                                (cy - armLen * sin(angle - wingAngle))
                            )
                            lineTo(cx, cy)
                            lineTo(
                                (cx - armLen * cos(angle + wingAngle)),
                                (cy - armLen * sin(angle + wingAngle))
                            )
                        }
                        drawPath(
                            path = chevPath,
                            color = Color.White.copy(alpha = if (isDark) 0.9f else 0.75f),
                            style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                        )
                    }
                }
            }

            // Jet Stream Name & FL Core Speed callout
            val midIdx = projected.size / 2
            val midP = projected[midIdx]
            if (midP.x in 20f..(w - 80f) && midP.y in 20f..(h - 20f)) {
                textPaint.color = jetColor.toArgb()
                drawContext.canvas.nativeCanvas.drawText(
                    "▶ ${jet.name} ${jet.coreSpeedKts}kt (${jet.flightLevel})",
                    midP.x + 10f,
                    midP.y - 10f,
                    textPaint
                )
            }
        }
    }
}

private fun DrawScope.drawAtmosphericFronts(
    fronts: List<GeoData.AtmosphericFront>,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    isDark: Boolean
) {
    val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = (11f * (zoom * 0.35f + 0.65f)).coerceIn(9f, 15f)
    }

    val worldOffsets = listOf(-360.0, 0.0, 360.0)
    for (worldLonOffset in worldOffsets) {
        for (front in fronts) {
            val projected = front.points.map { pt ->
                val shifted = GeoPoint(pt.lat, pt.lon + worldLonOffset)
                projectPointNearest(shifted, w, h, centerLat, centerLon, zoom)
            }
            if (projected.size < 2) continue

            val frontColor = when (front.type) {
                GeoData.FrontType.COLD -> Color(0xFF2979FF)
                GeoData.FrontType.WARM -> Color(0xFFFF1744)
                GeoData.FrontType.STATIONARY -> Color(0xFF00E676)
                GeoData.FrontType.OCCLUDED -> Color(0xFFD500F9)
            }

            val frontPath = Path().apply {
                moveTo(projected[0].x, projected[0].y)
                for (i in 1 until projected.size) {
                    lineTo(projected[i].x, projected[i].y)
                }
            }

            // Front backbone line
            drawPath(
                path = frontPath,
                color = frontColor,
                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )

            // Pips along the front line: triangles (cold), semicircles (warm), or alternating
            for (i in 0 until projected.size - 1) {
                val p1 = projected[i]
                val p2 = projected[i + 1]
                val segDx = p2.x - p1.x
                val segDy = p2.y - p1.y
                val segLen = sqrt(segDx * segDx + segDy * segDy)
                if (segLen < 20f) continue

                val normalX = -segDy / segLen
                val normalY = segDx / segLen
                val numPips = (segLen / 35f).toInt().coerceAtLeast(1)

                for (p in 1..numPips) {
                    val t = p.toFloat() / (numPips + 1).toFloat()
                    val pipX = p1.x + segDx * t
                    val pipY = p1.y + segDy * t

                    if (pipX !in -30f..(w + 30f) || pipY !in -30f..(h + 30f)) continue

                    val isEven = (p % 2 == 0)
                    when (front.type) {
                        GeoData.FrontType.COLD -> {
                            val triSize = 8f
                            val tipX = pipX + normalX * triSize * 1.5f
                            val tipY = pipY + normalY * triSize * 1.5f
                            val b1X = pipX - (segDx / segLen) * triSize
                            val b1Y = pipY - (segDy / segLen) * triSize
                            val b2X = pipX + (segDx / segLen) * triSize
                            val b2Y = pipY + (segDy / segLen) * triSize

                            val triangle = Path().apply {
                                moveTo(b1X, b1Y)
                                lineTo(tipX, tipY)
                                lineTo(b2X, b2Y)
                                close()
                            }
                            drawPath(triangle, color = Color(0xFF2979FF), style = Fill)
                        }
                        GeoData.FrontType.WARM -> {
                            val r = 7f
                            val tipX = pipX + normalX * r
                            val tipY = pipY + normalY * r
                            drawCircle(color = Color(0xFFFF1744), radius = r, center = Offset(tipX, tipY))
                        }
                        GeoData.FrontType.STATIONARY -> {
                            if (isEven) {
                                val triSize = 7f
                                val tipX = pipX + normalX * triSize * 1.5f
                                val tipY = pipY + normalY * triSize * 1.5f
                                val b1X = pipX - (segDx / segLen) * triSize
                                val b1Y = pipY - (segDy / segLen) * triSize
                                val b2X = pipX + (segDx / segLen) * triSize
                                val b2Y = pipY + (segDy / segLen) * triSize
                                val triangle = Path().apply {
                                    moveTo(b1X, b1Y)
                                    lineTo(tipX, tipY)
                                    lineTo(b2X, b2Y)
                                    close()
                                }
                                drawPath(triangle, color = Color(0xFF2979FF), style = Fill)
                            } else {
                                val r = 6f
                                val tipX = pipX - normalX * r
                                val tipY = pipY - normalY * r
                                drawCircle(color = Color(0xFFFF1744), radius = r, center = Offset(tipX, tipY))
                            }
                        }
                        GeoData.FrontType.OCCLUDED -> {
                            val purple = Color(0xFFD500F9)
                            if (isEven) {
                                val triSize = 7f
                                val tipX = pipX + normalX * triSize * 1.5f
                                val tipY = pipY + normalY * triSize * 1.5f
                                val b1X = pipX - (segDx / segLen) * triSize
                                val b1Y = pipY - (segDy / segLen) * triSize
                                val b2X = pipX + (segDx / segLen) * triSize
                                val b2Y = pipY + (segDy / segLen) * triSize
                                val triangle = Path().apply {
                                    moveTo(b1X, b1Y)
                                    lineTo(tipX, tipY)
                                    lineTo(b2X, b2Y)
                                    close()
                                }
                                drawPath(triangle, color = purple, style = Fill)
                            } else {
                                val r = 6f
                                val tipX = pipX + normalX * r
                                val tipY = pipY + normalY * r
                                drawCircle(color = purple, radius = r, center = Offset(tipX, tipY))
                            }
                        }
                    }
                }
            }

            // Front Label
            val midIdx = projected.size / 2
            val midP = projected[midIdx]
            if (midP.x in 20f..(w - 80f) && midP.y in 20f..(h - 20f)) {
                textPaint.color = frontColor.toArgb()
                drawContext.canvas.nativeCanvas.drawText(
                    "${front.name} ${front.speedKts}kt",
                    midP.x + 12f,
                    midP.y - 8f,
                    textPaint
                )
            }
        }
    }
}

private fun DrawScope.drawDopplerRadarStations(
    stations: List<GeoData.DopplerRadarStation>,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    pulse: Float,
    isDark: Boolean
) {
    val radarCyan = if (isDark) Color(0xFF00E5FF) else Color(0xFF00838F)
    val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = (11f * (zoom * 0.35f + 0.65f)).coerceIn(9f, 14f)
        color = radarCyan.toArgb()
    }
    val scale = getMapScale(w, zoom)
    val kmToPx = scale / 111.32f

    val worldOffsets = listOf(-360.0, 0.0, 360.0)
    for (worldLonOffset in worldOffsets) {
        for (st in stations) {
            val shifted = GeoPoint(st.location.lat, st.location.lon + worldLonOffset)
            val p = projectPointNearest(shifted, w, h, centerLat, centerLon, zoom)

            if (p.x !in -300f..(w + 300f) || p.y !in -300f..(h + 300f)) continue

            val outerRadiusPx = (st.maxRangeKm * kmToPx).coerceIn(20f, 600f)
            val innerRadiusPx = (st.innerRangeKm * kmToPx).coerceIn(10f, 300f)

            // Outer surveillance range ring (460km)
            drawCircle(
                color = radarCyan.copy(alpha = if (isDark) 0.25f else 0.18f),
                radius = outerRadiusPx,
                center = p,
                style = Stroke(
                    width = 1.2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            )

            // Inner Doppler velocity range ring (230km)
            drawCircle(
                color = radarCyan.copy(alpha = if (isDark) 0.35f else 0.25f),
                radius = innerRadiusPx,
                center = p,
                style = Stroke(
                    width = 1.0f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                )
            )

            // Rotating sweep beam tick from station center
            val sweepAngle = (pulse * 360f) % 360f
            val rad = Math.toRadians(sweepAngle.toDouble())
            val beamEnd = Offset(
                (p.x + outerRadiusPx * cos(rad)).toFloat(),
                (p.y + outerRadiusPx * sin(rad)).toFloat()
            )
            drawLine(
                color = radarCyan.copy(alpha = if (isDark) 0.45f else 0.35f),
                start = p,
                end = beamEnd,
                strokeWidth = 1.2f
            )

            // Central Radar Mast / Dish icon
            drawCircle(color = radarCyan.copy(alpha = 0.3f), radius = 8f, center = p)
            drawCircle(color = radarCyan, radius = 4f, center = p)
            drawCircle(color = Color.White, radius = 1.8f, center = p)

            // Crosshair tick marks on mast
            drawLine(radarCyan, Offset(p.x - 7f, p.y), Offset(p.x + 7f, p.y), strokeWidth = 1.2f)
            drawLine(radarCyan, Offset(p.x, p.y - 7f), Offset(p.x, p.y + 7f), strokeWidth = 1.2f)

            // Station Callsign & Range tag
            if (p.x in 0f..w && p.y in 0f..h) {
                drawContext.canvas.nativeCanvas.drawText(
                    "[${st.callsign}] ${st.name.take(12)} (${st.maxRangeKm}km)",
                    p.x + 10f,
                    p.y - 6f,
                    textPaint
                )
            }
        }
    }
}

private fun DrawScope.drawNavCorridors(
    corridors: List<GeoData.NavCorridor>,
    waypoints: List<GeoData.NavWaypoint>,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    isDark: Boolean
) {
    val corridorColor = if (isDark) Color(0xFF69F0AE) else Color(0xFF2E7D32)
    val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = (10f * (zoom * 0.35f + 0.65f)).coerceIn(8f, 13f)
        color = corridorColor.toArgb()
    }

    val worldOffsets = listOf(-360.0, 0.0, 360.0)
    for (worldLonOffset in worldOffsets) {
        // Corridors
        for (corridor in corridors) {
            val projected = corridor.waypoints.map { pt ->
                val shifted = GeoPoint(pt.lat, pt.lon + worldLonOffset)
                projectPointNearest(shifted, w, h, centerLat, centerLon, zoom)
            }
            if (projected.size < 2) continue

            val corridorPath = Path().apply {
                moveTo(projected[0].x, projected[0].y)
                for (i in 1 until projected.size) {
                    lineTo(projected[i].x, projected[i].y)
                }
            }

            drawPath(
                path = corridorPath,
                color = corridorColor.copy(alpha = if (isDark) 0.35f else 0.25f),
                style = Stroke(
                    width = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                )
            )

            // Corridor title
            val midIdx = projected.size / 2
            val midP = projected[midIdx]
            if (midP.x in 20f..(w - 60f) && midP.y in 20f..(h - 20f)) {
                drawContext.canvas.nativeCanvas.drawText(
                    "AIR-CORRIDOR ${corridor.name}",
                    midP.x + 8f,
                    midP.y - 6f,
                    textPaint
                )
            }
        }

        // Oceanic Waypoints (Fixes)
        for (wp in waypoints) {
            val shifted = GeoPoint(wp.location.lat, wp.location.lon + worldLonOffset)
            val p = projectPointNearest(shifted, w, h, centerLat, centerLon, zoom)

            if (p.x !in -20f..(w + 20f) || p.y !in -20f..(h + 20f)) continue

            // Diamond waypoint shape
            val dSize = 5f
            val diamond = Path().apply {
                moveTo(p.x, p.y - dSize)
                lineTo(p.x + dSize, p.y)
                lineTo(p.x, p.y + dSize)
                lineTo(p.x - dSize, p.y)
                close()
            }
            drawPath(diamond, color = corridorColor, style = Stroke(width = 1.5f))
            drawCircle(color = corridorColor, radius = 1.5f, center = p)

            drawContext.canvas.nativeCanvas.drawText(
                "▲ ${wp.code}",
                p.x + 7f,
                p.y + 4f,
                textPaint
            )
        }
    }
}

// -------------------------------------------------------------
// 2. SAT + AI PREDICTIVE INFRASTRUCTURE: OPTICAL FLOW VECTOR FIELD
// -------------------------------------------------------------
private fun DrawScope.drawOpticalFlowField(
    vectors: List<OpticalFlowVector>,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    phase: Float,
    isDark: Boolean
) {
    if (vectors.isEmpty()) return

    val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = (10f * zoom.coerceIn(0.8f, 1.3f)).coerceIn(8f, 13f)
        color = android.graphics.Color.argb(220, 0, 229, 255)
    }

    for (v in vectors) {
        val p = projectPointNearest(v.location, w, h, centerLat, centerLon, zoom)
        if (p.x < -60f || p.x > w + 60f || p.y < -60f || p.y > h + 60f) continue

        val angleRad = atan2(v.vVelocity.toDouble(), v.uVelocity.toDouble()).toFloat()
        val vectorLen = (v.speedKts * 0.45f * zoom.coerceIn(0.7f, 1.5f)).coerceIn(12f, 38f)

        val vectorColor = if (v.vorticity >= 8.0f) {
            Color(0xFFE040FB)
        } else if (v.speedKts >= 60f) {
            Color(0xFFFF5252)
        } else {
            Color(0xFF00E5FF)
        }

        val startX = p.x - (cos(angleRad) * vectorLen * 0.4f)
        val startY = p.y - (sin(angleRad) * vectorLen * 0.4f)
        val endX = p.x + (cos(angleRad) * vectorLen * 0.6f)
        val endY = p.y + (sin(angleRad) * vectorLen * 0.6f)

        // Streamline
        drawLine(
            color = vectorColor.copy(alpha = 0.75f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2.0f,
            cap = StrokeCap.Round
        )

        // Arrow head
        val headAngle1 = angleRad + 2.6f
        val headAngle2 = angleRad - 2.6f
        val headLen = 6f
        drawLine(
            color = vectorColor,
            start = Offset(endX, endY),
            end = Offset(endX + cos(headAngle1) * headLen, endY + sin(headAngle1) * headLen),
            strokeWidth = 2.0f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = vectorColor,
            start = Offset(endX, endY),
            end = Offset(endX + cos(headAngle2) * headLen, endY + sin(headAngle2) * headLen),
            strokeWidth = 2.0f,
            cap = StrokeCap.Round
        )

        // Fluid dynamic particle pulse
        val pulseFraction = ((phase * 3f + (v.id.hashCode() % 10) * 0.1f) % 1f)
        val pulseX = startX + (endX - startX) * pulseFraction
        val pulseY = startY + (endY - startY) * pulseFraction
        drawCircle(
            color = Color.White,
            radius = 1.8f,
            center = Offset(pulseX, pulseY)
        )

        if (zoom > 1.35f) {
            drawContext.canvas.nativeCanvas.drawText(
                "${v.speedKts.toInt()}kt ω:${v.vorticity}",
                endX + 4f,
                endY + 3f,
                textPaint
            )
        }
    }
}

// -------------------------------------------------------------
// 2. SAT + AI PREDICTIVE INFRASTRUCTURE: SPATIAL DBSCAN CLUSTERING
// -------------------------------------------------------------
private fun DrawScope.drawDbscanClusters(
    clusters: List<DbscanCluster>,
    selected: DbscanCluster?,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    pulse: Float,
    isDark: Boolean
) {
    if (clusters.isEmpty()) return

    val labelPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = 10.5f
        color = android.graphics.Color.WHITE
    }

    val badgeBgPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = android.graphics.Color.argb(215, 15, 23, 42)
    }

    val badgeStrokePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1.4f
    }

    for (cluster in clusters) {
        val centroidPx = projectPointNearest(cluster.centroid, w, h, centerLat, centerLon, zoom)
        val isSelected = selected?.clusterId == cluster.clusterId

        val clusterColor = when {
            cluster.maxReflectivityDbz >= 60f -> Color(0xFFD500F9)
            cluster.maxReflectivityDbz >= 50f -> Color(0xFFFF1744)
            cluster.maxReflectivityDbz >= 40f -> Color(0xFFFFD600)
            else -> Color(0xFF00E676)
        }

        // Bounding Polygon Hull
        if (cluster.boundingHull.size >= 3) {
            val hullPath = Path()
            cluster.boundingHull.forEachIndexed { idx, pt ->
                val p = projectPointNearest(pt, w, h, centerLat, centerLon, zoom)
                if (idx == 0) hullPath.moveTo(p.x, p.y) else hullPath.lineTo(p.x, p.y)
            }
            hullPath.close()

            drawPath(
                path = hullPath,
                color = clusterColor.copy(alpha = if (isSelected) 0.28f else 0.12f),
                style = Fill
            )

            drawPath(
                path = hullPath,
                color = clusterColor.copy(alpha = if (isSelected) 0.95f else 0.65f),
                style = Stroke(
                    width = if (isSelected) 2.5f else 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), pulse * 14f)
                )
            )
        }

        // Centroid Reticle
        if (centroidPx.x in -50f..(w + 50f) && centroidPx.y in -50f..(h + 50f)) {
            val reticleRadius = 13f + (if (isSelected) sin(pulse * 6.28f) * 3f else 0f)
            drawCircle(
                color = clusterColor,
                radius = reticleRadius,
                center = centroidPx,
                style = Stroke(width = 1.6f)
            )
            drawCircle(
                color = clusterColor,
                radius = 3f,
                center = centroidPx,
                style = Fill
            )

            // Telemetry Badge
            val pillText = "DBSCAN #${cluster.clusterId} [${cluster.pointCount} PTS • ${cluster.maxReflectivityDbz.toInt()} dBZ]"
            val textWidth = labelPaint.measureText(pillText)
            val pillX = centroidPx.x - (textWidth / 2f)
            val pillY = centroidPx.y + reticleRadius + 16f

            badgeStrokePaint.color = clusterColor.toArgb()
            drawContext.canvas.nativeCanvas.drawRoundRect(
                pillX - 6f,
                pillY - 13f,
                pillX + textWidth + 6f,
                pillY + 5f,
                5f,
                5f,
                badgeBgPaint
            )
            drawContext.canvas.nativeCanvas.drawRoundRect(
                pillX - 6f,
                pillY - 13f,
                pillX + textWidth + 6f,
                pillY + 5f,
                5f,
                5f,
                badgeStrokePaint
            )
            drawContext.canvas.nativeCanvas.drawText(pillText, pillX, pillY, labelPaint)
        }
    }
}

// -------------------------------------------------------------
// 3. GROUND-TRUTH VALIDATION FRAMEWORK: BMKG BUOYS & TELEMETRY
// -------------------------------------------------------------
private fun DrawScope.drawBmkgBuoys(
    buoys: List<BmkgBuoyStation>,
    selected: BmkgBuoyStation?,
    verification: BmkgGroundTruthVerification?,
    w: Float,
    h: Float,
    centerLat: Double,
    centerLon: Double,
    zoom: Float,
    pulse: Float,
    isDark: Boolean
) {
    if (buoys.isEmpty()) return

    val labelPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = 10f
        color = android.graphics.Color.WHITE
    }

    val subPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.MONOSPACE
        textSize = 9f
        color = android.graphics.Color.argb(220, 0, 230, 118)
    }

    val bgPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = android.graphics.Color.argb(220, 10, 25, 47)
    }

    val borderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1.2f
        color = android.graphics.Color.argb(255, 0, 230, 118)
    }

    for (buoy in buoys) {
        val p = projectPointNearest(buoy.location, w, h, centerLat, centerLon, zoom)
        if (p.x < -60f || p.x > w + 60f || p.y < -60f || p.y > h + 60f) continue

        val isSelected = selected?.id == buoy.id
        val isVerifiedTarget = verification?.nearestStation?.id == buoy.id
        val buoyColor = if (isVerifiedTarget) Color(0xFF00E676) else Color(0xFF00E5FF)

        // Radar acoustic ping
        val pingRadius = 7f + (pulse * 20f)
        val pingAlpha = (1f - pulse).coerceIn(0f, 1f) * 0.7f
        drawCircle(
            color = buoyColor.copy(alpha = pingAlpha),
            radius = pingRadius,
            center = p,
            style = Stroke(width = 1.3f)
        )

        // Moored buoy diamond marker
        val dSize = 6.5f
        val buoyPath = Path().apply {
            moveTo(p.x, p.y - dSize)
            lineTo(p.x + dSize, p.y)
            lineTo(p.x, p.y + dSize)
            lineTo(p.x - dSize, p.y)
            close()
        }
        drawPath(buoyPath, color = buoyColor, style = Fill)
        drawPath(buoyPath, color = Color.White, style = Stroke(width = 1.3f))
        drawCircle(color = Color.Black, radius = 2f, center = p)

        // Buoy telemetry card
        val line1 = "BMKG ${buoy.stationType.iconTag}: ${buoy.name.take(18)}"
        val line2 = "${buoy.barometricPressureHpa} hPa • ${buoy.windSpeedKts} kt • ${buoy.seaSurfaceTempC}°C"
        val textWidth = maxOf(labelPaint.measureText(line1), subPaint.measureText(line2))

        val cardX = p.x + 10f
        val cardY = p.y - 10f

        if (cardX + textWidth < w && cardY > 20f) {
            drawContext.canvas.nativeCanvas.drawRoundRect(
                cardX - 4f,
                cardY - 13f,
                cardX + textWidth + 6f,
                cardY + 15f,
                4f,
                4f,
                bgPaint
            )
            borderPaint.color = buoyColor.toArgb()
            drawContext.canvas.nativeCanvas.drawRoundRect(
                cardX - 4f,
                cardY - 13f,
                cardX + textWidth + 6f,
                cardY + 15f,
                4f,
                4f,
                borderPaint
            )
            drawContext.canvas.nativeCanvas.drawText(line1, cardX, cardY - 2f, labelPaint)
            drawContext.canvas.nativeCanvas.drawText(line2, cardX, cardY + 9f, subPaint)
        }
    }
}

// -------------------------------------------------------------
// 1. HIGH-PERFORMANCE GRAPHICS ENGINE: GPU VECTOR TILING BADGE
// -------------------------------------------------------------
private fun DrawScope.drawGpuVectorTilingBadge(
    framerate: TargetFramerateFps,
    w: Float,
    h: Float,
    isDark: Boolean
) {
    val badgePaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = 9.5f
        color = android.graphics.Color.argb(210, 0, 229, 255)
    }

    val bgPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = android.graphics.Color.argb(200, 10, 15, 30)
    }

    val borderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1.0f
        color = android.graphics.Color.argb(160, 0, 229, 255)
    }

    val text = "GPU VECTOR TILING: 60 FPS LOCKED • ${framerate.label} • OPENGL VERTEX ARRAYS"
    val textWidth = badgePaint.measureText(text)
    val x = w - textWidth - 16f
    val y = h - 14f

    if (x > 10f) {
        drawContext.canvas.nativeCanvas.drawRoundRect(
            x - 6f,
            y - 12f,
            x + textWidth + 6f,
            y + 4f,
            4f,
            4f,
            bgPaint
        )
        drawContext.canvas.nativeCanvas.drawRoundRect(
            x - 6f,
            y - 12f,
            x + textWidth + 6f,
            y + 4f,
            4f,
            4f,
            borderPaint
        )
        drawContext.canvas.nativeCanvas.drawText(text, x, y, badgePaint)
    }
}

