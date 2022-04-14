package com.example.myweather.repository

import com.example.myweather.database.dateWeather.DateWeatherDatabase
import com.example.myweather.network.RetrofitInstance
import com.example.myweather.network.dateData.DateWeatherData

class DateWeatherRepository(
    val database: DateWeatherDatabase
) {
    suspend fun getDateWeather(latString: String, lonString: String) =
        RetrofitInstance.api.searchWeatherForecast(latString, lonString)

    suspend fun upsert(dateWeatherData: DateWeatherData) = database.getWeatherDao().upsert(dateWeatherData)

    fun getSavedWeather() = database.getWeatherDao().getAllWeather()

    suspend fun deleteWeather(dateWeatherData: DateWeatherData) = database.getWeatherDao().delete(dateWeatherData)
}