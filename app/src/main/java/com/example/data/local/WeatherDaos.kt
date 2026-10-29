package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CycloneCacheDao {
    @Query("SELECT * FROM cached_cyclones ORDER BY sustainedWindKts DESC")
    fun getAllCyclonesFlow(): Flow<List<CachedCycloneEntity>>

    @Query("SELECT * FROM cached_cyclones ORDER BY sustainedWindKts DESC")
    suspend fun getAllCyclones(): List<CachedCycloneEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cyclones: List<CachedCycloneEntity>)

    @Query("DELETE FROM cached_cyclones WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM cached_cyclones")
    suspend fun clearAll()
}

@Dao
interface ProbeCacheDao {
    @Query("SELECT * FROM cached_probes ORDER BY cachedAtTick DESC LIMIT 50")
    fun getRecentProbesFlow(): Flow<List<CachedProbeEntity>>

    @Query("SELECT * FROM cached_probes ORDER BY cachedAtTick DESC")
    suspend fun getAllProbes(): List<CachedProbeEntity>

    @Query("SELECT * FROM cached_probes WHERE id = :id LIMIT 1")
    suspend fun getProbeById(id: String): CachedProbeEntity?

    @Query("SELECT COUNT(*) FROM cached_probes")
    suspend fun getProbeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProbe(probe: CachedProbeEntity)

    @Query("DELETE FROM cached_probes WHERE cachedAtTick < :cutoffTick")
    suspend fun purgeOlderThan(cutoffTick: Long)
}

@Dao
interface WindAreaCacheDao {
    @Query("SELECT * FROM cached_wind_areas ORDER BY sustainedWindKts DESC")
    fun getAllWindAreasFlow(): Flow<List<CachedWindAreaEntity>>

    @Query("SELECT * FROM cached_wind_areas ORDER BY sustainedWindKts DESC")
    suspend fun getAllWindAreas(): List<CachedWindAreaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(areas: List<CachedWindAreaEntity>)

    @Query("DELETE FROM cached_wind_areas")
    suspend fun clearAll()
}

@Dao
interface ScannedSpotCacheDao {
    @Query("SELECT * FROM cached_scanned_spots ORDER BY detectedTick DESC LIMIT 30")
    fun getRecentSpotsFlow(): Flow<List<CachedScannedSpotEntity>>

    @Query("SELECT * FROM cached_scanned_spots ORDER BY detectedTick DESC LIMIT 30")
    suspend fun getRecentSpots(): List<CachedScannedSpotEntity>

    @Query("SELECT COUNT(*) FROM cached_scanned_spots")
    suspend fun getSpotCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpot(spot: CachedScannedSpotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(spots: List<CachedScannedSpotEntity>)

    @Query("DELETE FROM cached_scanned_spots WHERE detectedTick < :cutoffTick")
    suspend fun clearOlderThan(cutoffTick: Long)
}
