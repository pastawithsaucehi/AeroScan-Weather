package com.example

import android.app.Application
import com.example.data.local.WeatherDatabase

class AeroScanApp : Application() {

    lateinit var database: WeatherDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = WeatherDatabase.getInstance(this)
    }

    companion object {
        lateinit var instance: AeroScanApp
            private set
    }
}
