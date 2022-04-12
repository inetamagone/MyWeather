package com.example.myweather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myweather.FirstFragment
import com.example.myweather.network.currentData.CurrentWeatherData

@Database(
    entities = [CurrentWeatherData::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao
    // Singleton class, visible to other classes
    companion object {
        // Changes can be seen immediately
        @Volatile
        // Recreate the instance of database
        var INSTANCE: WeatherDatabase? = null

        fun createDatabase(context: FirstFragment): WeatherDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.requireContext().applicationContext,
                    WeatherDatabase::class.java,
                    "current_weather"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}