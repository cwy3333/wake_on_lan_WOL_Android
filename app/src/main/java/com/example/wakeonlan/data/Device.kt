package com.example.wakeonlan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val macAddress: String,
    val ipAddress: String = "255.255.255.255",
    val port: Int = 9,
    val note: String = ""
)
