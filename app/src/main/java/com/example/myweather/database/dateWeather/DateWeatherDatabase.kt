package com.example.myweather.database.dateWeather

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myweather.SecondFragment
import com.example.myweather.network.dateData.DateWeatherData

@Database(
    entities = [DateWeatherData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ConvertersDate::class)
abstract class DateWeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDao(): DateWeatherDao

    companion object {
        @Volatile
        var INSTANCE: DateWeatherDatabase? = null

        fun createDatabase(context: SecondFragment): DateWeatherDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.requireContext(),
                    DateWeatherDatabase::class.java,
                    "date_weather"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}