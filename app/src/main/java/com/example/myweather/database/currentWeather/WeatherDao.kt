package com.example.myweather.database.currentWeather

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myweather.network.currentData.CurrentWeatherData

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(currentWeatherData: CurrentWeatherData): Long

    @Query("SELECT * FROM current_weather")
    fun getAllWeather(): LiveData<List<CurrentWeatherData>>

    @Delete
    suspend fun deleteOneSearch(currentWeatherData: CurrentWeatherData)
}