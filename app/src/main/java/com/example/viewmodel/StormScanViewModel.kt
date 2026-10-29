package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.WeatherRepository
import com.example.model.AppScanMode
import com.example.model.AtmosphericProbe
import com.example.model.Cyclone
import com.example.model.DataFetchOrigin
import com.example.model.ForecastModel
import com.example.model.GeoPoint
import com.example.model.LightningFlash
import com.example.model.MapLayerType
import com.example.model.MapScanType
import com.example.model.ModelWeatherReport
import com.example.model.ModelWeightPreset
import com.example.model.Satellite
import com.example.model.ScannedSpot
import com.example.model.ScannedSpotType
import com.example.model.ScannerBeamStyle
import com.example.model.ServerNode
import com.example.model.StabilizationDisplayMode
import com.example.model.StrongWindArea
import com.example.model.SurgeRiskZone
import com.example.model.MapProjectionType
import com.example.model.TerrainMapStyle
import com.example.model.WindSpeedUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.example.model.getCycloneForecastLocation
import com.example.model.getCycloneForecastIntensity
import kotlin.random.Random

data class MapViewportState(
    val centerLat: Double = 22.0,
    val centerLon: Double = -50.0,
    val zoom: Float = 1.0f
)

data class StormScanUiState(
    val appScanMode: AppScanMode = AppScanMode.CYCLONES,
    val cyclones: List<Cyclone> = emptyList(),
    val strongWindAreas: List<StrongWindArea> = emptyList(),
    val servers: List<ServerNode> = emptyList(),
    val satellites: List<Satellite> = emptyList(),
    val selectedCyclone: Cyclone? = null,
    val selectedWindArea: StrongWindArea? = null,
    val selectedServer: ServerNode? = null,
    val selectedSatellite: Satellite? = null,
    val activeLayers: Set<MapLayerType> = setOf(
        MapLayerType.TERRAIN,
        MapLayerType.RADAR_SCANNER,
        MapLayerType.CYCLONES,
        MapLayerType.WIND_STREAMS,
        MapLayerType.ISOBARS,
        MapLayerType.HUMIDITY,
        MapLayerType.AIR_QUALITY,
        MapLayerType.JET_STREAMS,
        MapLayerType.FRONTAL_SYSTEMS,
        MapLayerType.BATHYMETRY,
        MapLayerType.DOPPLER_STATIONS,
        MapLayerType.NAV_CORRIDORS,
        MapLayerType.DATA_LINKS,
        MapLayerType.SERVERS,
        MapLayerType.SATELLITES,
        MapLayerType.LIGHTNING
    ),
    val terrainStyle: TerrainMapStyle = TerrainMapStyle.SATELLITE_TOPO,
    val mapScanType: MapScanType = MapScanType.COMBINED_SATELLITE_AI,
    val scannedSpots: List<ScannedSpot> = emptyList(),
    val selectedScannedSpot: ScannedSpot? = null,
    val isEasterEggUnlocked: Boolean = false,
    val maxScanningSpeedSpotsPerSec: Float = 10.0f,
    val easterEggBannerText: String? = null,
    val stabilizationMode: StabilizationDisplayMode = StabilizationDisplayMode.STABILIZED_ONLY,
    val activeModel: ForecastModel = ForecastModel.STABILIZED_ENSEMBLE,
    val modelWeightPreset: ModelWeightPreset = ModelWeightPreset.BALANCED_CONSENSUS,
    val projectionType: MapProjectionType = MapProjectionType.GLOBE_3D,
    val scannerBeamStyle: ScannerBeamStyle = ScannerBeamStyle.SCANNING_BOX,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KNOTS,
    val accuracyPct: Int = 95,
    val scanningSpeedSpotsPerSec: Float = 7.5f,
    val isDarkMode: Boolean = true,
    val showServersOnMap: Boolean = true,
    val showSatelliteTelemetry: Boolean = true,
    val showDataLinks: Boolean = true,
    val showIsobars: Boolean = true,
    val windThresholdKts: Int = 34,
    val forecastHourOffset: Int = 0, // 0, 12, 24, 36, 48, 72
    val isPlayingTimeline: Boolean = false,
    val autoTargetCycling: Boolean = false,
    val showDayNightTerminator: Boolean = true,
    val showMiniMap: Boolean = true,
    val animateCoriolisVortex: Boolean = true,
    val enableSonarPingAudio: Boolean = true,
    val highDensityScan: Boolean = true,
    val detectionSensitivityKts: Int = 30,
    val ecmwfCustomWeight: Float = 0.40f,
    val wn3CustomWeight: Float = 0.35f,
    val wn2CustomWeight: Float = 0.15f,
    val gfsCustomWeight: Float = 0.10f,
    val radarScanAngleDeg: Float = 0f,
    val pulsePhase: Float = 0f,
    val windParticlePhase: Float = 0f,
    val dataPacketPhase: Float = 0f,
    val lightningFlashes: List<LightningFlash> = emptyList(),
    val viewport: MapViewportState = MapViewportState(),
    val atmosphericProbe: AtmosphericProbe? = null,
    val isProbeLoading: Boolean = false,
    val isScanningPaused: Boolean = false,
    val mapPanSensitivityPct: Int = 60,
    val showScanningProcessing: Boolean = false,
    val localCacheCount: Int = 0,
    val dataFetchOrigin: DataFetchOrigin = DataFetchOrigin.NETWORK_LIVE,
    val lastNetworkLatencyMs: Long = 24L,
    val forceRoomCacheOnly: Boolean = false,
    val showSurgeVectors: Boolean = true,
    val surgeRiskZones: List<SurgeRiskZone> = emptyList(),
    val selectedSurgeZone: SurgeRiskZone? = null,
    val dspProcessingStage: String = "PULSE COMPRESSION • DOPPLER FFT",
    val fftSpectralPeaks: List<Float> = listOf(0.42f, 0.78f, 0.95f, 0.62f, 0.88f, 0.51f, 0.74f, 0.91f, 0.38f, 0.66f, 0.82f, 0.49f),
    val rfPrfHz: Int = 1200,
    val aiInferenceLatencyMs: Float = 14.2f,
    val framesProcessedCount: Long = 0L,
    val activeDetectionAlert: String = "10cm S-BAND RADAR SCANNER ACTIVE: WSR-88D & BMKG Doppler Network (2.7–3.0 GHz) • 5 Cyclones Tracked"
)

class StormScanViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        StormScanUiState(
            cyclones = repository.cyclones,
            strongWindAreas = repository.strongWindAreas,
            surgeRiskZones = repository.surgeRiskZones,
            servers = repository.servers,
            satellites = repository.satellites,
            selectedCyclone = null
        )
    )
    val uiState: StateFlow<StormScanUiState> = _uiState.asStateFlow()

    init {
        // Automatically prime local Room database cache
        viewModelScope.launch {
            repository.primeCache()
            val count = repository.getLocalCacheCount()
            _uiState.update { it.copy(localCacheCount = count) }
        }

        // Real-time animation, radar sweep, particle streams, and lightning ticker (60fps)
        viewModelScope.launch {
            var angle = 0f
            var pulse = 0f
            var particle = 0f
            var packet = 0f
            var tick = 0L

            while (isActive) {
                if (!_uiState.value.isScanningPaused) {
                    val speedFactor = (_uiState.value.scanningSpeedSpotsPerSec / 7.5f).coerceIn(0.5f, 2.0f)
                    angle = (angle + 1.8f * speedFactor) % 360f
                    pulse = (pulse + 0.035f * speedFactor) % 1f
                    particle = (particle + 0.02f * speedFactor) % 1f
                    packet = (packet + 0.015f * speedFactor) % 1f
                    tick++

                    // Dynamic Doppler FFT spectral peaks fluctuation
                    val basePeaks = listOf(0.42f, 0.78f, 0.95f, 0.62f, 0.88f, 0.51f, 0.74f, 0.91f, 0.38f, 0.66f, 0.82f, 0.49f)
                    val dynamicPeaks = basePeaks.mapIndexed { idx, v ->
                        val phaseOffset = (tick * 0.12f + idx * 0.5f)
                        (v + kotlin.math.sin(phaseOffset) * 0.15f).coerceIn(0.15f, 1.0f)
                    }

                    // Occasional convective lightning flash in active storm eyewalls
                    val currentFlashes = _uiState.value.lightningFlashes.filter { (tick - it.createdTick) < 15 }
                    val newFlashes = currentFlashes.toMutableList()

                    if (tick % 45 == 0L && _uiState.value.cyclones.isNotEmpty()) {
                        val randomCyclone = _uiState.value.cyclones.random()
                        val offsetLat = (Random.nextFloat() - 0.5f) * 1.5
                        val offsetLon = (Random.nextFloat() - 0.5f) * 1.5
                        newFlashes.add(
                            LightningFlash(
                                location = GeoPoint(randomCyclone.currentLocation.lat + offsetLat, randomCyclone.currentLocation.lon + offsetLon),
                                intensity = Random.nextFloat().coerceIn(0.6f, 1.0f),
                                createdTick = tick
                            )
                        )
                    }

                    _uiState.update { current ->
                        current.copy(
                            radarScanAngleDeg = angle,
                            pulsePhase = pulse,
                            windParticlePhase = particle,
                            dataPacketPhase = packet,
                            lightningFlashes = newFlashes,
                            framesProcessedCount = current.framesProcessedCount + 1,
                            fftSpectralPeaks = dynamicPeaks,
                            aiInferenceLatencyMs = (13.5f + (tick % 10) * 0.18f)
                        )
                    }

                    // Periodically refresh cache count in UI state
                    if (tick % 120 == 0L) {
                        val count = repository.getLocalCacheCount()
                        _uiState.update { it.copy(localCacheCount = count) }
                    }
                }
                delay(20)
            }
        }

        // Timeline playback ticker for forecasting animation (T+0h to T+72h)
        viewModelScope.launch {
            val hours = listOf(0, 12, 24, 36, 48, 72)
            while (isActive) {
                delay(1800)
                if (_uiState.value.isPlayingTimeline) {
                    _uiState.update { current ->
                        val currentIndex = hours.indexOf(current.forecastHourOffset)
                        val nextIndex = (currentIndex + 1) % hours.size
                        current.copy(forecastHourOffset = hours[nextIndex])
                    }
                }
            }
        }

        // Auto target cycling when enabled
        viewModelScope.launch {
            while (isActive) {
                delay(6000)
                if (_uiState.value.autoTargetCycling) {
                    when (_uiState.value.appScanMode) {
                        AppScanMode.CYCLONES -> cycleNextCyclone()
                        AppScanMode.STRONG_WINDS -> cycleNextWindArea()
                        AppScanMode.SURGE_RISK -> cycleNextSurgeZone()
                        AppScanMode.SATELLITES -> cycleNextSatellite()
                        AppScanMode.ALL_MODELS -> cycleNextCyclone()
                        AppScanMode.SETTINGS -> {}
                    }
                }
            }
        }

        // Real-time spot detection engine running continuously at scanningSpeedSpotsPerSec (5.0 to 15.0 spots/sec)
        viewModelScope.launch {
            var spotCounter = 100
            while (isActive) {
                val speed = _uiState.value.scanningSpeedSpotsPerSec.coerceIn(1.0f, 20.0f)
                val delayMs = (1000f / speed).toLong().coerceIn(35L, 250L)
                delay(delayMs)

                if (!_uiState.value.isScanningPaused) {
                    val currState = _uiState.value
                    val spot = generateTacticalSpot(spotCounter++, currState)

                    _uiState.update { current ->
                        val updated = (current.scannedSpots + spot).takeLast(36)
                        current.copy(
                            scannedSpots = updated
                        )
                    }

                    // Asynchronously persist to local Room cache without blocking spot generation loop
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            repository.cacheScannedSpots(listOf(spot))
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }

    private val settingsTapTimestamps = mutableListOf<Long>()

    /**
     * Easter Egg: Spam-tapping Settings mode secretly boosts spots per sec limit to 15.0 spots/sec!
     */
    fun onSettingsModeTapped() {
        val now = System.currentTimeMillis()
        settingsTapTimestamps.add(now)
        settingsTapTimestamps.removeAll { now - it > 2500 }

        if (settingsTapTimestamps.size >= 5) {
            settingsTapTimestamps.clear()
            triggerEasterEggOverclock()
        }
    }

    fun triggerEasterEggOverclock() {
        _uiState.update { current ->
            current.copy(
                isEasterEggUnlocked = true,
                maxScanningSpeedSpotsPerSec = 15.0f,
                scanningSpeedSpotsPerSec = 15.0f,
                easterEggBannerText = "⚡ 10cm S-BAND RADAR OVERCLOCK: 15.0 SWEEPS/SEC PULSE LIMIT ENGAGED!",
                activeDetectionAlert = "⚡ 10cm S-BAND PULSE OVERCLOCK: 15.0 SWEEPS/SEC • DUAL-POL LEVEL II DOPPLER LOCKED"
            )
        }
    }

    fun dismissEasterEggBanner() {
        _uiState.update { it.copy(easterEggBannerText = null) }
    }

    fun setMapScanType(type: MapScanType) {
        _uiState.update { it.copy(mapScanType = type) }
    }

    fun selectScannedSpot(spot: ScannedSpot?) {
        _uiState.update { it.copy(selectedScannedSpot = spot) }
        spot?.let {
            probeLocation(it.location)
        }
    }

    fun cycleNextSatellite() {
        val sats = _uiState.value.satellites
        if (sats.isEmpty()) return
        val currentIdx = sats.indexOfFirst { it.id == _uiState.value.selectedSatellite?.id }
        val nextIdx = (currentIdx + 1) % sats.size
        selectSatellite(sats[nextIdx])
    }

    fun setAppScanMode(mode: AppScanMode) {
        if (mode == AppScanMode.SETTINGS) {
            onSettingsModeTapped()
        }
        _uiState.update { current ->
            current.copy(
                appScanMode = mode,
                selectedCyclone = null,
                selectedWindArea = null,
                selectedSurgeZone = null,
                selectedSatellite = null,
                selectedServer = null,
                selectedScannedSpot = null
            )
        }
        when (mode) {
            AppScanMode.CYCLONES, AppScanMode.ALL_MODELS -> {
                _uiState.value.cyclones.firstOrNull()?.let {
                    centerOnCoordinate(it.currentLocation.lat, it.currentLocation.lon, 1.4f)
                }
            }
            AppScanMode.STRONG_WINDS -> {
                _uiState.value.strongWindAreas.firstOrNull()?.let {
                    centerOnCoordinate(it.centerLocation.lat, it.centerLocation.lon, 1.4f)
                }
            }
            AppScanMode.SURGE_RISK -> {
                _uiState.value.surgeRiskZones.firstOrNull()?.let {
                    centerOnCoordinate(it.location.lat, it.location.lon, 1.6f)
                }
            }
            AppScanMode.SATELLITES -> {
                _uiState.value.satellites.firstOrNull()?.let {
                    centerOnCoordinate(it.location.lat, it.location.lon, 1.2f)
                }
            }
            AppScanMode.SETTINGS -> {}
        }
    }

    fun cycleNextSurgeZone() {
        val zones = _uiState.value.surgeRiskZones
        if (zones.isEmpty()) return
        val current = _uiState.value.selectedSurgeZone
        val nextIndex = if (current == null) 0 else (zones.indexOfFirst { it.id == current.id } + 1) % zones.size
        selectSurgeZone(zones[nextIndex])
    }

    fun setAccuracyPct(accuracy: Int) {
        val clamped = accuracy.coerceIn(20, 100)
        _uiState.update { current ->
            val updatedCyclones = current.cyclones.map { c ->
                val newConsensus = repository.computeCustomConsensusTrack(
                    c, current.ecmwfCustomWeight, current.wn3CustomWeight,
                    current.wn2CustomWeight, current.gfsCustomWeight, clamped
                )
                c.copy(stabilizedTrack = newConsensus)
            }
            current.copy(
                accuracyPct = clamped,
                cyclones = updatedCyclones,
                selectedCyclone = updatedCyclones.find { it.id == current.selectedCyclone?.id } ?: current.selectedCyclone
            )
        }
    }

    fun setScanningSpeed(speed: Float) {
        _uiState.update { it.copy(scanningSpeedSpotsPerSec = speed.coerceIn(5.0f, 15.0f)) }
    }

    fun setDarkMode(darkMode: Boolean) {
        _uiState.update { it.copy(isDarkMode = darkMode) }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun setActiveModel(model: ForecastModel) {
        _uiState.update {
            it.copy(
                activeModel = model,
                stabilizationMode = if (model == ForecastModel.STABILIZED_ENSEMBLE)
                    StabilizationDisplayMode.STABILIZED_ONLY else StabilizationDisplayMode.MODEL_COMPARISON
            )
        }
    }

    fun setModelWeightPreset(preset: ModelWeightPreset) {
        _uiState.update { current ->
            val updatedCyclones = current.cyclones.map { c ->
                val newConsensus = repository.computeCustomConsensusTrack(
                    c, preset.ecmwfWeight, preset.weatherNext3Weight,
                    preset.weatherNext2Weight, preset.gfsWeight, current.accuracyPct
                )
                c.copy(stabilizedTrack = newConsensus)
            }
            current.copy(
                modelWeightPreset = preset,
                ecmwfCustomWeight = preset.ecmwfWeight,
                wn3CustomWeight = preset.weatherNext3Weight,
                wn2CustomWeight = preset.weatherNext2Weight,
                gfsCustomWeight = preset.gfsWeight,
                cyclones = updatedCyclones,
                selectedCyclone = updatedCyclones.find { it.id == current.selectedCyclone?.id } ?: current.selectedCyclone
            )
        }
    }

    fun setCustomModelWeights(ecmwf: Float, wn3: Float, wn2: Float, gfs: Float) {
        _uiState.update { current ->
            val updatedCyclones = current.cyclones.map { c ->
                val newConsensus = repository.computeCustomConsensusTrack(
                    c, ecmwf, wn3, wn2, gfs, current.accuracyPct
                )
                c.copy(stabilizedTrack = newConsensus)
            }
            current.copy(
                modelWeightPreset = ModelWeightPreset.CUSTOM,
                ecmwfCustomWeight = ecmwf,
                wn3CustomWeight = wn3,
                wn2CustomWeight = wn2,
                gfsCustomWeight = gfs,
                cyclones = updatedCyclones,
                selectedCyclone = updatedCyclones.find { it.id == current.selectedCyclone?.id } ?: current.selectedCyclone
            )
        }
    }

    fun setShowDayNightTerminator(show: Boolean) {
        _uiState.update { it.copy(showDayNightTerminator = show) }
    }

    fun setShowMiniMap(show: Boolean) {
        _uiState.update { it.copy(showMiniMap = show) }
    }

    fun setAnimateCoriolisVortex(animate: Boolean) {
        _uiState.update { it.copy(animateCoriolisVortex = animate) }
    }

    fun setEnableSonarPingAudio(enable: Boolean) {
        _uiState.update { it.copy(enableSonarPingAudio = enable) }
    }

    fun setHighDensityScan(enabled: Boolean) {
        _uiState.update { it.copy(highDensityScan = enabled) }
    }

    fun setDetectionSensitivityKts(kts: Int) {
        _uiState.update { it.copy(detectionSensitivityKts = kts) }
    }

    fun resetToDefaults() {
        _uiState.update { current ->
            current.copy(
                accuracyPct = 95,
                scanningSpeedSpotsPerSec = 7.5f,
                isDarkMode = true,
                terrainStyle = TerrainMapStyle.SATELLITE_TOPO,
                projectionType = MapProjectionType.GLOBE_3D,
                scannerBeamStyle = ScannerBeamStyle.SCANNING_BOX,
                modelWeightPreset = ModelWeightPreset.BALANCED_CONSENSUS,
                activeModel = ForecastModel.STABILIZED_ENSEMBLE,
                stabilizationMode = StabilizationDisplayMode.STABILIZED_ONLY,
                windSpeedUnit = WindSpeedUnit.KNOTS,
                showServersOnMap = true,
                showSatelliteTelemetry = true,
                showDataLinks = true,
                showIsobars = true,
                showDayNightTerminator = true,
                showMiniMap = true,
                animateCoriolisVortex = true,
                highDensityScan = true,
                detectionSensitivityKts = 30
            )
        }
    }

    fun setProjectionType(type: MapProjectionType) {
        _uiState.update { it.copy(projectionType = type) }
    }

    fun toggleProjectionType() {
        _uiState.update {
            it.copy(
                projectionType = if (it.projectionType == MapProjectionType.GLOBE_3D) {
                    MapProjectionType.TACTICAL_2D
                } else {
                    MapProjectionType.GLOBE_3D
                }
            )
        }
    }

    fun setScannerBeamStyle(style: ScannerBeamStyle) {
        _uiState.update { it.copy(scannerBeamStyle = style) }
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        _uiState.update { it.copy(windSpeedUnit = unit) }
    }

    fun setForecastHour(hour: Int) {
        _uiState.update { it.copy(forecastHourOffset = hour) }
        val currentCyclone = _uiState.value.selectedCyclone
        if (currentCyclone != null && hour > 0) {
            val predLoc = getCycloneForecastLocation(currentCyclone, hour)
            centerOnCoordinate(predLoc.lat, predLoc.lon, _uiState.value.viewport.zoom.coerceAtLeast(1.2f))
        }
    }

    fun togglePlayTimeline() {
        _uiState.update { it.copy(isPlayingTimeline = !it.isPlayingTimeline) }
    }

    fun toggleAutoTargetCycling() {
        _uiState.update { it.copy(autoTargetCycling = !it.autoTargetCycling) }
    }

    fun toggleShowServers(show: Boolean) {
        _uiState.update { it.copy(showServersOnMap = show) }
        toggleLayer(MapLayerType.SERVERS)
    }

    fun toggleShowSatellites(show: Boolean) {
        _uiState.update { it.copy(showSatelliteTelemetry = show) }
        toggleLayer(MapLayerType.SATELLITES)
    }

    fun toggleShowDataLinks(show: Boolean) {
        _uiState.update { it.copy(showDataLinks = show) }
        toggleLayer(MapLayerType.DATA_LINKS)
    }

    fun toggleShowIsobars(show: Boolean) {
        _uiState.update { it.copy(showIsobars = show) }
        toggleLayer(MapLayerType.ISOBARS)
    }

    fun setWindThreshold(kts: Int) {
        _uiState.update { it.copy(windThresholdKts = kts) }
    }

    fun toggleLayer(layer: MapLayerType) {
        _uiState.update { current ->
            val updated = current.activeLayers.toMutableSet()
            if (updated.contains(layer)) {
                updated.remove(layer)
            } else {
                updated.add(layer)
            }
            current.copy(activeLayers = updated)
        }
    }

    fun setTerrainStyle(style: TerrainMapStyle) {
        _uiState.update { it.copy(terrainStyle = style) }
    }

    fun setStabilizationMode(mode: StabilizationDisplayMode) {
        _uiState.update { it.copy(stabilizationMode = mode) }
    }

    fun selectCyclone(cyclone: Cyclone?) {
        _uiState.update {
            it.copy(
                selectedCyclone = cyclone,
                selectedWindArea = null,
                selectedServer = null,
                selectedSatellite = null
            )
        }
        if (cyclone != null) {
            centerOnCoordinate(cyclone.currentLocation.lat, cyclone.currentLocation.lon, 1.8f)
            probeLocation(cyclone.currentLocation)
        }
    }

    fun selectWindArea(area: StrongWindArea?) {
        _uiState.update {
            it.copy(
                selectedWindArea = area,
                selectedCyclone = null,
                selectedServer = null,
                selectedSatellite = null
            )
        }
        if (area != null) {
            centerOnCoordinate(area.centerLocation.lat, area.centerLocation.lon, 1.6f)
            probeLocation(area.centerLocation)
        }
    }

    fun cycleNextCyclone() {
        val list = _uiState.value.cyclones
        if (list.isEmpty()) return
        val currentIndex = list.indexOfFirst { it.id == _uiState.value.selectedCyclone?.id }
        val nextIndex = (currentIndex + 1) % list.size
        selectCyclone(list[nextIndex])
    }

    fun cycleNextWindArea() {
        val list = _uiState.value.strongWindAreas
        if (list.isEmpty()) return
        val currentIndex = list.indexOfFirst { it.id == _uiState.value.selectedWindArea?.id }
        val nextIndex = (currentIndex + 1) % list.size
        selectWindArea(list[nextIndex])
    }

    fun selectServer(server: ServerNode?) {
        _uiState.update {
            it.copy(
                selectedServer = server,
                selectedCyclone = null,
                selectedWindArea = null,
                selectedSatellite = null
            )
        }
        if (server != null) {
            centerOnCoordinate(server.location.lat, server.location.lon, 2.2f)
            probeLocation(server.location)
        }
    }

    fun selectSatellite(satellite: Satellite?) {
        _uiState.update {
            it.copy(
                selectedSatellite = satellite,
                selectedCyclone = null,
                selectedWindArea = null,
                selectedServer = null
            )
        }
        if (satellite != null) {
            centerOnCoordinate(satellite.location.lat, satellite.location.lon, 1.5f)
            probeLocation(satellite.location)
        }
    }

    fun selectSurgeZone(zone: SurgeRiskZone?) {
        _uiState.update {
            it.copy(
                selectedSurgeZone = zone,
                selectedCyclone = null,
                selectedWindArea = null,
                selectedServer = null,
                selectedSatellite = null,
                selectedScannedSpot = null
            )
        }
        if (zone != null) {
            centerOnCoordinate(zone.location.lat, zone.location.lon, 2.2f)
            probeLocation(zone.location)
        }
    }

    fun clearSelections() {
        _uiState.update {
            it.copy(
                selectedCyclone = null,
                selectedWindArea = null,
                selectedServer = null,
                selectedSatellite = null,
                selectedSurgeZone = null,
                selectedScannedSpot = null,
                atmosphericProbe = null
            )
        }
    }

    fun dismissProbe() {
        _uiState.update { it.copy(atmosphericProbe = null) }
    }

    fun setForceRoomCacheOnly(force: Boolean) {
        _uiState.update { it.copy(forceRoomCacheOnly = force) }
        val targetPoint = _uiState.value.selectedCyclone?.currentLocation
            ?: _uiState.value.selectedSurgeZone?.location
            ?: _uiState.value.selectedWindArea?.centerLocation
            ?: GeoPoint(_uiState.value.viewport.centerLat, _uiState.value.viewport.centerLon)
        probeLocation(targetPoint)
    }

    fun setShowSurgeVectors(show: Boolean) {
        _uiState.update { it.copy(showSurgeVectors = show) }
    }

    fun probeLocation(point: GeoPoint) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProbeLoading = true) }
            val result = repository.getAtmosphericProbeWithOrigin(point, _uiState.value.forceRoomCacheOnly)
            _uiState.update {
                it.copy(
                    atmosphericProbe = result.probe,
                    dataFetchOrigin = result.origin,
                    lastNetworkLatencyMs = result.latencyMs,
                    localCacheCount = result.cachedRecordCount,
                    isProbeLoading = false
                )
            }
        }
    }

    fun toggleScanPause() {
        _uiState.update { it.copy(isScanningPaused = !it.isScanningPaused) }
    }

    fun setMapPanSensitivityPct(sensitivity: Int) {
        _uiState.update { it.copy(mapPanSensitivityPct = sensitivity.coerceIn(20, 100)) }
    }

    fun panMap(deltaLat: Double, deltaLon: Double) {
        _uiState.update { current ->
            val newLat = (current.viewport.centerLat + deltaLat).coerceIn(-75.0, 75.0)
            var newLon = current.viewport.centerLon + deltaLon
            while (newLon > 180.0) newLon -= 360.0
            while (newLon < -180.0) newLon += 360.0
            current.copy(
                viewport = current.viewport.copy(centerLat = newLat, centerLon = newLon)
            )
        }
    }

    fun zoomMap(zoomDelta: Float) {
        _uiState.update { current ->
            val newZoom = (current.viewport.zoom * zoomDelta).coerceIn(0.6f, 16.0f)
            current.copy(viewport = current.viewport.copy(zoom = newZoom))
        }
    }

    fun setShowScanningProcessing(show: Boolean) {
        _uiState.update { it.copy(showScanningProcessing = show) }
    }

    fun toggleScanningProcessing() {
        _uiState.update { it.copy(showScanningProcessing = !it.showScanningProcessing) }
    }

    fun zoomIn() = zoomMap(1.3f)
    fun zoomOut() = zoomMap(0.77f)

    fun resetView() {
        _uiState.update {
            it.copy(viewport = MapViewportState(centerLat = 20.0, centerLon = -40.0, zoom = 1.0f))
        }
    }

    fun centerOnCoordinate(lat: Double, lon: Double, zoom: Float = 1.8f) {
        _uiState.update {
            it.copy(
                viewport = MapViewportState(centerLat = lat, centerLon = lon, zoom = zoom)
            )
        }
    }

    private fun dbzToPrecipMmH(dbz: Float): Float {
        if (dbz < 20f) return 0f
        val z = Math.pow(10.0, (dbz / 10.0).toDouble())
        // National Weather Service Marshall-Palmer equation: Z = 300 * R^1.4 => R = (Z / 300)^(1 / 1.4)
        val r = Math.pow(z / 300.0, 1.0 / 1.4).toFloat()
        return kotlin.math.round(r * 10f) / 10f
    }

    private fun generateTacticalSpot(counter: Int, state: StormScanUiState): ScannedSpot {
        val accuracyBase = state.accuracyPct.toFloat()
        val jitter = (Random.nextFloat() * 2.0f - 1.0f)
        val calculatedAccuracy = (accuracyBase + jitter).coerceIn(85.0f, 99.8f)

        // Alternate: 50% around the active storm system, 50% from active 10cm S-Band ground Doppler stations or viewport
        val useStationOrViewport = (counter % 2 == 0)

        if (useStationOrViewport) {
            // Find nearest or designated real ground Doppler radar station
            val station = com.example.data.GeoData.dopplerStations[counter % com.example.data.GeoData.dopplerStations.size]
            val angleRad = Math.toRadians((state.radarScanAngleDeg + (counter * 37) % 360).toDouble())
            val maxRangeDeg = 3.5 // ~380km radar range
            val dist = Random.nextFloat() * maxRangeDeg
            val spotLat = (station.location.lat + dist * kotlin.math.sin(angleRad)).coerceIn(-80.0, 80.0)
            val spotLon = (station.location.lon + dist * kotlin.math.cos(angleRad))

            val stationCells = listOf(
                Pair(ScannedSpotType.HEAVY_RAIN, "PRECIP CELL"),
                Pair(ScannedSpotType.HEAVY_RAIN, "MONSOON SQUALL"),
                Pair(ScannedSpotType.CONVECTIVE_CELL, "CONVECTIVE ECHO"),
                Pair(ScannedSpotType.STRONG_WIND_CELL, "GALE SHEAR CELL"),
                Pair(ScannedSpotType.SATELLITE_FOOTPRINT, "ORBITAL SOUNDING"),
                Pair(ScannedSpotType.AI_NEURAL_ANOMALY, "CYCLOGENESIS ANOMALY")
            )
            val chosen = stationCells[counter % stationCells.size]
            val dbz = when (chosen.first) {
                ScannedSpotType.HEAVY_RAIN -> Random.nextFloat() * 12f + 48f // 48 - 60 dBZ
                ScannedSpotType.CONVECTIVE_CELL -> Random.nextFloat() * 12f + 36f // 36 - 48 dBZ
                ScannedSpotType.STRONG_WIND_CELL -> Random.nextFloat() * 10f + 42f // 42 - 52 dBZ
                ScannedSpotType.SATELLITE_FOOTPRINT -> Random.nextFloat() * 8f + 30f // 30 - 38 dBZ
                ScannedSpotType.AI_NEURAL_ANOMALY -> Random.nextFloat() * 10f + 35f // 35 - 45 dBZ
                ScannedSpotType.CYCLONE_CORE -> Random.nextFloat() * 8f + 60f
            }
            val rainMmH = dbzToPrecipMmH(dbz)
            val echoTop = Random.nextInt(32, 52)
            val windSpeed = when (chosen.first) {
                ScannedSpotType.STRONG_WIND_CELL -> Random.nextInt(50, 85)
                ScannedSpotType.HEAVY_RAIN -> Random.nextInt(35, 65)
                else -> Random.nextInt(25, 55)
            }

            return ScannedSpot(
                id = "spot_rad_${counter}_${System.currentTimeMillis()}",
                location = GeoPoint(spotLat, spotLon),
                name = "${station.callsign} ${chosen.second}",
                type = chosen.first,
                accuracyPct = calculatedAccuracy,
                windKts = windSpeed,
                humidityPct = if (chosen.first == ScannedSpotType.HEAVY_RAIN) Random.nextInt(90, 100) else Random.nextInt(70, 95),
                aqi = Random.nextInt(12, 30),
                detectedTick = System.currentTimeMillis(),
                precipitationRateMmH = rainMmH,
                reflectivityDbz = dbz,
                echoTopKft = echoTop,
                radarStationCallsign = station.callsign
            )
        }

        return when (state.appScanMode) {
            AppScanMode.CYCLONES, AppScanMode.ALL_MODELS -> {
                val storm = state.selectedCyclone ?: state.cyclones.randomOrNull()
                if (storm != null) {
                    val activeLoc = getCycloneForecastLocation(storm, state.forecastHourOffset)
                    val (activeWind, _) = getCycloneForecastIntensity(storm, state.forecastHourOffset)
                    val angleRad = Math.toRadians((Random.nextFloat() * 360.0))
                    val distDeg = Random.nextFloat() * 1.8f
                    val spotLat = activeLoc.lat + (distDeg * kotlin.math.sin(angleRad))
                    val spotLon = activeLoc.lon + (distDeg * kotlin.math.cos(angleRad))
                    val isEyewall = distDeg < 0.45f
                    val isHeavyRain = distDeg in 0.45f..1.2f

                    val dbz = if (isEyewall) {
                        Random.nextFloat() * 8f + 60f // 60 - 68 dBZ (Extreme eyewall)
                    } else if (isHeavyRain) {
                        Random.nextFloat() * 10f + 48f // 48 - 58 dBZ (Torrential rain)
                    } else {
                        Random.nextFloat() * 10f + 35f // 35 - 45 dBZ (Outer convective band)
                    }
                    val rainMmH = dbzToPrecipMmH(dbz)
                    val echoTop = if (isEyewall) Random.nextInt(48, 58) else Random.nextInt(36, 48)

                    ScannedSpot(
                        id = "spot_${counter}_${System.currentTimeMillis()}",
                        location = GeoPoint(spotLat, spotLon),
                        name = if (isEyewall) "${storm.name} EYEWALL" else if (isHeavyRain) "${storm.name} FEEDER RAIN" else "${storm.name} RAINBAND",
                        type = if (isEyewall) ScannedSpotType.CYCLONE_CORE else if (isHeavyRain) ScannedSpotType.HEAVY_RAIN else ScannedSpotType.CONVECTIVE_CELL,
                        accuracyPct = calculatedAccuracy,
                        windKts = if (isEyewall) activeWind else (activeWind * 0.68f).toInt(),
                        humidityPct = (storm.relativeHumidityPct + Random.nextInt(-2, 3)).coerceIn(85, 100),
                        aqi = Random.nextInt(10, 25),
                        detectedTick = System.currentTimeMillis(),
                        precipitationRateMmH = rainMmH,
                        reflectivityDbz = dbz,
                        echoTopKft = echoTop,
                        radarStationCallsign = "10cm S-BAND"
                    )
                } else {
                    generateFallbackSpot(counter, calculatedAccuracy)
                }
            }
            AppScanMode.STRONG_WINDS -> {
                val wind = state.selectedWindArea ?: state.strongWindAreas.randomOrNull()
                if (wind != null) {
                    val driftFactor = (state.forecastHourOffset * 0.05).toFloat()
                    val driftRad = Math.toRadians((wind.windDirectionDeg - 180.0))
                    val baseLat = wind.centerLocation.lat + (driftFactor * kotlin.math.sin(driftRad))
                    val baseLon = wind.centerLocation.lon + (driftFactor * kotlin.math.cos(driftRad))

                    val angleRad = Math.toRadians((Random.nextFloat() * 360.0))
                    val distDeg = Random.nextFloat() * 2.5f
                    val spotLat = baseLat + (distDeg * kotlin.math.sin(angleRad))
                    val spotLon = baseLon + (distDeg * kotlin.math.cos(angleRad))
                    val dbz = Random.nextFloat() * 10f + 42f // 42 - 52 dBZ

                    ScannedSpot(
                        id = "spot_${counter}_${System.currentTimeMillis()}",
                        location = GeoPoint(spotLat, spotLon),
                        name = "${wind.name} GALE CORE",
                        type = ScannedSpotType.STRONG_WIND_CELL,
                        accuracyPct = calculatedAccuracy,
                        windKts = (wind.sustainedWindKts + Random.nextInt(-4, 5)).coerceAtLeast(35),
                        humidityPct = (wind.humidityPct + Random.nextInt(-3, 4)).coerceIn(50, 95),
                        aqi = Random.nextInt(20, 50),
                        detectedTick = System.currentTimeMillis(),
                        precipitationRateMmH = dbzToPrecipMmH(dbz),
                        reflectivityDbz = dbz,
                        echoTopKft = Random.nextInt(28, 42),
                        radarStationCallsign = "POLAR-WIND"
                    )
                } else {
                    generateFallbackSpot(counter, calculatedAccuracy)
                }
            }
            AppScanMode.SATELLITES -> {
                val sat = state.selectedSatellite ?: state.satellites.randomOrNull()
                if (sat != null) {
                    val angleRad = Math.toRadians((Random.nextFloat() * 360.0))
                    val distDeg = Random.nextFloat() * 4.0f
                    val spotLat = (sat.location.lat + (distDeg * kotlin.math.sin(angleRad))).coerceIn(-75.0, 75.0)
                    val spotLon = sat.location.lon + (distDeg * kotlin.math.cos(angleRad))
                    val dbz = Random.nextFloat() * 8f + 32f

                    ScannedSpot(
                        id = "spot_${counter}_${System.currentTimeMillis()}",
                        location = GeoPoint(spotLat, spotLon),
                        name = "${sat.name} SOUNDING",
                        type = ScannedSpotType.SATELLITE_FOOTPRINT,
                        accuracyPct = calculatedAccuracy,
                        windKts = Random.nextInt(20, 85),
                        humidityPct = Random.nextInt(65, 96),
                        aqi = Random.nextInt(15, 40),
                        detectedTick = System.currentTimeMillis(),
                        precipitationRateMmH = dbzToPrecipMmH(dbz),
                        reflectivityDbz = dbz,
                        echoTopKft = Random.nextInt(32, 48),
                        radarStationCallsign = sat.name.take(8)
                    )
                } else {
                    generateFallbackSpot(counter, calculatedAccuracy)
                }
            }
            AppScanMode.SURGE_RISK -> {
                val zone = state.selectedSurgeZone ?: state.surgeRiskZones.randomOrNull()
                if (zone != null) {
                    val angleRad = Math.toRadians((Random.nextFloat() * 360.0))
                    val distDeg = Random.nextFloat() * 1.5f
                    val spotLat = zone.location.lat + (distDeg * kotlin.math.sin(angleRad))
                    val spotLon = zone.location.lon + (distDeg * kotlin.math.cos(angleRad))
                    val dbz = Random.nextFloat() * 12f + 46f

                    ScannedSpot(
                        id = "surge_${counter}_${System.currentTimeMillis()}",
                        location = GeoPoint(spotLat, spotLon),
                        name = "${zone.coastalRegion.take(15).uppercase()} SURGE",
                        type = ScannedSpotType.CONVECTIVE_CELL,
                        accuracyPct = calculatedAccuracy,
                        windKts = (zone.peakSurgeFeet * 5.2f).toInt().coerceAtLeast(35),
                        humidityPct = 96,
                        aqi = 18,
                        detectedTick = System.currentTimeMillis(),
                        precipitationRateMmH = dbzToPrecipMmH(dbz),
                        reflectivityDbz = dbz,
                        echoTopKft = Random.nextInt(35, 48),
                        radarStationCallsign = "COAST-SURGE"
                    )
                } else {
                    generateFallbackSpot(counter, calculatedAccuracy)
                }
            }
            AppScanMode.SETTINGS -> {
                generateFallbackSpot(counter, calculatedAccuracy)
            }
        }
    }

    private fun generateFallbackSpot(counter: Int, accuracy: Float): ScannedSpot {
        val station = com.example.data.GeoData.dopplerStations[counter % com.example.data.GeoData.dopplerStations.size]
        val lat = (station.location.lat + (counter % 3) * 0.8).coerceIn(-80.0, 80.0)
        val lon = (station.location.lon + (counter % 4) * 0.8)
        val dbz = 44.5f
        return ScannedSpot(
            id = "spot_${counter}_${System.currentTimeMillis()}",
            location = GeoPoint(lat, lon),
            name = "${station.callsign} RADAR SWEEP",
            type = ScannedSpotType.HEAVY_RAIN,
            accuracyPct = accuracy,
            windKts = 42,
            humidityPct = 88,
            aqi = 22,
            detectedTick = System.currentTimeMillis(),
            precipitationRateMmH = dbzToPrecipMmH(dbz),
            reflectivityDbz = dbz,
            echoTopKft = 36,
            radarStationCallsign = station.callsign
        )
    }

    fun getModelReports(cyclone: Cyclone): List<ModelWeatherReport> {
        return repository.getAllModelReports(cyclone)
    }
}
