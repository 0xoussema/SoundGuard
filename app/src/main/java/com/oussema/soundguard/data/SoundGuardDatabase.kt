
package com.oussema.soundguard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NoiseReading::class],
    version = 1,
    exportSchema = false
)
abstract class SoundGuardDatabase : RoomDatabase() {

    abstract fun noiseReadingDao(): NoiseReadingDao

    companion object {
        @Volatile
        private var INSTANCE: SoundGuardDatabase? = null

        fun getDatabase(context: Context): SoundGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoundGuardDatabase::class.java,
                    "soundguard_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
