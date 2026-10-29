package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_wind_areas")
data class CachedWindAreaEntity(
    @PrimaryKey val id: String,
    val name: String,
    val oceanRegion: String,
    val centerLat: Double,
    val centerLon: Double,
    val sustainedWindKts: Int,
    val gustKts: Int,
    val windDirectionDeg: Int,
    val radiusKm: Int,
    val severityName: String,
    val humidityPct: Int,
    val pressureHpa: Int,
    val systemDescription: String,
    val cachedAtTick: Long = System.currentTimeMillis()
)
