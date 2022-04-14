package com.example.myweather.repository

import com.example.myweather.database.currentWeather.WeatherDatabase
import com.example.myweather.network.RetrofitInstance
import com.example.myweather.network.currentData.CurrentWeatherData

class CurrentWeatherRepository(
    val database: WeatherDatabase
) {
    suspend fun getCurrentWeather() =
        RetrofitInstance.api.getCurrentWeather()

    fun searchCurrentWeather(searchQuery: String) =
        RetrofitInstance.api.searchCurrentWeather(searchQuery)

    suspend fun upsert(currentWeatherData: CurrentWeatherData) = database.getWeatherDao().upsert(currentWeatherData)

    fun getSavedWeather() = database.getWeatherDao().getAllWeather()

    suspend fun deleteWeather(currentWeatherData: CurrentWeatherData) = database.getWeatherDao().deleteOneSearch(currentWeatherData)
}