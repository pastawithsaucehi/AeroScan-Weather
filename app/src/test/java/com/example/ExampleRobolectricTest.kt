package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.WeatherRepository
import com.example.model.AppScanMode
import com.example.viewmodel.StormScanViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AeroScan", appName)
  }

  @Test
  fun `weather repository contains active cyclones, strong wind areas, and servers`() {
    val repo = WeatherRepository()
    assertTrue(repo.cyclones.isNotEmpty())
    assertTrue(repo.strongWindAreas.isNotEmpty())
    assertTrue(repo.servers.isNotEmpty())
    assertTrue(repo.satellites.isNotEmpty())

    val arthur = repo.cyclones.first { it.id == "cyclone_arthur" }
    assertTrue(arthur.ecmwfTrack.isNotEmpty())
    assertTrue(arthur.weatherNext2Track.isNotEmpty())
    assertTrue(arthur.weatherNext3Track.isNotEmpty())
    assertTrue(arthur.stabilizedTrack.isNotEmpty())

    val roaringForties = repo.strongWindAreas.first { it.id == "wind_roaring_forties" }
    assertTrue(roaringForties.sustainedWindKts >= 60)
  }

  @Test
  fun `viewmodel mode and settings state updates correctly`() {
    val vm = StormScanViewModel()
    assertEquals(AppScanMode.CYCLONES, vm.uiState.value.appScanMode)

    vm.setAppScanMode(AppScanMode.STRONG_WINDS)
    assertEquals(AppScanMode.STRONG_WINDS, vm.uiState.value.appScanMode)
    val firstWind = vm.uiState.value.strongWindAreas.first()
    vm.selectWindArea(firstWind)
    assertNotNull(vm.uiState.value.selectedWindArea)

    vm.setAccuracyPct(80)
    assertEquals(80, vm.uiState.value.accuracyPct)

    vm.setScanningSpeed(9.5f)
    assertEquals(9.5f, vm.uiState.value.scanningSpeedSpotsPerSec, 0.01f)

    vm.toggleDarkMode()
    assertEquals(false, vm.uiState.value.isDarkMode)
  }

  @Test
  fun `surge risk zones are populated and can be selected in SURGE_RISK mode`() {
    val repo = WeatherRepository()
    assertTrue(repo.surgeRiskZones.isNotEmpty())
    val tampa = repo.surgeRiskZones.first { it.id == "surge_tampa_bay" }
    assertTrue(tampa.peakSurgeMeters > 4.0f)
    assertEquals("Hurricane Arthur (2026)", tampa.associatedCyclone)

    val vm = StormScanViewModel(repo)
    vm.setAppScanMode(AppScanMode.SURGE_RISK)
    assertEquals(AppScanMode.SURGE_RISK, vm.uiState.value.appScanMode)

    vm.selectSurgeZone(tampa)
    assertEquals(tampa.id, vm.uiState.value.selectedSurgeZone?.id)
    assertEquals(2.2f, vm.uiState.value.viewport.zoom, 0.01f)
  }

  @Test
  fun `force room database cache setting switches data origin`() {
    val repo = WeatherRepository()
    val vm = StormScanViewModel(repo)

    assertEquals(false, vm.uiState.value.forceRoomCacheOnly)
    vm.setForceRoomCacheOnly(true)
    assertEquals(true, vm.uiState.value.forceRoomCacheOnly)

    vm.setShowSurgeVectors(false)
    assertEquals(false, vm.uiState.value.showSurgeVectors)
  }

  @Test
  fun `indonesian archipelago and tactical radar stations are loaded in geodata`() {
    val indonesiaLabels = com.example.data.GeoData.continentLabels.filter { 
      it.text in listOf("INDONESIA", "JAKARTA", "BALI", "SURABAYA", "MAKASSAR", "PAPUA") 
    }
    assertTrue("Should have Indonesian tactical map labels", indonesiaLabels.size >= 5)

    val indonesianStations = com.example.data.GeoData.dopplerStations.filter {
      it.callsign in listOf("WIII", "WADD", "WARR", "WAAA")
    }
    assertEquals(4, indonesianStations.size)

    val indonesiaCorridor = com.example.data.GeoData.navCorridors.find { it.id == "INDONESIA-TRUNK" }
    assertNotNull(indonesiaCorridor)
  }

  @Test
  fun `all cyclones support 48h and 72h prediction tracks and smooth interpolation`() {
    val repo = WeatherRepository()
    for (cyclone in repo.cyclones) {
      val has48 = cyclone.stabilizedTrack.any { it.hoursAhead == 48 }
      val has72 = cyclone.stabilizedTrack.any { it.hoursAhead == 72 }
      assertTrue("Cyclone ${cyclone.name} must have 48h forecast point", has48)
      assertTrue("Cyclone ${cyclone.name} must have 72h forecast point", has72)

      // Test 42h interpolation (user request)
      val loc42 = com.example.model.getCycloneForecastLocation(cyclone, 42)
      assertNotNull(loc42)

      // Test 48h and 72h locations
      val loc48 = com.example.model.getCycloneForecastLocation(cyclone, 48)
      val loc72 = com.example.model.getCycloneForecastLocation(cyclone, 72)
      assertNotNull(loc48)
      assertNotNull(loc72)
      // 72h location should differ from current location
      assertTrue(loc72.lat != cyclone.currentLocation.lat || loc72.lon != cyclone.currentLocation.lon)
    }

    // Verify consensus track computes 72h
    val arthur = repo.cyclones.first { it.id == "cyclone_arthur" }
    val customTrack = repo.computeCustomConsensusTrack(arthur, 0.4f, 0.35f, 0.15f, 0.1f, 95)
    assertTrue(customTrack.any { it.hoursAhead == 48 })
    assertTrue(customTrack.any { it.hoursAhead == 72 })
  }

  @Test
  fun `heavy rain spot type and precipitation telemetry are properly modeled and generated`() {
    val spotType = com.example.model.ScannedSpotType.HEAVY_RAIN
    assertEquals("Precipitation Cell", spotType.label)
    assertEquals("RAIN", spotType.tag)

    val rainSpot = com.example.model.ScannedSpot(
      id = "spot_rain_1",
      location = com.example.model.GeoPoint(24.5, -85.2),
      name = "KAMX PRECIP CELL",
      type = spotType,
      accuracyPct = 96.5f,
      windKts = 45,
      humidityPct = 94,
      aqi = 18,
      detectedTick = System.currentTimeMillis(),
      precipitationRateMmH = 55.4f,
      reflectivityDbz = 54.2f,
      echoTopKft = 46,
      radarStationCallsign = "KAMX"
    )
    assertTrue(rainSpot.precipitationRateMmH > 0f)
    assertEquals(55.4f, rainSpot.precipitationRateMmH, 0.01f)
    assertEquals(54.2f, rainSpot.reflectivityDbz, 0.01f)
    assertEquals("KAMX", rainSpot.radarStationCallsign)
    assertEquals("TORRENTIAL DOWNPOUR", com.example.model.getReflectivityCategory(rainSpot.reflectivityDbz))
  }

  @Test
  fun `version 1_5 and 10cm s-band radar scanning active detection alert`() {
    val repo = WeatherRepository()
    val vm = StormScanViewModel(repo)
    assertTrue(vm.uiState.value.activeDetectionAlert.contains("10cm S-BAND RADAR SCANNER ACTIVE"))
    assertTrue(vm.uiState.value.scanningSpeedSpotsPerSec in 5.0f..15.0f)
  }

  @Test
  fun `gpu vector tiling engine framerate targets and refresh rules`() {
    val vm = StormScanViewModel()
    // Performance locked precisely at 60 FPS by default
    assertEquals(com.example.model.TargetFramerateFps.FPS_60, vm.uiState.value.targetFramerate)
    assertEquals(60, vm.uiState.value.targetFramerate.fps)
    assertEquals(16L, vm.uiState.value.targetFramerate.intervalMs)
    assertEquals(true, vm.uiState.value.gpuVectorTilingEnabled)

    // Dynamic framerate target adjustment via settings
    vm.setTargetFramerate(com.example.model.TargetFramerateFps.FPS_120)
    assertEquals(com.example.model.TargetFramerateFps.FPS_120, vm.uiState.value.targetFramerate)
    assertEquals(8L, vm.uiState.value.targetFramerate.intervalMs)

    vm.setTargetFramerate(com.example.model.TargetFramerateFps.FPS_30)
    assertEquals(33L, vm.uiState.value.targetFramerate.intervalMs)

    vm.toggleGpuVectorTiling()
    assertEquals(false, vm.uiState.value.gpuVectorTilingEnabled)
  }

  @Test
  fun `optical flow neural network analyzes fluid dynamics and cloud-top fluid mechanics`() {
    val repo = WeatherRepository()
    val flow = repo.generateOpticalFlowField(repo.cyclones, 0.25f)
    assertTrue("Optical flow vector field must not be empty", flow.isNotEmpty())

    val first = flow.first()
    assertTrue("Speed must be positive", first.speedKts > 0f)
    assertNotNull(first.location)
    assertTrue(first.cloudTopTempC < 0f) // Sub-zero convective clouds
  }

  @Test
  fun `spatial dbscan clustering groups and isolates high-density radar cores from noise`() {
    val repo = WeatherRepository()
    val testSpots = listOf(
      com.example.model.ScannedSpot(
        id = "spot_core_1",
        location = com.example.model.GeoPoint(-6.0, 106.8),
        name = "Jakarta Core 1",
        type = com.example.model.ScannedSpotType.HEAVY_RAIN,
        accuracyPct = 96f,
        windKts = 40,
        humidityPct = 95,
        aqi = 20,
        detectedTick = System.currentTimeMillis(),
        reflectivityDbz = 52f,
        precipitationRateMmH = 45f
      ),
      com.example.model.ScannedSpot(
        id = "spot_core_2",
        location = com.example.model.GeoPoint(-6.2, 107.0),
        name = "Jakarta Core 2",
        type = com.example.model.ScannedSpotType.HEAVY_RAIN,
        accuracyPct = 97f,
        windKts = 45,
        humidityPct = 96,
        aqi = 18,
        detectedTick = System.currentTimeMillis(),
        reflectivityDbz = 55f,
        precipitationRateMmH = 58f
      ),
      com.example.model.ScannedSpot(
        id = "spot_isolated_noise",
        location = com.example.model.GeoPoint(45.0, -120.0), // Far isolated noise
        name = "Pacific Noise",
        type = com.example.model.ScannedSpotType.CONVECTIVE_CELL,
        accuracyPct = 85f,
        windKts = 15,
        humidityPct = 70,
        aqi = 30,
        detectedTick = System.currentTimeMillis(),
        reflectivityDbz = 25f,
        precipitationRateMmH = 4f
      )
    )

    val clusters = repo.runDbscanClustering(testSpots, epsKm = 300f, minPts = 2)
    assertEquals(1, clusters.size)
    val cluster = clusters.first()
    assertEquals(2, cluster.pointCount)
    assertTrue(cluster.memberIds.contains("spot_core_1"))
    assertTrue(cluster.memberIds.contains("spot_core_2"))
    assertEquals(false, cluster.memberIds.contains("spot_isolated_noise")) // Noise excluded!
    assertTrue(cluster.boundingHull.size >= 8)
  }

  @Test
  fun `bmkg ground-truth validation sub-routine cross-references surface pressure and wind`() {
    val repo = WeatherRepository()
    assertTrue("BMKG moored buoys must be populated", repo.bmkgBuoyStations.isNotEmpty())

    // Target near Java Sea / Sunda Strait
    val targetLoc = com.example.model.GeoPoint(-5.5, 107.5)
    val verification = repo.verifyTargetWithBmkgGroundTruth(
      targetId = "anomaly_sunda",
      targetName = "Sunda Squall Line",
      targetLocation = targetLoc,
      aiPredictedPressureHpa = 1008.0f,
      aiPredictedWindKts = 30
    )

    assertNotNull(verification.nearestStation)
    assertTrue(verification.distanceKm > 0f)
    assertNotNull(verification.status)
    assertTrue(verification.normalizedPressureHpa in 990f..1030f)
    assertTrue(verification.normalizedWindKts > 0)

    val vm = StormScanViewModel(repo)
    vm.triggerBmkgGroundTruthVerification(cyclone = repo.cyclones.first())
    assertNotNull(vm.uiState.value.bmkgBuoys)
  }
}
