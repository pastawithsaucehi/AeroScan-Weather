package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_scanned_spots")
data class CachedScannedSpotEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val typeName: String,
    val accuracyPct: Float,
    val windKts: Int,
    val humidityPct: Int,
    val aqi: Int,
    val detectedTick: Long
)
