package com.example.myweather.database.currentDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myweather.network.currentData.CurrentWeatherData

@Database(
    entities = [CurrentWeatherData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CurrentWeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDao(): CurrentWeatherDao

    // Singleton class, visible to other classes
    companion object {
        // Changes can be seen immediately
        @Volatile
        // Recreate the instance of database
        private var instance: CurrentWeatherDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CurrentWeatherDatabase::class.java,
                "currentD"
            ).build()
    }
}