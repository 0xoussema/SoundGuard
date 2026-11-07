
package com.oussema.soundguard.data

import kotlinx.coroutines.flow.Flow

class NoiseReadingRepository(
    private val dao: NoiseReadingDao
) {

    val allReadings: Flow<List<NoiseReading>> =
        dao.getAllReadings()

    suspend fun insertReading(reading: NoiseReading) {
        dao.insertReading(reading)
    }

    suspend fun deleteAllReadings() {
        dao.deleteAllReadings()
    }
}
