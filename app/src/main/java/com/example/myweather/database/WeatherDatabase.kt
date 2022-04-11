package com.example.myweather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myweather.network.currentData.CurrentWeatherData

@Database(
    entities = [CurrentWeatherData::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        // Changes can be seen immediately
        @Volatile
        // Recreate the instance of database
        private var instance: WeatherDatabase? = null
        // Synchronize setting the instance
        private val lock = Any()
        // There is no other thread that sets the same instance. Verify if null and set the instance to the result of function createDatabase
        operator fun invoke(context: Context) = instance ?: synchronized(lock) {
            instance ?: createDatabase(context).also { instance = it }
        }
        // Instance of db class will be used to access WeatherDao and its functions

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                "currentWeather_db.db"
            ).build()
    }
}