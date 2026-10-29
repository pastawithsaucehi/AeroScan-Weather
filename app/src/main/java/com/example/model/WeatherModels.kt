package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ConsensusModelTrack
import com.example.ui.theme.EcmwfTrack
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.StormAmber
import com.example.ui.theme.StormOrange
import com.example.ui.theme.StormPurple
import com.example.ui.theme.StormRed
import com.example.ui.theme.WeatherNext2Track
import com.example.ui.theme.WeatherNext3Track

data class GeoPoint(
    val lat: Double,
    val lon: Double
)

enum class CycloneCategory(
    val label: String,
    val shortCode: String,
    val color: Color,
    val minWindKts: Int
) {
    TROPICAL_DEPRESSION("Tropical Depression", "TD", Color(0xFF4FC3F7), 25),
    TROPICAL_STORM("Tropical Storm", "TS", RadarCyan, 34),
    CATEGORY_1("Category 1 Hurricane", "CAT 1", Color(0xFFFFEB3B), 64),
    CATEGORY_2("Category 2 Hurricane", "CAT 2", StormAmber, 83),
    CATEGORY_3("Category 3 Major Hurricane", "CAT 3", StormOrange, 96),
    CATEGORY_4("Category 4 Major Hurricane", "CAT 4", StormRed, 113),
    CATEGORY_5("Category 5 Catastrophic Hurricane", "CAT 5", Color(0xFFD50000), 137),
    SUPER_TYPHOON("Category 5 Super Typhoon", "STY", StormPurple, 150)
}

data class ForecastTrackPoint(
    val point: GeoPoint,
    val hoursAhead: Int,
    val windSpeedKts: Int,
    val pressureHpa: Int
)

enum class ForecastModel(
    val displayName: String,
    val color: Color,
    val shortName: String,
    val provider: String,
    val resolution: String,
    val description: String
) {
    STABILIZED_ENSEMBLE(
        displayName = "Stabilized Ensemble",
        color = ConsensusModelTrack,
        shortName = "STABILIZED",
        provider = "Consensus Engine",
        resolution = "Fused 4km",
        description = "Kalman-filtered consensus of ECMWF HRES and WeatherNext 2/3 eliminating track divergence"
    ),
    ECMWF_IFS(
        displayName = "ECMWF IFS (HRES)",
        color = EcmwfTrack,
        shortName = "ECMWF",
        provider = "European Centre for Medium-Range Weather",
        resolution = "9 km",
        description = "World-standard numerical weather prediction model with 137 vertical pressure levels"
    ),
    WEATHER_NEXT_2(
        displayName = "WeatherNext 2",
        color = WeatherNext2Track,
        shortName = "WN-2",
        provider = "Atmospheric Neural Systems",
        resolution = "0.25° (~25km)",
        description = "Deep autoregressive graph neural network trained on 40-year ERA5 reanalysis"
    ),
    WEATHER_NEXT_3(
        displayName = "WeatherNext 3",
        color = WeatherNext3Track,
        shortName = "WN-3",
        provider = "Atmospheric Neural Systems",
        resolution = "0.1° (~10km)",
        description = "Diffusion-enhanced cyclone core stabilizer with sub-grid convection mechanics"
    ),
    NOAA_GFS(
        displayName = "NOAA GFS",
        color = Color(0xFF00E676),
        shortName = "GFS",
        provider = "National Oceanic and Atmospheric Admin",
        resolution = "13 km",
        description = "Global Forecast System operational spectral dynamical model"
    ),
    JMA_GSM(
        displayName = "JMA Global",
        color = Color(0xFFFFD600),
        shortName = "JMA",
        provider = "Japan Meteorological Agency",
        resolution = "10 km",
        description = "Asia-Pacific specialized typhoon track and cyclogenesis predictor"
    )
}

data class Cyclone(
    val id: String,
    val name: String,
    val basin: String,
    val category: CycloneCategory,
    val currentLocation: GeoPoint,
    val sustainedWindKts: Int,
    val gustKts: Int,
    val centralPressureHpa: Int,
    val movementHeadingDeg: Int,
    val movementSpeedKts: Int,
    val eyeRadiusKm: Int,
    val galeRadiusKm: Int,
    val relativeHumidityPct: Int,
    val seaSurfaceTempC: Float,
    val statusText: String,
    val historicalPath: List<GeoPoint>,
    val ecmwfTrack: List<ForecastTrackPoint>,
    val weatherNext2Track: List<ForecastTrackPoint>,
    val weatherNext3Track: List<ForecastTrackPoint>,
    val gfsTrack: List<ForecastTrackPoint> = emptyList(),
    val jmaTrack: List<ForecastTrackPoint> = emptyList(),
    val stabilizedTrack: List<ForecastTrackPoint>,
    val stabilizationConfidencePct: Float,
    val modelSpreadKm: Int
)

data class ServerNode(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val organization: String,
    val location: GeoPoint,
    val role: String,
    val latencyMs: Int,
    val computeLoadPct: Int,
    val throughputTbps: Float,
    val activeModels: List<String>,
    val status: String,
    val connectedSatellitesCount: Int
)

data class Satellite(
    val id: String,
    val name: String,
    val agency: String,
    val orbitType: String,
    val location: GeoPoint,
    val altitudeKm: Int,
    val sensorBands: List<String>,
    val scanIntervalMin: Int,
    val coverageRadiusKm: Int,
    val status: String
)

data class AtmosphericProbe(
    val point: GeoPoint,
    val windSpeedKts: Int,
    val windDirectionDeg: Int,
    val humidityPct: Int,
    val pressureHpa: Float,
    val temperatureC: Float,
    val dewPointC: Float,
    val cloudCoverPct: Int,
    val nearestCycloneDistanceKm: Float?,
    val nearestCycloneName: String?,
    val aqi: Int = 38,
    val aqiCategory: String = "Good",
    val pm25: Float = 8.5f,
    val ozonePpb: Int = 34
)

enum class MapLayerType(
    val title: String,
    val iconName: String
) {
    TERRAIN("Terrain", "Terrain"),
    RADAR_SCANNER("Scanner", "Radar"),
    CYCLONES("Cyclones", "Hurricane"),
    WIND_STREAMS("Wind Vectors", "Air"),
    ISOBARS("Pressure Isobars", "Speed"),
    HUMIDITY("Humidity (%)", "WaterDrop"),
    AIR_QUALITY("Air Quality (AQI)", "Masks"),
    JET_STREAMS("Jet Streams", "Air"),
    FRONTAL_SYSTEMS("Frontal Zones", "Thunderstorm"),
    BATHYMETRY("Bathymetry", "Waves"),
    DOPPLER_STATIONS("NEXRAD Radars", "Radar"),
    NAV_CORRIDORS("Nav Corridors", "Navigation"),
    DATA_LINKS("Satellite Lasers", "ElectricBolt"),
    SATELLITES("Satellites", "Satellite"),
    SERVERS("Supercomputers", "Dns"),
    LIGHTNING("Lightning (ENTLN)", "Bolt")
}

enum class MapScanType(
    val label: String,
    val shortLabel: String,
    val description: String
) {
    DOPPLER_RADAR("Doppler Radar", "Radar", "Dual-polarization precipitation & wind shear sweep"),
    SATELLITE_SCAN("Satellite Swath", "Satellite", "Orbital multispectral IR & visible thermal scanning"),
    AI_NEURAL_SCAN("AI Neural Scanner", "AI Scan", "Deep learning storm perception & cyclogenesis grid"),
    COMBINED_SATELLITE_AI("Satellite + AI Fusion", "Sat + AI", "Fused orbital satellite radiance with neural AI inference")
}

enum class ScannedSpotType(
    val label: String,
    val tag: String,
    val color: Color
) {
    CYCLONE_CORE("Cyclone Eyewall", "TYPHOON", Color(0xFFFF1744)),
    STRONG_WIND_CELL("Gale Core", "GALE", Color(0xFFFF9100)),
    HEAVY_RAIN("Precipitation Cell", "RAIN", Color(0xFF00B0FF)),
    SATELLITE_FOOTPRINT("Orbital Swath", "SAT-SCAN", Color(0xFF00E5FF)),
    AI_NEURAL_ANOMALY("AI Anomaly", "NEURAL-AI", Color(0xFFE040FB)),
    CONVECTIVE_CELL("Moisture Cell", "CONVECT", Color(0xFF00E676))
}

data class ScannedSpot(
    val id: String,
    val location: GeoPoint,
    val name: String,
    val type: ScannedSpotType,
    val accuracyPct: Float,
    val windKts: Int,
    val humidityPct: Int,
    val aqi: Int,
    val detectedTick: Long,
    val boxSizePx: Float = 48f,
    val precipitationRateMmH: Float = 0f,
    val reflectivityDbz: Float = 45f,
    val echoTopKft: Int = 38,
    val radarStationCallsign: String = "WSR-88D"
)

/**
 * Standard NWS Doppler Radar Reflectivity Color Scale:
 * - <25 dBZ: Light Blue / Cyan (Light rain / drizzle)
 * - 25-39 dBZ: Green (Moderate stratiform rain)
 * - 40-49 dBZ: Yellow / Amber (Heavy convective rain)
 * - 50-59 dBZ: Red / Crimson (Severe torrential downpour / storm core)
 * - >=60 dBZ: Magenta / Dark Purple (Violent cyclone eyewall / hail core)
 */
fun getReflectivityColor(dbz: Float): Color {
    return when {
        dbz >= 60f -> Color(0xFFD500F9) // Vivid Magenta (Eyewall/Hail)
        dbz >= 50f -> Color(0xFFFF1744) // Bright Red (Torrential Rain)
        dbz >= 40f -> Color(0xFFFFD600) // Deep Yellow (Heavy Convection)
        dbz >= 25f -> Color(0xFF00E676) // Radar Green (Moderate Rain)
        else -> Color(0xFF00B0FF)       // Light Cyan (Light Rain/Mist)
    }
}

fun getReflectivityCategory(dbz: Float): String {
    return when {
        dbz >= 60f -> "EXTREME EYEWALL (HAIL/CORE)"
        dbz >= 50f -> "TORRENTIAL DOWNPOUR"
        dbz >= 40f -> "HEAVY CONVECTIVE RAIN"
        dbz >= 25f -> "MODERATE RAIN CELL"
        else -> "LIGHT PRECIPITATION"
    }
}

data class ModelWeatherReport(
    val model: ForecastModel,
    val windSpeedKts: Int,
    val centralPressureHpa: Int,
    val humidityPct: Int,
    val precipitationMm: Float,
    val trackErrorKm: Int,
    val confidencePct: Float,
    val forecastSummary: String
)

enum class TerrainMapStyle(
    val label: String,
    val detail: String
) {
    SATELLITE_TOPO("Satellite Topo", "Rich topographic vegetation and mountain relief"),
    TACTICAL_RADAR("Tactical Radar", "High-contrast glowing cyan coastlines and dark terrain"),
    NAUTICAL_CHART("Nautical Chart", "Crisp landmasses with oceanic depth contours"),
    MINIMAL_VECTOR("Vector Grid", "Ultra-fast dark vector contours with radar scanlines")
}

enum class StabilizationDisplayMode(
    val label: String,
    val detail: String
) {
    STABILIZED_ONLY("Consensus Track", "Stabilized ensemble filter (ECMWF + WN2/3)"),
    MODEL_COMPARISON("All Model Tracks", "Overlay individual tracks (ECMWF vs WN3 vs WN2 vs GFS vs JMA)")
}

enum class DataFetchOrigin(
    val label: String,
    val shortTag: String,
    val description: String
) {
    NETWORK_LIVE("Live Network", "LIVE NET", "Open-Meteo REST API • Real-time synchronization"),
    ROOM_DATABASE_CACHE("Room DB Cache", "ROOM DB", "Local SQLite Room cache • Offline resilient layer"),
    SYNCHRONIZING("Synchronizing", "SYNCING", "Syncing real-time atmospheric stream to Room DB")
}

enum class SurgeRiskLevel(
    val label: String,
    val color: Color,
    val alertLevel: String
) {
    MODERATE("Moderate Inundation", Color(0xFFFFD600), "ADVISORY"),
    HIGH("High Surge Risk", Color(0xFFFF9100), "WARNING"),
    EXTREME("Extreme Surge Threat", Color(0xFFFF1744), "DANGER"),
    CATASTROPHIC("Catastrophic Surge", Color(0xFFD50000), "EMERGENCY")
}

data class SurgeRiskZone(
    val id: String,
    val coastalRegion: String,
    val oceanBasin: String,
    val location: GeoPoint,
    val peakSurgeMeters: Float,
    val peakSurgeFeet: Float,
    val riskLevel: SurgeRiskLevel,
    val associatedCyclone: String,
    val evacuationStatus: String,
    val bathymetryDepthMeters: Int,
    val highTideTiming: String,
    val inundationRadiusKm: Int
)

data class ProbeResult(
    val probe: AtmosphericProbe,
    val origin: DataFetchOrigin,
    val latencyMs: Long,
    val cachedRecordCount: Int = 0
)

enum class AppScanMode(
    val title: String,
    val shortLabel: String,
    val description: String
) {
    CYCLONES("Typhoon Scanner", "Cyclones", "Active tropical cyclones & hurricanes"),
    STRONG_WINDS("Strong Winds", "Strong Winds", "Gale-force fronts & jet streams"),
    SURGE_RISK("Storm Surge Matrix", "Surge", "Coastal bathymetry inundation, surge risk zones & astronomical tide timing"),
    SATELLITES("Satellite Tracker", "Satellites", "Scans earth-observation & weather satellites in orbit"),
    ALL_MODELS("All Models Weather", "All Models", "Simultaneous multi-model ensemble comparison matrix"),
    SETTINGS("Settings", "Settings", "Accuracy, speed, theme & models")
}

enum class WindSeverity(
    val label: String,
    val color: Color,
    val minWindKts: Int
) {
    STRONG_BREEZE("Near Gale (28-33 kt)", Color(0xFF4FC3F7), 28),
    GALE_FORCE("Gale Force (34-47 kt)", Color(0xFFFFD600), 34),
    STORM_FORCE("Storm Force (48-63 kt)", Color(0xFFFF6D00), 48),
    HURRICANE_FORCE("Violent Gale / Storm (64+ kt)", Color(0xFFFF1744), 64)
}

data class StrongWindArea(
    val id: String,
    val name: String,
    val oceanRegion: String,
    val centerLocation: GeoPoint,
    val sustainedWindKts: Int,
    val gustKts: Int,
    val windDirectionDeg: Int,
    val radiusKm: Int,
    val severity: WindSeverity,
    val humidityPct: Int,
    val pressureHpa: Int,
    val systemDescription: String
)

enum class ScannerBeamStyle(
    val label: String,
    val description: String
) {
    SCANNING_BOX("Volumetric Scanning Box", "3D tactical holographic targeting cube with animated laser raster scan"),
    TACTICAL_LASER("Tactical Laser", "Sharp focused cyan laser beam with target reticle"),
    PHOSPHOR_RADAR("Phosphor Sweep", "Sweeping radar with glowing phosphor afterglow"),
    INFRARED_SPECTRUM("Infrared Convection", "Thermal spectral scan highlighting deep eyewalls"),
    DOPPLER_VELOCITY("Doppler Velocity", "Dual-polarization pulse measuring radial wind velocity shear")
}

enum class MapProjectionType(
    val label: String,
    val shortLabel: String,
    val description: String
) {
    GLOBE_3D("3D Globe", "3D Globe", "Orthographic spherical Earth with orbital atmospheric limb and space void"),
    TACTICAL_2D("Tactical 2D", "2D Map", "Equirectangular cylindrical projection with global overview")
}

enum class ModelWeightPreset(
    val label: String,
    val ecmwfWeight: Float,
    val weatherNext3Weight: Float,
    val weatherNext2Weight: Float,
    val gfsWeight: Float = 0.0f,
    val detail: String
) {
    BALANCED_CONSENSUS("Balanced Consensus", 0.40f, 0.35f, 0.15f, 0.10f, "40% ECMWF • 35% WN-3 • 15% WN-2 • 10% GFS"),
    AI_WEATHERNEXT_CORE("WeatherNext 3 AI Focus", 0.15f, 0.60f, 0.15f, 0.10f, "60% WN-3 Neural Diffusion Core"),
    ECMWF_PRIMARY("ECMWF IFS Heavy", 0.65f, 0.20f, 0.10f, 0.05f, "65% European High-Resolution Physics"),
    CUSTOM("Custom Weights", 0.35f, 0.35f, 0.15f, 0.15f, "User-calibrated multi-model weighting matrix")
}

enum class WindSpeedUnit(
    val label: String,
    val factorFromKts: Float
) {
    KNOTS("kt", 1.0f),
    KM_H("km/h", 1.852f),
    MPH("mph", 1.15078f),
    M_S("m/s", 0.514444f)
}

data class IsobarLine(
    val pressureHpa: Int,
    val points: List<GeoPoint>
)

data class LightningFlash(
    val location: GeoPoint,
    val intensity: Float,
    val createdTick: Long
)

/**
 * Computes forecasted location with exact match or smooth interpolation across forecast hours (including 42h, 48h, 72h).
 */
fun getCycloneForecastLocation(cyclone: Cyclone, forecastHours: Int): GeoPoint {
    if (forecastHours <= 0 || cyclone.stabilizedTrack.isEmpty()) {
        return cyclone.currentLocation
    }
    val exact = cyclone.stabilizedTrack.find { it.hoursAhead == forecastHours }
    if (exact != null) return exact.point

    val track = cyclone.stabilizedTrack.sortedBy { it.hoursAhead }
    val before = track.lastOrNull { it.hoursAhead <= forecastHours } ?: track.first()
    val after = track.firstOrNull { it.hoursAhead >= forecastHours } ?: track.last()

    if (before == after || before.hoursAhead == after.hoursAhead) {
        return before.point
    }
    val ratio = (forecastHours - before.hoursAhead).toDouble() / (after.hoursAhead - before.hoursAhead).toDouble()
    val interpLat = before.point.lat + (after.point.lat - before.point.lat) * ratio
    val interpLon = before.point.lon + (after.point.lon - before.point.lon) * ratio
    return GeoPoint(interpLat, interpLon)
}

data class ForecastIntensity(
    val windKts: Int,
    val pressureHpa: Int
)

/**
 * Computes forecasted sustained wind (kts) and pressure (hPa) for any given forecast hour.
 */
fun getCycloneForecastIntensity(cyclone: Cyclone, forecastHours: Int): ForecastIntensity {
    if (forecastHours <= 0 || cyclone.stabilizedTrack.isEmpty()) {
        return ForecastIntensity(cyclone.sustainedWindKts, cyclone.centralPressureHpa)
    }
    val exact = cyclone.stabilizedTrack.find { it.hoursAhead == forecastHours }
    if (exact != null) return ForecastIntensity(exact.windSpeedKts, exact.pressureHpa)

    val track = cyclone.stabilizedTrack.sortedBy { it.hoursAhead }
    val before = track.lastOrNull { it.hoursAhead <= forecastHours } ?: track.first()
    val after = track.firstOrNull { it.hoursAhead >= forecastHours } ?: track.last()

    if (before == after || before.hoursAhead == after.hoursAhead) {
        return ForecastIntensity(before.windSpeedKts, before.pressureHpa)
    }
    val ratio = (forecastHours - before.hoursAhead).toFloat() / (after.hoursAhead - before.hoursAhead).toFloat()
    val interpWind = (before.windSpeedKts + (after.windSpeedKts - before.windSpeedKts) * ratio).toInt()
    val interpPressure = (before.pressureHpa + (after.pressureHpa - before.pressureHpa) * ratio).toInt()
    return ForecastIntensity(interpWind, interpPressure)
}

/**
 * Maps cyclone intensity (wind speed) to the user's requested color scheme:
 * - Super strong: Dark Purple (>= 115 kts, Category 4 & 5)
 * - Strong: Red (64 to 114 kts, Category 1-3)
 * - Not too strong: Blue (< 64 kts, Tropical Storm / Depression)
 */
fun getCycloneIntensityColor(windKts: Int): Color {
    return when {
        windKts >= 115 -> Color(0xFF6A0DAD) // Dark Purple (Super Strong)
        windKts >= 64 -> Color(0xFFFF1744)  // Red (Strong)
        else -> Color(0xFF00B0FF)           // Blue (Not too strong)
    }
}

/**
 * High-visibility luminous accent/rim for checkpoint indicators and glowing badges on dark maps
 */
fun getCycloneIntensityAccentColor(windKts: Int): Color {
    return when {
        windKts >= 115 -> Color(0xFFE040FB) // Radiant Neon Purple / Magenta
        windKts >= 64 -> Color(0xFFFF5252)  // Coral Red
        else -> Color(0xFF80D8FF)           // Bright Sky Cyan
    }
}

fun getCycloneIntensityCategory(windKts: Int): String {
    return when {
        windKts >= 137 -> "CAT 5 SUPER TYPHOON"
        windKts >= 113 -> "CAT 4 MAJOR CYCLONE"
        windKts >= 96 -> "CAT 3 MAJOR CYCLONE"
        windKts >= 83 -> "CAT 2 TYPHOON"
        windKts >= 64 -> "CAT 1 TYPHOON"
        windKts >= 34 -> "TROPICAL STORM"
        else -> "TROPICAL DEPRESSION"
    }
}

data class CycloneTrackCheckpoint(
    val hoursOffset: Int,
    val timeLabel: String,
    val dateLabel: String,
    val location: GeoPoint,
    val sustainedWindKts: Int,
    val gustKts: Int,
    val pressureHpa: Int,
    val isPast: Boolean = false,
    val isCurrent: Boolean = false
)

fun getCycloneTrackHistoryAndForecast(cyclone: Cyclone): List<CycloneTrackCheckpoint> {
    val list = mutableListOf<CycloneTrackCheckpoint>()

    // 1. Past Historical Recordings
    val hist = cyclone.historicalPath
    val pastOffsets = listOf(-24, -18, -12, -6)
    val pastDates = listOf("Sun, Oct 25 · 18:00 UTC", "Mon, Oct 26 · 00:00 UTC", "Mon, Oct 26 · 06:00 UTC", "Mon, Oct 26 · 12:00 UTC")

    for (i in hist.indices) {
        val offset = pastOffsets.getOrElse(i) { -((hist.size - i) * 6) }
        val date = pastDates.getOrElse(i) { "Past ${kotlin.math.abs(offset)}h" }
        val fraction = (i + 1).toFloat() / (hist.size + 1)
        val pastWind = (cyclone.sustainedWindKts * (0.60f + 0.35f * fraction)).toInt().coerceAtLeast(30)
        val pastPressure = (cyclone.centralPressureHpa + (40 * (1f - fraction))).toInt()
        val pastGust = (pastWind * 1.22f).toInt()

        list.add(
            CycloneTrackCheckpoint(
                hoursOffset = offset,
                timeLabel = "T${offset}H",
                dateLabel = date,
                location = hist[i],
                sustainedWindKts = pastWind,
                gustKts = pastGust,
                pressureHpa = pastPressure,
                isPast = true,
                isCurrent = false
            )
        )
    }

    // 2. Current Observation (NOW / T-0)
    list.add(
        CycloneTrackCheckpoint(
            hoursOffset = 0,
            timeLabel = "NOW (T-0)",
            dateLabel = "Mon, Oct 26 · 18:00 UTC",
            location = cyclone.currentLocation,
            sustainedWindKts = cyclone.sustainedWindKts,
            gustKts = cyclone.gustKts,
            pressureHpa = cyclone.centralPressureHpa,
            isPast = false,
            isCurrent = true
        )
    )

    // 3. Forecast Checkpoints (+12h, +24h, +36h, +42h, +48h, +72h)
    val standardForecastHours = listOf(12, 24, 36, 42, 48, 72)
    val forecastDates = mapOf(
        12 to "Tue, Oct 27 · 06:00 UTC",
        24 to "Tue, Oct 27 · 18:00 UTC",
        36 to "Wed, Oct 28 · 06:00 UTC",
        42 to "Wed, Oct 28 · 12:00 UTC",
        48 to "Wed, Oct 28 · 18:00 UTC",
        72 to "Thu, Oct 29 · 18:00 UTC"
    )

    for (hr in standardForecastHours) {
        val loc = getCycloneForecastLocation(cyclone, hr)
        val intensity = getCycloneForecastIntensity(cyclone, hr)
        list.add(
            CycloneTrackCheckpoint(
                hoursOffset = hr,
                timeLabel = "+${hr}H",
                dateLabel = forecastDates[hr] ?: "+${hr}h Forecast",
                location = loc,
                sustainedWindKts = intensity.windKts,
                gustKts = (intensity.windKts * 1.25f).toInt(),
                pressureHpa = intensity.pressureHpa,
                isPast = false,
                isCurrent = false
            )
        )
    }

    return list
}
