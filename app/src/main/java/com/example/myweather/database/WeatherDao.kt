package com.example.myweather.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myweather.network.currentData.CurrentWeatherData

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(currentWeather: CurrentWeatherData): Long

    @Query("SELECT * FROM currentWeather")
    fun getSearchHistory(): LiveData<List<CurrentWeatherData>>

    @Delete
    suspend fun deleteOneSearch(currentWeather: CurrentWeatherData)
}