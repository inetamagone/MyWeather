package com.example.myweather.database.dateWeather

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.network.dateData.DateWeatherData

@Dao
interface DateWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(dateWeatherData: DateWeatherData): Long

    @Query("SELECT * FROM date_weather")
    fun getAllWeather(): LiveData<List<DateWeatherData>>

    @Delete
    suspend fun delete(dateWeatherData: DateWeatherData)
}