package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_cyclones")
data class CachedCycloneEntity(
    @PrimaryKey val id: String,
    val name: String,
    val basin: String,
    val currentLat: Double,
    val currentLon: Double,
    val sustainedWindKts: Int,
    val gustKts: Int,
    val centralPressureHpa: Int,
    val movementDirectionDeg: Int,
    val movementSpeedKts: Int,
    val categoryName: String,
    val relativeHumidityPct: Int,
    val galeRadiusKm: Int,
    val eyewallDiameterKm: Int,
    val stabilizationConfidencePct: Float,
    val lastUpdatedTick: Long = System.currentTimeMillis()
)
