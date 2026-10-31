package com.example.ui.hud

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.ZoomIn
import com.example.model.DataFetchOrigin
import com.example.model.ForecastIntensity
import com.example.model.getCycloneForecastLocation
import com.example.model.getCycloneForecastIntensity
import com.example.model.MapScanType
import com.example.model.ModelWeatherReport
import com.example.model.ScannedSpot
import com.example.model.ScannedSpotType
import com.example.model.SurgeRiskLevel
import com.example.model.SurgeRiskZone
import com.example.model.getCycloneIntensityColor
import com.example.model.getCycloneIntensityAccentColor
import com.example.model.getCycloneIntensityCategory
import com.example.model.getCycloneTrackHistoryAndForecast
import com.example.model.CycloneTrackCheckpoint
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScanMode
import com.example.model.AtmosphericProbe
import com.example.model.Cyclone
import com.example.model.ForecastModel
import com.example.model.MapLayerType
import com.example.model.MapProjectionType
import com.example.model.ModelWeightPreset
import com.example.model.Satellite
import com.example.model.ScannerBeamStyle
import com.example.model.ServerNode
import com.example.model.StabilizationDisplayMode
import com.example.model.StrongWindArea
import com.example.model.TerrainMapStyle
import com.example.model.WindSeverity
import com.example.model.WindSpeedUnit
import com.example.model.TargetFramerateFps
import com.example.model.OpticalFlowVector
import com.example.model.DbscanCluster
import com.example.model.BmkgBuoyStation
import com.example.model.BmkgGroundTruthVerification
import com.example.model.BmkgVerificationStatus
import com.example.ui.theme.BorderDark
import com.example.ui.theme.ConsensusModelTrack
import com.example.ui.theme.EcmwfTrack
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.RadarGreen
import com.example.ui.theme.ServerNodeGlow
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.WeatherNext2Track
import com.example.ui.theme.WeatherNext3Track
import com.example.viewmodel.StormScanUiState
import kotlin.math.roundToInt

@Composable
fun MapHudOverlay(
    uiState: StormScanUiState,
    onSetAppScanMode: (AppScanMode) -> Unit,
    onToggleLayer: (MapLayerType) -> Unit,
    onSetTerrainStyle: (TerrainMapStyle) -> Unit,
    onSetStabilizationMode: (StabilizationDisplayMode) -> Unit,
    onSetAccuracyPct: (Int) -> Unit,
    onSetScanningSpeed: (Float) -> Unit,
    onToggleDarkMode: () -> Unit,
    onSetActiveModel: (ForecastModel) -> Unit,
    onToggleShowServers: (Boolean) -> Unit,
    onToggleShowSatellites: (Boolean) -> Unit,
    onSelectCyclone: (Cyclone) -> Unit,
    onSelectWindArea: (StrongWindArea) -> Unit,
    onCloseDetail: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetView: () -> Unit,
    onTogglePauseScan: () -> Unit,
    onSetScannerBeamStyle: (ScannerBeamStyle) -> Unit = {},
    onSetModelWeightPreset: (ModelWeightPreset) -> Unit = {},
    onSetCustomModelWeights: (Float, Float, Float, Float) -> Unit = { _, _, _, _ -> },
    onSetWindSpeedUnit: (WindSpeedUnit) -> Unit = {},
    onToggleDayNight: (Boolean) -> Unit = {},
    onToggleMiniMap: (Boolean) -> Unit = {},
    onToggleCoriolis: (Boolean) -> Unit = {},
    onToggleSonarPing: (Boolean) -> Unit = {},
    onSetDetectionSensitivity: (Int) -> Unit = {},
    onSetMapPanSensitivity: (Int) -> Unit = {},
    onSetForecastHour: (Int) -> Unit = {},
    onTogglePlayTimeline: () -> Unit = {},
    onSelectSatellite: (Satellite) -> Unit = {},
    onSelectScannedSpot: (ScannedSpot) -> Unit = {},
    onSetMapScanType: (MapScanType) -> Unit = {},
    onDismissEasterEggBanner: () -> Unit = {},
    onGetModelReports: ((Cyclone) -> List<ModelWeatherReport>)? = null,
    onToggleScanningProcessing: () -> Unit = {},
    onSetShowScanningProcessing: (Boolean) -> Unit = {},
    onSelectSurgeZone: (SurgeRiskZone) -> Unit = {},
    onToggleForceRoomCache: (Boolean) -> Unit = {},
    onToggleSurgeVectors: (Boolean) -> Unit = {},
    onDismissProbe: () -> Unit = {},
    onSetProjectionType: (MapProjectionType) -> Unit = {},
    onToggleProjectionType: () -> Unit = {},
    onSetTargetFramerate: (TargetFramerateFps) -> Unit = {},
    onToggleGpuVectorTiling: () -> Unit = {},
    onToggleDbscanClustering: () -> Unit = {},
    onTriggerBmkgVerification: (Cyclone?, ScannedSpot?) -> Unit = { _, _ -> },
    onDismissBmkgVerification: () -> Unit = {},
    onSelectDbscanCluster: (DbscanCluster?) -> Unit = {},
    onSelectBmkgBuoy: (BmkgBuoyStation?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isCleanMapMode by remember { mutableStateOf(false) }
    var isTopDetailsExpanded by remember { mutableStateOf(false) }
    var isLayersPanelExpanded by remember { mutableStateOf(false) }
    var isTopHudCollapsed by remember { mutableStateOf(true) }

    val surfaceBg = if (uiState.isDarkMode) SurfaceDark.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.92f)
    val borderCol = if (uiState.isDarkMode) BorderDark else Color(0xFFCFD8DC)
    val textPrimary = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (uiState.isDarkMode) Color.LightGray else Color(0xFF475569)

    // 100% UNRESTRICTED FULL MAP MODE (Zero UI blocking the map)
    if (isCleanMapMode) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = surfaceBg.copy(alpha = 0.88f),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.6f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp)
                    .clickable { isCleanMapMode = false }
                    .testTag("exit_clean_map_mode_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Show Controls",
                        tint = RadarCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SHOW HUD",
                        color = RadarCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (isTopHudCollapsed) {
            // Ultra-compact floating pill taking <32dp of screen space
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = surfaceBg.copy(alpha = 0.90f),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.6f)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .clickable { isTopHudCollapsed = false }
                    .testTag("expand_top_hud_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isScanningPaused) Color(0xFFFF9100) else RadarCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AEROSCAN v1.5",
                        color = textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "CONTROLS ▼",
                        color = RadarCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        } else {
            // TOP SECTION: Status Telemetry Header + Mode Switcher Tabs + Quick Target Carousels
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                // Easter Egg Overclock Alert Banner
                uiState.easterEggBannerText?.let { banner ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF261200),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD600)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .testTag("easter_egg_overclock_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD600),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = banner,
                                    color = Color(0xFFFFD600),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            IconButton(
                                onClick = onDismissEasterEggBanner,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color(0xFFFFD600),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                // 1. Top Telemetry Header (Clean, slim single line)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = surfaceBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("top_telemetry_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isScanningPaused) Color(0xFFFF9100) else RadarCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AEROSCAN",
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "v1.5",
                                color = RadarCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .background(RadarCyan.copy(alpha = 0.18f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "10cm S-BAND",
                                color = RadarGreen,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .background(RadarGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }

                        // Map controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onTogglePauseScan,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("pause_resume_scan_button")
                            ) {
                                Icon(
                                    imageVector = if (uiState.isScanningPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = "Toggle Scan Animation",
                                    tint = RadarCyan,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            IconButton(
                                onClick = onResetView,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("reset_view_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "Reset Global Center",
                                    tint = textPrimary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            IconButton(
                                onClick = onZoomIn,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("zoom_in_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ZoomIn,
                                    contentDescription = "Zoom In",
                                    tint = textPrimary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            IconButton(
                                onClick = onZoomOut,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("zoom_out_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Zoom Out",
                                    tint = textPrimary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            // Collapse button to tuck the header away
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = RadarCyan.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .clickable { isTopHudCollapsed = true }
                                    .testTag("collapse_top_bar_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Hide Controls",
                                        tint = RadarCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "HIDE ▲",
                                        color = RadarCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Primary Application Modes Selector (Cyclones | Strong Winds | Satellites | All Models | Settings)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = surfaceBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_mode_selector_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppScanMode.values().forEach { mode ->
                        val isSelected = uiState.appScanMode == mode
                        val (icon, tint) = when (mode) {
                            AppScanMode.CYCLONES -> Icons.Default.Warning to (if (isSelected) Color(0xFFFF5252) else textSecondary)
                            AppScanMode.STRONG_WINDS -> Icons.Default.Air to (if (isSelected) Color(0xFFFFB300) else textSecondary)
                            AppScanMode.SURGE_RISK -> Icons.Default.WaterDrop to (if (isSelected) Color(0xFF00E5FF) else textSecondary)
                            AppScanMode.SATELLITES -> Icons.Default.Public to (if (isSelected) Color(0xFF00E5FF) else textSecondary)
                            AppScanMode.ALL_MODELS -> Icons.Default.CompareArrows to (if (isSelected) Color(0xFFE040FB) else textSecondary)
                            AppScanMode.SETTINGS -> Icons.Default.Settings to (if (isSelected) (if (uiState.isEasterEggUnlocked) Color(0xFFFFD600) else RadarCyan) else textSecondary)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) tint.copy(alpha = 0.2f) else Color.Transparent,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, tint) else null,
                            modifier = Modifier
                                .widthIn(min = 62.dp)
                                .clickable { onSetAppScanMode(mode) }
                                .testTag("mode_tab_${mode.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = tint,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = mode.shortLabel,
                                    color = if (isSelected) tint else textSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Expand / Collapse secondary targets & engine controls
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isTopDetailsExpanded) RadarCyan.copy(alpha = 0.2f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isTopDetailsExpanded) RadarCyan else borderCol),
                        modifier = Modifier
                            .clickable { isTopDetailsExpanded = !isTopDetailsExpanded }
                            .testTag("toggle_top_targets_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 7.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isTopDetailsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (isTopDetailsExpanded) RadarCyan else textSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isTopDetailsExpanded) "LESS" else "TARGETS",
                                color = if (isTopDetailsExpanded) RadarCyan else textSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Secondary controls (Collapsible so they don't block the map)
            AnimatedVisibility(
                visible = isTopDetailsExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(6.dp))

                    // 2b. Map Scanning Engine Mode (Doppler Radar | Satellite Swath | AI Neural | Sat + AI Combined)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = surfaceBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scan_type_selector_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    MapScanType.values().forEach { scanType ->
                        val isScanSelected = uiState.mapScanType == scanType
                        val activeColor = when (scanType) {
                            MapScanType.DOPPLER_RADAR -> RadarCyan
                            MapScanType.SATELLITE_SCAN -> Color(0xFF00E5FF)
                            MapScanType.AI_NEURAL_SCAN -> Color(0xFFE040FB)
                            MapScanType.COMBINED_SATELLITE_AI -> Color(0xFFFFD600)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isScanSelected) activeColor.copy(alpha = 0.22f) else Color.Transparent,
                            border = if (isScanSelected) androidx.compose.foundation.BorderStroke(1.dp, activeColor) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSetMapScanType(scanType) }
                                .testTag("scan_type_${scanType.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (scanType == MapScanType.COMBINED_SATELLITE_AI) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = if (isScanSelected) activeColor else textSecondary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                }
                                Text(
                                    text = scanType.shortLabel,
                                    color = if (isScanSelected) activeColor else textSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isScanSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Quick Selector Carousel depending on active mode
            when (uiState.appScanMode) {
                AppScanMode.CYCLONES -> {
                    // Tropical Cyclone carousel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.cyclones.forEach { cyclone ->
                            val isSelected = uiState.selectedCyclone?.id == cyclone.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) cyclone.category.color.copy(alpha = 0.25f) else surfaceBg,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) cyclone.category.color else borderCol
                                ),
                                modifier = Modifier
                                    .clickable { onSelectCyclone(cyclone) }
                                    .testTag("storm_pill_${cyclone.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                             .clip(CircleShape)
                                            .background(cyclone.category.color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cyclone.name,
                                        color = textPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${cyclone.sustainedWindKts}kt",
                                        color = cyclone.category.color,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
                AppScanMode.STRONG_WINDS -> {
                    // Strong Wind Areas carousel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.strongWindAreas.forEach { area ->
                            val isSelected = uiState.selectedWindArea?.id == area.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) area.severity.color.copy(alpha = 0.25f) else surfaceBg,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) area.severity.color else borderCol
                                ),
                                modifier = Modifier
                                    .clickable { onSelectWindArea(area) }
                                    .testTag("wind_area_pill_${area.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Air,
                                        contentDescription = null,
                                        tint = area.severity.color,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = area.name,
                                        color = textPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "${area.sustainedWindKts}kt",
                                        color = area.severity.color,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
                AppScanMode.SURGE_RISK -> {
                    // Coastal Storm Surge Risk Zones Carousel (New Mode)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.surgeRiskZones.forEach { zone ->
                            val isSelected = uiState.selectedSurgeZone?.id == zone.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) zone.riskLevel.color.copy(alpha = 0.25f) else surfaceBg,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) zone.riskLevel.color else borderCol
                                ),
                                modifier = Modifier
                                    .clickable { onSelectSurgeZone(zone) }
                                    .testTag("surge_pill_${zone.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(zone.riskLevel.color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = zone.coastalRegion.take(18),
                                        color = textPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "+${zone.peakSurgeMeters}m",
                                        color = zone.riskLevel.color,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
                AppScanMode.SATELLITES -> {
                    // Satellite Scanner Carousel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.satellites.forEach { sat ->
                            val isSelected = uiState.selectedSatellite?.id == sat.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.25f) else surfaceBg,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFF00E5FF) else borderCol
                                ),
                                modifier = Modifier
                                    .clickable { onSelectSatellite(sat) }
                                    .testTag("sat_pill_${sat.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = sat.name,
                                        color = textPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "${sat.altitudeKm}km",
                                        color = Color(0xFF00E5FF),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
                AppScanMode.ALL_MODELS -> {
                    // All Models Weather Carousel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.cyclones.forEach { cyclone ->
                            val isSelected = uiState.selectedCyclone?.id == cyclone.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFFE040FB).copy(alpha = 0.25f) else surfaceBg,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFFE040FB) else borderCol
                                ),
                                modifier = Modifier
                                    .clickable { onSelectCyclone(cyclone) }
                                    .testTag("all_models_pill_${cyclone.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CompareArrows,
                                        contentDescription = null,
                                        tint = Color(0xFFE040FB),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "${cyclone.name} (ALL MODELS)",
                                        color = textPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "${cyclone.sustainedWindKts}kt",
                                        color = Color(0xFFE040FB),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
                AppScanMode.SETTINGS -> {
                    // Settings mode banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = (if (uiState.isEasterEggUnlocked) Color(0xFFFFD600) else RadarCyan).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, (if (uiState.isEasterEggUnlocked) Color(0xFFFFD600) else RadarCyan).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (uiState.isEasterEggUnlocked) Icons.Default.Bolt else Icons.Default.Tune,
                                contentDescription = null,
                                tint = if (uiState.isEasterEggUnlocked) Color(0xFFFFD600) else RadarCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isEasterEggUnlocked)
                                    "⚡ SYSTEM OVERCLOCKED: 15.0 SPOTS/SEC SCANNING LIMIT UNLOCKED"
                                else
                                    "CONFIGURING SCANNER & FORECAST MODEL PARAMETERS",
                                color = if (uiState.isEasterEggUnlocked) Color(0xFFFFD600) else RadarCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
}

        // FLOATING SCANNING PROCESSING HUD OVERLAY (Toggled via Settings)
        AnimatedVisibility(
            visible = uiState.showScanningProcessing && uiState.appScanMode != AppScanMode.SETTINGS,
            enter = fadeIn() + slideInVertically { -20 },
            exit = fadeOut() + slideOutVertically { -20 },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 184.dp, start = 12.dp)
        ) {
            ScanningProcessingHud(
                uiState = uiState,
                surfaceBg = surfaceBg,
                borderCol = borderCol,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onClose = onToggleScanningProcessing
            )
        }

        // BOTTOM SECTION: Details Cards, Settings Panel, Probe & Layer Dock
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mode 3: SETTINGS PANEL OVERLAY
            AnimatedVisibility(
                visible = uiState.appScanMode == AppScanMode.SETTINGS,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                RadarSettingsPanel(
                    uiState = uiState,
                    surfaceBg = surfaceBg,
                    borderCol = borderCol,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onSetAccuracyPct = onSetAccuracyPct,
                    onSetScanningSpeed = onSetScanningSpeed,
                    onSetMapPanSensitivity = onSetMapPanSensitivity,
                    onToggleDarkMode = onToggleDarkMode,
                    onSetActiveModel = onSetActiveModel,
                    onToggleShowServers = onToggleShowServers,
                    onToggleShowSatellites = onToggleShowSatellites,
                    onToggleScanningProcessing = onSetShowScanningProcessing,
                    onToggleForceRoomCache = onToggleForceRoomCache,
                    onToggleSurgeVectors = onToggleSurgeVectors,
                    onSetProjectionType = onSetProjectionType,
                    onSetScannerBeamStyle = onSetScannerBeamStyle,
                    onSetTargetFramerate = onSetTargetFramerate,
                    onToggleGpuVectorTiling = onToggleGpuVectorTiling,
                    onToggleDbscanClustering = onToggleDbscanClustering,
                    onTriggerBmkgVerification = { onTriggerBmkgVerification(uiState.selectedCyclone ?: uiState.cyclones.firstOrNull(), null) },
                    onClose = { onSetAppScanMode(AppScanMode.CYCLONES) }
                )
            }

            // Mode 1: Selected Cyclone Detail Card
            AnimatedVisibility(
                visible = uiState.appScanMode == AppScanMode.CYCLONES && uiState.selectedCyclone != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                uiState.selectedCyclone?.let { cyclone ->
                    CycloneDetailCard(
                        cyclone = cyclone,
                        stabilizationMode = uiState.stabilizationMode,
                        accuracyPct = uiState.accuracyPct,
                        forecastHours = uiState.forecastHourOffset,
                        surfaceBg = surfaceBg,
                        borderCol = borderCol,
                        textPrimary = textPrimary,
                        isBmkgVerifying = uiState.isBmkgVerifying,
                        verification = uiState.activeGroundTruthVerification,
                        onTriggerBmkgVerification = { onTriggerBmkgVerification(cyclone, null) },
                        onSetForecastHour = onSetForecastHour,
                        onClose = onCloseDetail
                    )
                }
            }

            // Mode 2: Selected Strong Wind Area Detail Card
            AnimatedVisibility(
                visible = uiState.appScanMode == AppScanMode.STRONG_WINDS && uiState.selectedWindArea != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                uiState.selectedWindArea?.let { area ->
                    StrongWindDetailCard(
                        area = area,
                        surfaceBg = surfaceBg,
                        borderCol = borderCol,
                        textPrimary = textPrimary,
                        onClose = onCloseDetail
                    )
                }
            }

            // Mode: Selected Coastal Surge Risk Zone Detail Card (New Mode)
            AnimatedVisibility(
                visible = uiState.appScanMode == AppScanMode.SURGE_RISK && uiState.selectedSurgeZone != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                uiState.selectedSurgeZone?.let { zone ->
                    SurgeDetailCard(
                        zone = zone,
                        surfaceBg = surfaceBg,
                        borderCol = borderCol,
                        textPrimary = textPrimary,
                        onClose = onCloseDetail
                    )
                }
            }

            // Server Node Card
            AnimatedVisibility(
                visible = uiState.selectedServer != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                uiState.selectedServer?.let { server ->
                    ServerDetailCard(
                        server = server,
                        surfaceBg = surfaceBg,
                        borderCol = borderCol,
                        textPrimary = textPrimary,
                        onClose = onCloseDetail
                    )
                }
            }

            // Satellite Card
            AnimatedVisibility(
                visible = uiState.selectedSatellite != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                uiState.selectedSatellite?.let { sat ->
                    SatelliteDetailCard(
                        satellite = sat,
                        surfaceBg = surfaceBg,
                        borderCol = borderCol,
                        textPrimary = textPrimary,
                        onClose = onCloseDetail
                    )
                }
            }

            // Mode: All Models Weather Ensemble Matrix Card
            AnimatedVisibility(
                visible = uiState.appScanMode == AppScanMode.ALL_MODELS,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                val currentCyclone = uiState.selectedCyclone ?: uiState.cyclones.firstOrNull()
                if (currentCyclone != null) {
                    val reports = onGetModelReports?.invoke(currentCyclone) ?: emptyList()
                    AllModelsMatrixCard(
                        cyclone = currentCyclone,
                        reports = reports,
                        surfaceBg = surfaceBg,
                        borderCol = borderCol,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onClose = { onSetAppScanMode(AppScanMode.CYCLONES) }
                    )
                }
            }

            // Scanned Spot Tactical Target Box Card
            AnimatedVisibility(
                visible = uiState.selectedScannedSpot != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                uiState.selectedScannedSpot?.let { spot ->
                    ScannedSpotDetailCard(
                        spot = spot,
                        surfaceBg = surfaceBg,
                        borderCol = borderCol,
                        textPrimary = textPrimary,
                        onClose = { onSelectScannedSpot(null as ScannedSpot) }
                    )
                }
            }

            // Ground-Truth Double-Verification Card
            AnimatedVisibility(
                visible = uiState.activeGroundTruthVerification != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                uiState.activeGroundTruthVerification?.let { verification ->
                    BmkgGroundTruthCard(
                        verification = verification,
                        surfaceBg = surfaceBg,
                        borderCol = borderCol,
                        textPrimary = textPrimary,
                        onClose = onDismissBmkgVerification
                    )
                }
            }

            // Live Atmospheric Telemetry Probe Bar (prominently showing Humidity & Air Quality)
            if (uiState.selectedCyclone == null && uiState.selectedWindArea == null &&
                uiState.selectedServer == null && uiState.selectedSatellite == null &&
                uiState.selectedScannedSpot == null && uiState.selectedSurgeZone == null &&
                uiState.appScanMode != AppScanMode.SETTINGS &&
                uiState.appScanMode != AppScanMode.ALL_MODELS
            ) {
                uiState.atmosphericProbe?.let { probe ->
                    AtmosphericProbeCard(
                        probe = probe,
                        isLoading = uiState.isProbeLoading,
                        dataFetchOrigin = uiState.dataFetchOrigin,
                        latencyMs = uiState.lastNetworkLatencyMs,
                        surfaceBg = surfaceBg,
                        borderCol = borderCol,
                        textPrimary = textPrimary,
                        onClose = onDismissProbe
                    )
                }
            }

            // 72-Hour Forecast Predictive Timeline Scrubber (v1.5 Feature)
            if (uiState.appScanMode != AppScanMode.SETTINGS) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = surfaceBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .testTag("forecast_timeline_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Play/Pause button + T+ offset indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            IconButton(
                                onClick = onTogglePlayTimeline,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        if (uiState.isPlayingTimeline) RadarCyan.copy(alpha = 0.2f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .testTag("timeline_play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (uiState.isPlayingTimeline) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (uiState.isPlayingTimeline) "Pause Forecast Animation" else "Play Forecast Animation",
                                    tint = if (uiState.isPlayingTimeline) RadarCyan else textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = if (uiState.forecastHourOffset == 0) "T-0 (LIVE)" else "T+${uiState.forecastHourOffset}h",
                                    color = if (uiState.forecastHourOffset == 0) RadarGreen else ConsensusModelTrack,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = if (uiState.isPlayingTimeline) "LOOPING" else "TIMELINE",
                                    color = if (uiState.isDarkMode) Color.LightGray else Color.DarkGray,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Stepper Pills: NOW, +12H, +24H, +36H, +48H, +72H
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                0 to "NOW",
                                12 to "+12H",
                                24 to "+24H",
                                36 to "+36H",
                                48 to "+48H",
                                72 to "+72H"
                            ).forEach { (hour, label) ->
                                val isSelected = uiState.forecastHourOffset == hour
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) RadarCyan else if (uiState.isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                                    modifier = Modifier
                                        .clickable { onSetForecastHour(hour) }
                                        .testTag("timeline_step_${hour}h")
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.Black else textPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Expandable Layer Switcher Dock (only when not in full settings mode)
            if (uiState.appScanMode != AppScanMode.SETTINGS) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = surfaceBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bottom_layer_dock")
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isLayersPanelExpanded = !isLayersPanelExpanded }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = RadarCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MAP LAYERS & MODEL ENGINE",
                                    color = textPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.activeModel.shortName,
                                    color = ConsensusModelTrack,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (isLayersPanelExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Toggle Layer Options",
                                    tint = RadarCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Expanded Section: Filter Chips, Cartography Styles & Consensus Model Switcher
                        AnimatedVisibility(
                            visible = isLayersPanelExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                // Layer Filter Chips Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    MapLayerType.values().forEach { layer ->
                                        val isActive = uiState.activeLayers.contains(layer)
                                        FilterChip(
                                            selected = isActive,
                                            onClick = { onToggleLayer(layer) },
                                            label = {
                                                Text(
                                                    text = layer.title,
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            },
                                            leadingIcon = {
                                                val icon = when (layer) {
                                                    MapLayerType.TERRAIN -> Icons.Default.Public
                                                    MapLayerType.RADAR_SCANNER -> Icons.Default.Radar
                                                    MapLayerType.CYCLONES -> Icons.Default.Warning
                                                    MapLayerType.WIND_STREAMS -> Icons.Default.Air
                                                    MapLayerType.ISOBARS -> Icons.Default.Speed
                                                    MapLayerType.HUMIDITY -> Icons.Default.WaterDrop
                                                    MapLayerType.AIR_QUALITY -> Icons.Default.Tune
                                                    MapLayerType.JET_STREAMS -> Icons.Default.Air
                                                    MapLayerType.FRONTAL_SYSTEMS -> Icons.Default.CloudQueue
                                                    MapLayerType.BATHYMETRY -> Icons.Default.WaterDrop
                                                    MapLayerType.DOPPLER_STATIONS -> Icons.Default.Radar
                                                    MapLayerType.NAV_CORRIDORS -> Icons.Default.MyLocation
                                                    MapLayerType.DATA_LINKS -> Icons.Default.ElectricBolt
                                                    MapLayerType.SATELLITES -> Icons.Default.SatelliteAlt
                                                    MapLayerType.SERVERS -> Icons.Default.Dns
                                                    MapLayerType.LIGHTNING -> Icons.Default.Bolt
                                                    MapLayerType.OPTICAL_FLOW -> Icons.Default.CompareArrows
                                                    MapLayerType.DBSCAN_CLUSTERS -> Icons.Default.Radar
                                                    MapLayerType.BMKG_BUOYS -> Icons.Default.MyLocation
                                                }
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = RadarCyan.copy(alpha = 0.25f),
                                                selectedLabelColor = RadarCyan,
                                                selectedLeadingIconColor = RadarCyan,
                                                containerColor = if (uiState.isDarkMode) SurfaceVariantDark else Color(0xFFF1F5F9),
                                                labelColor = textSecondary
                                            ),
                                            border = FilterChipDefaults.filterChipBorder(
                                                enabled = true,
                                                selected = isActive,
                                                borderColor = borderCol,
                                                selectedBorderColor = RadarCyan
                                            ),
                                            modifier = Modifier.testTag("layer_chip_${layer.name}")
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Terrain Cartography Style:",
                                    color = textSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TerrainMapStyle.values().forEach { style ->
                                        val isSelected = uiState.terrainStyle == style
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) RadarCyan.copy(alpha = 0.25f) else (if (uiState.isDarkMode) SurfaceVariantDark else Color(0xFFF1F5F9)),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isSelected) RadarCyan else borderCol
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onSetTerrainStyle(style) }
                                                .testTag("style_button_${style.name}")
                                        ) {
                                            Text(
                                                text = style.label,
                                                color = if (isSelected) RadarCyan else textSecondary,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Stabilization Forecast Model:",
                                    color = textSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ForecastModel.values().forEach { model ->
                                        val isSelected = uiState.activeModel == model
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) model.color.copy(alpha = 0.25f) else (if (uiState.isDarkMode) SurfaceVariantDark else Color(0xFFF1F5F9)),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isSelected) model.color else borderCol
                                            ),
                                            modifier = Modifier
                                                .clickable { onSetActiveModel(model) }
                                                .testTag("model_select_${model.shortName}")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(model.color, CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = model.shortName,
                                                    color = if (isSelected) model.color else textSecondary,
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SETTINGS PANEL COMPONENT
// -------------------------------------------------------------

@Composable
private fun RadarSettingsPanel(
    uiState: StormScanUiState,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onSetAccuracyPct: (Int) -> Unit,
    onSetScanningSpeed: (Float) -> Unit,
    onSetMapPanSensitivity: (Int) -> Unit,
    onToggleDarkMode: () -> Unit,
    onSetActiveModel: (ForecastModel) -> Unit,
    onToggleShowServers: (Boolean) -> Unit,
    onToggleShowSatellites: (Boolean) -> Unit,
    onToggleScanningProcessing: (Boolean) -> Unit = {},
    onToggleForceRoomCache: (Boolean) -> Unit = {},
    onToggleSurgeVectors: (Boolean) -> Unit = {},
    onSetProjectionType: (MapProjectionType) -> Unit = {},
    onSetScannerBeamStyle: (ScannerBeamStyle) -> Unit = {},
    onSetTargetFramerate: (TargetFramerateFps) -> Unit = {},
    onToggleGpuVectorTiling: () -> Unit = {},
    onToggleDbscanClustering: () -> Unit = {},
    onTriggerBmkgVerification: () -> Unit = {},
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = surfaceBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.8f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("radar_settings_panel")
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = RadarCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RADAR SETTINGS",
                        color = textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = RadarCyan.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan)
                    ) {
                        Text(
                            text = "v1.5",
                            color = RadarCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close Settings", tint = textSecondary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // App Version & Status Information Card
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = RadarCyan.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_version_info_card")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AEROSCAN SUITE",
                            color = textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Release 2026.10 • Build 7 • Target SDK 36",
                            color = textSecondary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = RadarCyan
                    ) {
                        Text(
                            text = "VERSION 1.5",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Map Projection Mode Selector (3D Globe vs 2D Map)
            Column {
                Text(
                    text = "Map Projection Engine",
                    color = textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Toggle between true spherical 3D Earth and planar tactical cartography",
                    color = textSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MapProjectionType.values().forEach { proj ->
                        val isSelected = uiState.projectionType == proj
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) RadarCyan.copy(alpha = 0.22f) else if (uiState.isDarkMode) SurfaceVariantDark else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RadarCyan else borderCol),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSetProjectionType(proj) }
                                .testTag("projection_select_${proj.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (proj == MapProjectionType.GLOBE_3D) Icons.Default.Public else Icons.Default.Map,
                                    contentDescription = null,
                                    tint = if (isSelected) RadarCyan else textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = proj.label,
                                        color = if (isSelected) RadarCyan else textPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = if (proj == MapProjectionType.GLOBE_3D) "Spherical Orbit" else "Planar Cylindrical",
                                        color = textSecondary,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Radar Scanner Acquisition Engine Selector
            Column {
                Text(
                    text = "Radar Scanner Acquisition Engine",
                    color = textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Select scanning geometry: Volumetric 3D Box (advanced), Laser beam, or Phosphor sweep",
                    color = textSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ScannerBeamStyle.values().forEach { style ->
                        val isSelected = uiState.scannerBeamStyle == style
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) RadarCyan.copy(alpha = 0.25f) else if (uiState.isDarkMode) SurfaceVariantDark else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RadarCyan else borderCol),
                            modifier = Modifier
                                .clickable { onSetScannerBeamStyle(style) }
                                .testTag("scanner_style_${style.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (style == ScannerBeamStyle.SCANNING_BOX) Icons.Default.Radar else Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = if (isSelected) RadarCyan else textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = style.label,
                                    color = if (isSelected) RadarCyan else textPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Accuracy Slider: 20% - 100%
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stabilization Accuracy",
                        color = textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${uiState.accuracyPct}%",
                        color = RadarCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Adjusts Kalman filter ensemble confidence & cone of uncertainty",
                    color = textSecondary,
                    fontSize = 10.sp
                )
                Slider(
                    value = uiState.accuracyPct.toFloat(),
                    onValueChange = { onSetAccuracyPct(it.roundToInt()) },
                    valueRange = 20f..100f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = RadarCyan,
                        activeTrackColor = RadarCyan,
                        inactiveTrackColor = borderCol
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("accuracy_slider")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("20% (Raw Variance)", color = textSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text("100% (Fused Lock)", color = textSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Radar Sweep Cadence Slider: 5.0 - 10.0 sweeps/sec (10cm S-Band WSR-88D Level II)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Doppler Sweep Cadence (10cm S-Band)",
                            color = textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (uiState.isEasterEggUnlocked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "⚡ 15.0 SWEEPS/S OVERCLOCK",
                                color = Color(0xFFFFD600),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .background(Color(0x33FFD600), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = String.format("%.1f sweeps/s", uiState.scanningSpeedSpotsPerSec),
                        color = if (uiState.isEasterEggUnlocked) Color(0xFFFFD600) else RadarGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Controls real-time 10cm S-Band (2.8 GHz) antenna rotation & Doppler precipitation cell acquisition rate",
                    color = textSecondary,
                    fontSize = 10.sp
                )
                val maxLimit = if (uiState.isEasterEggUnlocked) 15.0f else 10.0f
                val sliderSteps = if (uiState.isEasterEggUnlocked) 19 else 9
                Slider(
                    value = uiState.scanningSpeedSpotsPerSec,
                    onValueChange = { onSetScanningSpeed(it) },
                    valueRange = 5.0f..maxLimit,
                    steps = sliderSteps,
                    colors = SliderDefaults.colors(
                        thumbColor = if (uiState.isEasterEggUnlocked) Color(0xFFFFD600) else RadarGreen,
                        activeTrackColor = if (uiState.isEasterEggUnlocked) Color(0xFFFFD600) else RadarGreen,
                        inactiveTrackColor = borderCol
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scanning_speed_slider")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("5.0 sweeps/s (VCP-31 Surveillance)", color = textSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    if (uiState.isEasterEggUnlocked) {
                        Text("15.0 sweeps/s (⚡ OVERCLOCK UNLOCKED)", color = Color(0xFFFFD600), fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    } else {
                        Text("10.0 sweeps/s (10cm High Speed)", color = textSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2b. Educational Radar Science Card: What does 10cm Radar Scanner mean?
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = RadarCyan.copy(alpha = 0.10f),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.40f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("radar_10cm_explanation_card")
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = null,
                            tint = RadarCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WHAT DOES '10cm SCANNER' MEAN?",
                            color = RadarCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "In operational meteorology, '10cm Scanner' designates the 10-centimeter microwave wavelength (S-Band, 2.7–3.0 GHz) used by NOAA WSR-88D NEXRAD and global Doppler networks. Unlike shorter 3cm (X-Band) or 5cm (C-Band) radars, 10cm radio waves suffer zero signal attenuation when penetrating intense hurricane eyewalls, severe hail cores, and torrential tropical rainbands, delivering unattenuated Level II Doppler reflectivity (dBZ) and true precipitation rates.",
                        color = textPrimary,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2b. Map Swipe & Movement Sensitivity Slider
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (uiState.isDarkMode) SurfaceVariantDark else Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = RadarCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Map Swipe & Pan Sensitivity:",
                                color = textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${uiState.mapPanSensitivityPct}%",
                            color = RadarCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = "Calibrates touch drag velocity and gesture responsiveness to keep panning smooth and steady",
                        color = textSecondary,
                        fontSize = 10.sp
                    )
                    Slider(
                        value = uiState.mapPanSensitivityPct.toFloat(),
                        onValueChange = { onSetMapPanSensitivity(it.roundToInt()) },
                        valueRange = 20f..100f,
                        steps = 15,
                        colors = SliderDefaults.colors(
                            thumbColor = RadarCyan,
                            activeTrackColor = RadarCyan,
                            inactiveTrackColor = borderCol
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("map_pan_sensitivity_slider")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("20% (Tactical Damped)", color = textSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("60% (Optimal Smooth)", color = RadarCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("100% (High Speed)", color = textSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Theme Mode Switcher: Light Mode <-> Dark Mode
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (uiState.isDarkMode) SurfaceVariantDark else Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (uiState.isDarkMode) Color(0xFFB388FF) else Color(0xFFFFB300),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (uiState.isDarkMode) "Tactical Dark Mode" else "Daylight Chart Mode",
                                color = textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (uiState.isDarkMode) "Optimal for night & low-glare radar monitoring" else "High-contrast daylight nautical cartography",
                                color = textSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                    Switch(
                        checked = uiState.isDarkMode,
                        onCheckedChange = { onToggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF7C4DFF),
                            uncheckedThumbColor = Color(0xFFFFB300),
                            uncheckedTrackColor = Color(0xFFCFD8DC)
                        ),
                        modifier = Modifier.testTag("theme_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Switch Between Meteorological Forecast Models
            Text(
                text = "Select Active Forecast Model Engine:",
                color = textPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ForecastModel.values().forEach { model ->
                    val isSelected = uiState.activeModel == model
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) model.color.copy(alpha = 0.22f) else (if (uiState.isDarkMode) SurfaceVariantDark else Color(0xFFF1F5F9)),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) model.color else borderCol
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSetActiveModel(model) }
                            .testTag("settings_model_${model.shortName}")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(model.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = model.displayName,
                                        color = if (isSelected) model.color else textPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${model.resolution} • ${model.provider}",
                                        color = textSecondary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = model.color, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Additional Radar Telemetry & Scanning Processing Toggles
            Text(
                text = "Hardware & Telemetry Layers:",
                color = textPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (uiState.isDarkMode) SurfaceVariantDark else Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Scanning Processing Setting Toggle (User Request)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("Show Scanning Processing HUD", color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("Live DSP pulse compression, Doppler FFT spectra, AI tensor inference & Room DB cache", color = textSecondary, fontSize = 9.sp)
                        }
                        Switch(
                            checked = uiState.showScanningProcessing,
                            onCheckedChange = onToggleScanningProcessing,
                            modifier = Modifier.testTag("scanning_processing_toggle")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Show HPC Supercomputer Servers", color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("ECMWF, WeatherNext AI, NOAA clusters", color = textSecondary, fontSize = 9.sp)
                        }
                        Switch(
                            checked = uiState.showServersOnMap,
                            onCheckedChange = onToggleShowServers,
                            modifier = Modifier.testTag("servers_toggle")
                        )
                    }

                    // Force Offline / Room DB Cache Mode (New Setting)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Force Room DB Cache Only", color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (uiState.forceRoomCacheOnly) "OFFLINE" else "LIVE NET",
                                    color = if (uiState.forceRoomCacheOnly) Color(0xFFFFB300) else RadarGreen,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background((if (uiState.forceRoomCacheOnly) Color(0xFFFFB300) else RadarGreen).copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Text("Simulate intermittent or zero connectivity by reading all storm data strictly from local Room SQLite cache (aeroscan_weather_cache.db)", color = textSecondary, fontSize = 9.sp)
                        }
                        Switch(
                            checked = uiState.forceRoomCacheOnly,
                            onCheckedChange = onToggleForceRoomCache,
                            modifier = Modifier.testTag("force_room_cache_toggle")
                        )
                    }

                    // Coastal Storm Surge & Inundation Vectors (New Setting)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("Coastal Surge Vectors & Inundation Matrix", color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("Renders coastal bathymetry contours, wave inundation arrows, and flood warning cones", color = textSecondary, fontSize = 9.sp)
                        }
                        Switch(
                            checked = uiState.showSurgeVectors,
                            onCheckedChange = onToggleSurgeVectors,
                            modifier = Modifier.testTag("surge_vectors_toggle")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Show Satellite Downlinks & Footprints", color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("GOES, Himawari, Meteosat constellations", color = textSecondary, fontSize = 9.sp)
                        }
                        Switch(
                            checked = uiState.showSatelliteTelemetry,
                            onCheckedChange = onToggleShowSatellites,
                            modifier = Modifier.testTag("satellites_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // =========================================================================
                    // 1. HIGH-PERFORMANCE GRAPHICS ENGINE (GPU VECTOR TILING)
                    // =========================================================================
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RadarCyan.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("GPU Vector Tiling Engine", color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "OPENGL / WEBGL",
                                            color = RadarCyan,
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(RadarCyan.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text("Eliminates CPU rendering bottlenecks during dense spatial mapping cycles. Shaders & vertex arrays active.", color = textSecondary, fontSize = 9.sp)
                                }
                                Switch(
                                    checked = uiState.gpuVectorTilingEnabled,
                                    onCheckedChange = { onToggleGpuVectorTiling() },
                                    modifier = Modifier.testTag("gpu_vector_tiling_toggle")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Framerate Target Selector (Locked 60 FPS default)
                            Text(
                                text = "Refresh Rules • Framerate Target (Dynamic):",
                                color = textPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TargetFramerateFps.values().forEach { fps ->
                                    val isSelected = uiState.targetFramerate == fps
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) RadarCyan.copy(alpha = 0.28f) else Color.Transparent,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RadarCyan else Color.White.copy(alpha = 0.15f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onSetTargetFramerate(fps) }
                                            .testTag("framerate_${fps.name}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "${fps.fps} FPS",
                                                color = if (isSelected) RadarCyan else textPrimary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = if (fps == TargetFramerateFps.FPS_60) "DEFAULT LOCK" else "${fps.intervalMs}ms",
                                                color = if (isSelected) RadarCyan else textSecondary,
                                                fontSize = 7.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // =========================================================================
                    // 2. SAT + AI PREDICTIVE INFRASTRUCTURE (DBSCAN CLUSTERING)
                    // =========================================================================
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE040FB).copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE040FB).copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Spatial DBSCAN Clustering", color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "OPTICAL FLOW + AI",
                                            color = Color(0xFFE040FB),
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(Color(0xFFE040FB).copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text("Density-Based Spatial Clustering: Dynamically groups and isolates high-density radar cores and lightning arrays from noise.", color = textSecondary, fontSize = 9.sp)
                                }
                                Switch(
                                    checked = uiState.dbscanClusteringEnabled,
                                    onCheckedChange = { onToggleDbscanClustering() },
                                    modifier = Modifier.testTag("dbscan_clustering_toggle")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // =========================================================================
                    // 3. GROUND-TRUTH VALIDATION FRAMEWORK (BMKG TELEMETRY HOOK)
                    // =========================================================================
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF00E676).copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("BMKG Maritime Ground-Truth Hook", color = textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "INDONESIA AGENCY",
                                            color = Color(0xFF00E676),
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(Color(0xFF00E676).copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text("Fetches live surface telemetry and maritime buoy data from BMKG. Double-verifies barometric pressure & wind speeds.", color = textSecondary, fontSize = 9.sp)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF00E676),
                                    modifier = Modifier
                                        .clickable { onTriggerBmkgVerification() }
                                        .testTag("trigger_bmkg_verification_button")
                                ) {
                                    Text(
                                        text = "RUN TEST",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // App Version Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AEROSCAN v1.5 • BUILD 7 (RELEASE-2026)",
                            color = textSecondary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BmkgGroundTruthCard(
    verification: BmkgGroundTruthVerification,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    onClose: () -> Unit
) {
    val statusColor = verification.status.color
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = surfaceBg,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, statusColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bmkg_ground_truth_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BMKG GROUND-TRUTH VERIFICATION",
                        color = textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(5.dp),
                color = statusColor.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${verification.status.badge}: ${verification.status.label.uppercase()}",
                        color = statusColor,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.35f),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderCol)
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "TARGET: ${verification.targetName.uppercase()}",
                        color = textPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "STATION: ${verification.nearestStation.name} (${verification.distanceKm.toInt()} km away • ${verification.nearestStation.oceanBasin})",
                        color = Color.LightGray,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "PRESSURE: AI ${verification.aiPredictedPressureHpa} hPa vs Buoy ${verification.nearestStation.barometricPressureHpa} hPa (ΔP ${verification.deltaPressureHpa} hPa)",
                        color = RadarCyan,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "WIND SPEED: AI ${verification.aiPredictedWindKts} kt vs Buoy ${verification.nearestStation.windSpeedKts} kt (ΔV ${verification.deltaWindKts} kt)",
                        color = RadarGreen,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = RadarCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "CALIBRATED OUTPUT PROFILE: ${verification.normalizedPressureHpa} hPa • ${verification.normalizedWindKts} kt",
                            color = RadarCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DETAIL CARDS
// -------------------------------------------------------------

@Composable
private fun StrongWindDetailCard(
    area: StrongWindArea,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, area.severity.color),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("strong_wind_detail_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Air, contentDescription = null, tint = area.severity.color, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = area.name.uppercase(),
                            color = textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${area.severity.label} • ${area.oceanRegion}",
                            color = area.severity.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close Detail", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("SUSTAINED WIND", "${area.sustainedWindKts} kts", area.severity.color)
                MetricColumn("PEAK GUSTS", "${area.gustKts} kts", Color(0xFFFFB300))
                MetricColumn("PRESSURE", "${area.pressureHpa} hPa", RadarCyan)
                MetricColumn("GALE RADIUS", "${area.radiusKm} km", Color(0xFF00E5FF))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = area.systemDescription,
                color = Color.LightGray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun CycloneDetailCard(
    cyclone: Cyclone,
    stabilizationMode: StabilizationDisplayMode,
    accuracyPct: Int,
    forecastHours: Int = 0,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    isBmkgVerifying: Boolean = false,
    verification: BmkgGroundTruthVerification? = null,
    onTriggerBmkgVerification: () -> Unit = {},
    onSetForecastHour: (Int) -> Unit = {},
    onClose: () -> Unit
) {
    val forecastLoc = if (forecastHours > 0) getCycloneForecastLocation(cyclone, forecastHours) else cyclone.currentLocation
    val (forecastWind, forecastPressure) = if (forecastHours > 0) getCycloneForecastIntensity(cyclone, forecastHours) else ForecastIntensity(cyclone.sustainedWindKts, cyclone.centralPressureHpa)
    val activeEFColor = getCycloneIntensityColor(forecastWind)
    val activeEFAccent = getCycloneIntensityAccentColor(forecastWind)
    val activeEFLabel = getCycloneIntensityCategory(forecastWind)

    var isExpanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val checkpoints = remember(cyclone) { getCycloneTrackHistoryAndForecast(cyclone) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = surfaceBg.copy(alpha = 0.92f),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, activeEFColor.copy(alpha = 0.85f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cyclone_detail_card")
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
            // COMPACT HEADER: Storm Name, Live Telemetry Strip, Track Expand Toggle & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).clickable { isExpanded = !isExpanded }
                ) {
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .background(activeEFColor, CircleShape)
                            .border(1.dp, activeEFAccent, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = cyclone.name.uppercase(),
                                color = textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = activeEFColor.copy(alpha = 0.30f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, activeEFColor)
                            ) {
                                Text(
                                    text = activeEFLabel,
                                    color = activeEFAccent,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        // Live Telemetry Strip (Permanently visible in compact mode - never covered!)
                        val coordText = "${String.format(java.util.Locale.US, "%.1f°%s, %.1f°%s", kotlin.math.abs(forecastLoc.lat), if (forecastLoc.lat >= 0) "N" else "S", kotlin.math.abs(forecastLoc.lon), if (forecastLoc.lon >= 0) "E" else "W")}"
                        Text(
                            text = "TELEMETRY: $forecastWind kt • $forecastPressure hPa • $coordText • HUM ${cyclone.relativeHumidityPct}%",
                            color = RadarCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // BMKG Ground-Truth Double Verification Trigger Chip
                    Surface(
                        shape = RoundedCornerShape(5.dp),
                        color = if (isBmkgVerifying) Color(0xFFFFD600).copy(alpha = 0.25f) else Color(0xFF00E676).copy(alpha = 0.20f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isBmkgVerifying) Color(0xFFFFD600) else Color(0xFF00E676)),
                        modifier = Modifier
                            .clickable { onTriggerBmkgVerification() }
                            .padding(end = 4.dp)
                            .testTag("verify_bmkg_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = if (isBmkgVerifying) Color(0xFFFFD600) else Color(0xFF00E676),
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isBmkgVerifying) "BMKG..." else "BMKG VERIFY",
                                color = if (isBmkgVerifying) Color(0xFFFFD600) else Color(0xFF00E676),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Expand/Collapse Toggle Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isExpanded) RadarCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isExpanded) RadarCyan else Color.White.copy(alpha = 0.20f)),
                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded }
                            .padding(end = 4.dp)
                            .testTag("toggle_expand_cyclone_track")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpanded) "LESS ▲" else "TRACK ▼",
                                color = if (isExpanded) RadarCyan else Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    IconButton(onClick = onClose, modifier = Modifier.size(26.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Detail",
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // EXPANDED SECTION (Shown only when user taps "TRACK ▼" so it NEVER blocks the map unnecessarily!)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    // Tab Switcher (Track Timeline vs Full Ensemble Matrix)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedTab == 0) RadarCyan.copy(alpha = 0.22f) else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedTab == 0) RadarCyan else Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 0 }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚡ ZOOM EARTH TIMELINE",
                                    color = if (selectedTab == 0) RadarCyan else Color.LightGray,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedTab == 1) RadarCyan.copy(alpha = 0.22f) else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedTab == 1) RadarCyan else Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 1 }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📊 ENSEMBLE METRICS",
                                    color = if (selectedTab == 1) RadarCyan else Color.LightGray,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (selectedTab == 0) {
                        // Zoom Earth Timeline List (Constrained to max 145dp)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 145.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            checkpoints.forEach { cp ->
                                val cpColor = getCycloneIntensityColor(cp.sustainedWindKts)
                                val cpAccent = getCycloneIntensityAccentColor(cp.sustainedWindKts)
                                val cpCategory = getCycloneIntensityCategory(cp.sustainedWindKts)
                                val isRowActive = (forecastHours == cp.hoursOffset) || (forecastHours <= 0 && cp.isCurrent)

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isRowActive) cpColor.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.35f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isRowActive) 1.2.dp else 1.dp,
                                        if (isRowActive) cpAccent else Color.White.copy(alpha = 0.10f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSetForecastHour(cp.hoursOffset.coerceAtLeast(0)) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(cpColor, CircleShape)
                                                .border(1.dp, if (isRowActive) Color.White else cpAccent, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${cp.timeLabel} • ${cp.dateLabel}",
                                                    color = if (isRowActive) Color.White else textPrimary,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                Text(
                                                    text = cpCategory,
                                                    color = cpAccent,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                            val kmh = (cp.sustainedWindKts * 1.852f).toInt()
                                            val mph = (cp.sustainedWindKts * 1.151f).toInt()
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${cp.sustainedWindKts} kt • $kmh km/h • ${cp.pressureHpa} hPa",
                                                    color = cpAccent,
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                if (isRowActive) {
                                                    Text(
                                                        text = "● ACTIVE",
                                                        color = RadarGreen,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Ensemble Metrics Tab
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricColumn("WIND", "$forecastWind kts", activeEFColor)
                            MetricColumn("GUSTS", "${(forecastWind * 1.2f).toInt()} kts", Color(0xFFFFB300))
                            MetricColumn("PRESSURE", "$forecastPressure hPa", RadarCyan)
                            MetricColumn("SPREAD", "±${cyclone.modelSpreadKm} km", RadarGreen)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerDetailCard(
    server: ServerNode,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, ServerNodeGlow),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("server_detail_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Dns, contentDescription = null, tint = ServerNodeGlow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = server.name,
                            color = textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${server.city}, ${server.country} • ${server.organization}",
                            color = ServerNodeGlow,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close Detail", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = server.role, color = Color.LightGray, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("LATENCY", "${server.latencyMs} ms", ServerNodeGlow)
                MetricColumn("COMPUTE LOAD", "${server.computeLoadPct}%", Color(0xFFFFB300))
                MetricColumn("THROUGHPUT", "${server.throughputTbps} Tb/s", RadarCyan)
                MetricColumn("SATELLITES", "${server.connectedSatellitesCount} linked", Color(0xFFB388FF))
            }
        }
    }
}

@Composable
private fun SatelliteDetailCard(
    satellite: Satellite,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB388FF)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("satellite_detail_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SatelliteAlt, contentDescription = null, tint = Color(0xFFB388FF), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = satellite.name, color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${satellite.agency} • ${satellite.orbitType}", color = Color(0xFFB388FF), fontSize = 11.sp)
                    }
                }

                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close Detail", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("ALTITUDE", "${satellite.altitudeKm} km", textPrimary)
                MetricColumn("CADENCE", "${satellite.scanIntervalMin} min", RadarGreen)
                MetricColumn("STATUS", "NOMINAL", RadarCyan)
            }
        }
    }
}

@Composable
private fun AtmosphericProbeCard(
    probe: AtmosphericProbe,
    isLoading: Boolean,
    dataFetchOrigin: DataFetchOrigin,
    latencyMs: Long,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    onClose: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = surfaceBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("atmospheric_probe_card")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f, fill = false)) {
                    val originCol = if (dataFetchOrigin == DataFetchOrigin.NETWORK_LIVE) RadarGreen else Color(0xFFFFB300)
                    Box(modifier = Modifier.size(6.dp).background(originCol, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PROBE [${dataFetchOrigin.shortTag} ${latencyMs}ms]: ${String.format("%.1f°", probe.point.lat)}, ${String.format("%.1f°", probe.point.lon)}",
                        color = originCol,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoading) {
                        Text(
                            text = "FETCHING API...",
                            color = RadarCyan,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(22.dp)
                            .testTag("dismiss_probe_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss Probe",
                            tint = textPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("WIND", "${probe.windSpeedKts} kts (${probe.windDirectionDeg}°)", Color(0xFFFFD600))
                MetricColumn("HUMIDITY", "${probe.humidityPct}%", Color(0xFF00E5FF))
                val aqiColor = when {
                    probe.aqi <= 50 -> Color(0xFF00E676)
                    probe.aqi <= 100 -> Color(0xFFFFD600)
                    else -> Color(0xFFFF5252)
                }
                MetricColumn("AIR QUALITY", "AQI ${probe.aqi} (${probe.aqiCategory})", aqiColor)
                MetricColumn("PRESSURE", "${probe.pressureHpa.roundToInt()} hPa", textPrimary)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("PM2.5 / O3", "${probe.pm25} µg • ${probe.ozonePpb}ppb", Color(0xFFB388FF))
                MetricColumn("TEMP / DEW", "${probe.temperatureC.roundToInt()}° / ${probe.dewPointC.roundToInt()}°C", Color(0xFF81D4FA))
                MetricColumn("NEAREST CYCLONE", probe.nearestCycloneName ?: "None (<5000km)", Color(0xFFFF5252))
            }
        }
    }
}

@Composable
private fun ScannedSpotDetailCard(
    spot: ScannedSpot,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceBg,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF1744)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scanned_spot_detail_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val spotAccent = com.example.model.getReflectivityColor(spot.reflectivityDbz)
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(spotAccent, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "10cm S-BAND DOPPLER: ${spot.name}",
                                color = textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "[${spot.type.tag}]",
                                color = spot.type.color,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        val coordText = "${String.format(java.util.Locale.US, "%.2f°%s, %.2f°%s", kotlin.math.abs(spot.location.lat), if (spot.location.lat >= 0) "N" else "S", kotlin.math.abs(spot.location.lon), if (spot.location.lon >= 0) "E" else "W")}"
                        Text(
                            text = "STATION: ${spot.radarStationCallsign} • LEVEL II DUAL-POL • $coordText",
                            color = RadarCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Detail",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Doppler Radar Physical Telemetry Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("REFLECTIVITY", "${String.format("%.1f", spot.reflectivityDbz)} dBZ", com.example.model.getReflectivityColor(spot.reflectivityDbz))
                MetricColumn("ECHO TOP", "${spot.echoTopKft},000 FT", Color(0xFFE040FB))
                if (spot.precipitationRateMmH > 0f) {
                    MetricColumn("RAIN RATE (Z-R)", "${String.format("%.1f", spot.precipitationRateMmH)} mm/h", Color(0xFF00B0FF))
                } else {
                    MetricColumn("WIND INTENSITY", "${spot.windKts} kts", Color(0xFFFF9100))
                }
                MetricColumn("CONFIDENCE", "${String.format("%.1f", spot.accuracyPct)}%", RadarGreen)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Atmospheric Telemetry Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("RADAR CATEGORY", com.example.model.getReflectivityCategory(spot.reflectivityDbz), com.example.model.getReflectivityColor(spot.reflectivityDbz))
                MetricColumn("HUMIDITY", "${spot.humidityPct}%", Color(0xFF00E5FF))
                val aqiCol = if (spot.aqi <= 50) Color(0xFF00E676) else if (spot.aqi <= 100) Color(0xFFFFD600) else Color(0xFFFF5252)
                MetricColumn("AIR QUALITY", "AQI ${spot.aqi}", aqiCol)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Real Radar Science Footnote
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = RadarCyan.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "📡 10cm S-Band (2.7–3.0 GHz WSR-88D): Global meteorological standard radar wavelength. Zero signal attenuation through hurricane eyewalls and torrential monsoons.",
                    color = textPrimary.copy(alpha = 0.75f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun AllModelsMatrixCard(
    cyclone: Cyclone,
    reports: List<ModelWeatherReport>,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceBg,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE040FB)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("all_models_matrix_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = Color(0xFFE040FB),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "MULTI-MODEL ENSEMBLE: ${cyclone.name.uppercase()}",
                            color = textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "COMPARING ECMWF, GFS, WN-3 AI, WN-2, JMA & STABILIZED CONSENSUS",
                            color = Color(0xFFE040FB),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(26.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Models Weather Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                reports.forEach { report ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceVariantDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, report.model.color.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(report.model.color, CircleShape))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = report.model.displayName,
                                        color = report.model.color,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = "${report.confidencePct}% CONFIDENCE",
                                    color = if (report.confidencePct > 95f) RadarGreen else Color(0xFFFFD600),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("WIND: ${report.windSpeedKts} kts", color = textPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("PRES: ${report.centralPressureHpa} hPa", color = RadarCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("HUM: ${report.humidityPct}%", color = Color(0xFF00E5FF), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("ERR: ±${report.trackErrorKm} km", color = Color.LightGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = report.forecastSummary,
                                color = textSecondary,
                                fontSize = 9.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(label: String, value: String, valueColor: Color) {
    Column {
        Text(
            text = label,
            color = Color(0xFF7A8EA8),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

// -------------------------------------------------------------
// SCANNING PROCESSING TELEMETRY HUD COMPONENT
// -------------------------------------------------------------

@Composable
private fun ScanningProcessingHud(
    uiState: StormScanUiState,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = surfaceBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, RadarCyan.copy(alpha = 0.7f)),
        modifier = Modifier
            .widthIn(max = 285.dp)
            .testTag("scanning_processing_hud")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(RadarGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SCANNING DSP & CACHE",
                        color = textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ROOM DB: ${uiState.localCacheCount}",
                        color = RadarGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(RadarGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onClose, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Hide Telemetry", tint = textSecondary, modifier = Modifier.size(13.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Stage details
            Text(
                text = "STAGE: ${uiState.dspProcessingStage}",
                color = RadarCyan,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Doppler FFT Spectral Peak Visualizer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                uiState.fftSpectralPeaks.forEach { peak ->
                    val barHeight = (peak * 20f).coerceIn(4f, 22f)
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(barHeight.dp)
                            .background(
                                color = if (peak > 0.85f) Color(0xFFFF1744) else if (peak > 0.65f) RadarCyan else RadarGreen,
                                shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DOPPLER FFT 1.2GHz",
                    color = textSecondary,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "PRF ${uiState.rfPrfHz}Hz",
                    color = textSecondary,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Latency & Frame Telemetry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI LATENCY: ${String.format("%.1f", uiState.aiInferenceLatencyMs)}ms",
                    color = textPrimary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "FRAMES: #${uiState.framesProcessedCount}",
                    color = textSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// -------------------------------------------------------------
// DYNAMIC DATA SOURCE STATUS INDICATOR COMPONENT
// -------------------------------------------------------------

@Composable
fun DataSourceStatusIndicator(
    dataFetchOrigin: DataFetchOrigin,
    latencyMs: Long,
    cacheCount: Int,
    isForceCache: Boolean,
    onToggleForce: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNetwork = dataFetchOrigin == DataFetchOrigin.NETWORK_LIVE && !isForceCache
    val badgeColor = if (isNetwork) RadarGreen else Color(0xFFFFB300)
    val icon = if (isNetwork) Icons.Default.CloudQueue else Icons.Default.Storage
    val labelText = if (isNetwork) "LIVE NET ${latencyMs}ms" else "ROOM DB ($cacheCount)"

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = badgeColor.copy(alpha = 0.16f),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.8f)),
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onToggleForce)
            .testTag("data_source_status_indicator")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(badgeColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = icon,
                contentDescription = "Data Source Origin",
                tint = badgeColor,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = labelText,
                color = badgeColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// -------------------------------------------------------------
// SURGE RISK ZONE DETAIL CARD COMPONENT
// -------------------------------------------------------------

@Composable
private fun SurgeDetailCard(
    zone: SurgeRiskZone,
    surfaceBg: Color,
    borderCol: Color,
    textPrimary: Color,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceBg,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, zone.riskLevel.color),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("surge_detail_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(zone.riskLevel.color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = zone.coastalRegion.uppercase(),
                            color = textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${zone.riskLevel.label} • ${zone.oceanBasin}",
                            color = zone.riskLevel.color,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Surge Detail",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("PEAK SURGE", "+${zone.peakSurgeMeters}m (${zone.peakSurgeFeet}ft)", zone.riskLevel.color)
                MetricColumn("TIDE TIMING", zone.highTideTiming, Color(0xFF00E5FF))
                MetricColumn("SHELF DEPTH", "${zone.bathymetryDepthMeters}m", RadarCyan)
                MetricColumn("INUNDATION ZONE", "${zone.inundationRadiusKm} km", Color(0xFFFF9100))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = zone.riskLevel.color.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, zone.riskLevel.color.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = zone.riskLevel.color,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${zone.evacuationStatus} • Associated with ${zone.associatedCyclone}",
                        color = textPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
