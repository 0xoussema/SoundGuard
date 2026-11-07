
package com.oussema.soundguard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoiseReadingDao {

    @Insert
    suspend fun insertReading(reading: NoiseReading)

    @Query("SELECT * FROM noise_readings ORDER BY timestamp DESC")
    fun getAllReadings(): Flow<List<NoiseReading>>

    @Query("DELETE FROM noise_readings")
    suspend fun deleteAllReadings()
}
