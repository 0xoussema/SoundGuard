
package com.oussema.soundguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "noise_readings")
data class NoiseReading(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val decibelLevel: Float,

    val timestamp: Long = System.currentTimeMillis()
)
