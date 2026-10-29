package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_probes")
data class CachedProbeEntity(
    @PrimaryKey val id: String, // e.g. "lat_lon"
    val lat: Double,
    val lon: Double,
    val windSpeedKts: Int,
    val windDirectionDeg: Int,
    val humidityPct: Int,
    val pressureHpa: Float,
    val temperatureC: Float,
    val dewPointC: Float,
    val cloudCoverPct: Int,
    val nearestCycloneDistanceKm: Float?,
    val nearestCycloneName: String?,
    val aqi: Int,
    val aqiCategory: String,
    val pm25: Float,
    val ozonePpb: Int,
    val isOfflineSimulated: Boolean,
    val cachedAtTick: Long = System.currentTimeMillis()
)
