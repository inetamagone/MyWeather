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
        var INSTANCE: CurrentWeatherDatabase? = null
        val converterInstance by lazy { Converters() }

        fun createDatabase(context: Context): CurrentWeatherDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrentWeatherDatabase::class.java,
                    //"current_we" // String
                    //"current_data"// Date
                    "current_datab"// Date?
                ).addTypeConverter(converterInstance)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}