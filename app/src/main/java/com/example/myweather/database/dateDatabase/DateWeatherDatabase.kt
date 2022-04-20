package com.example.myweather.database.dateDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myweather.database.currentDatabase.CurrentWeatherDao
import com.example.myweather.network.dateData.DateWeatherData

@Database(
    entities = [DateWeatherData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class DateWeatherDatabase : RoomDatabase() {
    abstract fun getDateWeatherDao(): DateWeatherDao

    companion object {
        @Volatile
        // Recreate the instance of database
        var INSTANCE: DateWeatherDatabase? = null

        fun createDatabase(context: Context): DateWeatherDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DateWeatherDatabase::class.java,
                    "date_weather_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}