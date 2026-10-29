package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CachedCycloneEntity::class,
        CachedProbeEntity::class,
        CachedWindAreaEntity::class,
        CachedScannedSpotEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun cycloneCacheDao(): CycloneCacheDao
    abstract fun probeCacheDao(): ProbeCacheDao
    abstract fun windAreaCacheDao(): WindAreaCacheDao
    abstract fun scannedSpotCacheDao(): ScannedSpotCacheDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "aeroscan_weather_cache.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
