package com.example.wakeonlan

import android.app.Application
import androidx.room.Room

class WakeOnLanApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "wakeonlan.db"
        ).build()
    }

    companion object {
        lateinit var instance: WakeOnLanApp
            private set
    }
}
