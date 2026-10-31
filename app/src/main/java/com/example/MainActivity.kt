package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppScanMode
import com.example.ui.hud.MapHudOverlay
import com.example.ui.map.TacticalWeatherMap
import com.example.ui.theme.AeroScanTheme
import com.example.viewmodel.StormScanViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StormScanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AeroScanTheme(darkTheme = uiState.isDarkMode) {
                // Intercept back button to dismiss open details or exit settings mode
                val hasModalOpen = uiState.selectedCyclone != null ||
                        uiState.selectedWindArea != null ||
                        uiState.selectedServer != null ||
                        uiState.selectedSatellite != null ||
                        uiState.selectedScannedSpot != null ||
                        uiState.selectedSurgeZone != null ||
                        uiState.appScanMode == AppScanMode.SETTINGS

                BackHandler(enabled = hasModalOpen) {
                    if (uiState.appScanMode == AppScanMode.SETTINGS) {
                        viewModel.setAppScanMode(AppScanMode.CYCLONES)
                    } else {
                        viewModel.clearSelections()
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Full-screen interactive tactical weather radar map
                    TacticalWeatherMap(
                        uiState = uiState,
                        onPan = { dLat, dLon -> viewModel.panMap(dLat, dLon) },
                        onZoom = { factor -> viewModel.zoomMap(factor) },
                        onTapPoint = { point -> viewModel.probeLocation(point) },
                        onSelectCyclone = { cyclone -> viewModel.selectCyclone(cyclone) },
                        onSelectWindArea = { windArea -> viewModel.selectWindArea(windArea) },
                        onSelectServer = { server -> viewModel.selectServer(server) },
                        onSelectSatellite = { sat -> viewModel.selectSatellite(sat) },
                        onSelectScannedSpot = { spot -> viewModel.selectScannedSpot(spot) },
                        onSelectSurgeZone = { zone -> viewModel.selectSurgeZone(zone) },
                        onSelectDbscanCluster = { cluster -> viewModel.selectDbscanCluster(cluster) },
                        onSelectBmkgBuoy = { buoy -> viewModel.selectBmkgBuoy(buoy) }
                    )

                    // Minimalist tactical HUD overlay with modes, settings and scanned spots
                    MapHudOverlay(
                        uiState = uiState,
                        onSetAppScanMode = { mode -> viewModel.setAppScanMode(mode) },
                        onToggleLayer = { layer -> viewModel.toggleLayer(layer) },
                        onSetTerrainStyle = { style -> viewModel.setTerrainStyle(style) },
                        onSetStabilizationMode = { mode -> viewModel.setStabilizationMode(mode) },
                        onSetAccuracyPct = { accuracy -> viewModel.setAccuracyPct(accuracy) },
                        onSetScanningSpeed = { speed -> viewModel.setScanningSpeed(speed) },
                        onSetMapPanSensitivity = { sensitivity -> viewModel.setMapPanSensitivityPct(sensitivity) },
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onSetActiveModel = { model -> viewModel.setActiveModel(model) },
                        onToggleShowServers = { show -> viewModel.toggleShowServers(show) },
                        onToggleShowSatellites = { show -> viewModel.toggleShowSatellites(show) },
                        onSelectCyclone = { cyclone -> viewModel.selectCyclone(cyclone) },
                        onSelectWindArea = { windArea -> viewModel.selectWindArea(windArea) },
                        onSelectSatellite = { sat -> viewModel.selectSatellite(sat) },
                        onSelectScannedSpot = { spot -> viewModel.selectScannedSpot(spot) },
                        onSelectSurgeZone = { zone -> viewModel.selectSurgeZone(zone) },
                        onToggleForceRoomCache = { force -> viewModel.setForceRoomCacheOnly(force) },
                        onToggleSurgeVectors = { show -> viewModel.setShowSurgeVectors(show) },
                        onSetMapScanType = { type -> viewModel.setMapScanType(type) },
                        onDismissEasterEggBanner = { viewModel.dismissEasterEggBanner() },
                        onGetModelReports = { cyclone -> viewModel.getModelReports(cyclone) },
                        onCloseDetail = { viewModel.clearSelections() },
                        onDismissProbe = { viewModel.dismissProbe() },
                        onZoomIn = { viewModel.zoomIn() },
                        onZoomOut = { viewModel.zoomOut() },
                        onResetView = { viewModel.resetView() },
                        onTogglePauseScan = { viewModel.toggleScanPause() },
                        onToggleScanningProcessing = { viewModel.toggleScanningProcessing() },
                        onSetShowScanningProcessing = { viewModel.setShowScanningProcessing(it) },
                        onSetForecastHour = { hour -> viewModel.setForecastHour(hour) },
                        onTogglePlayTimeline = { viewModel.togglePlayTimeline() },
                        onSetProjectionType = { type -> viewModel.setProjectionType(type) },
                        onToggleProjectionType = { viewModel.toggleProjectionType() },
                        onSetScannerBeamStyle = { style -> viewModel.setScannerBeamStyle(style) },
                        onSetTargetFramerate = { target -> viewModel.setTargetFramerate(target) },
                        onToggleGpuVectorTiling = { viewModel.toggleGpuVectorTiling() },
                        onToggleDbscanClustering = { viewModel.toggleDbscanClustering() },
                        onTriggerBmkgVerification = { cyclone, spot -> viewModel.triggerBmkgGroundTruthVerification(cyclone, spot) },
                        onDismissBmkgVerification = { viewModel.dismissGroundTruthVerification() },
                        onSelectDbscanCluster = { cluster -> viewModel.selectDbscanCluster(cluster) },
                        onSelectBmkgBuoy = { buoy -> viewModel.selectBmkgBuoy(buoy) }
                    )
                }
            }
        }
    }
}
