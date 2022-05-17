package com.example.myweather.repository

import androidx.lifecycle.LiveData
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.network.currentData.CurrentWeatherData

class CurrentWeatherRepository(val database: CurrentWeatherDatabase) {

    fun getWeatherDataFromDb(): CurrentWeatherData =
        database.getWeatherDao().getWeatherDataFromDb()

    fun getWeatherSearchFromDb(
        searchQuery: String
    ): LiveData<CurrentWeatherData> =
        database.getWeatherDao().getWeatherSearchFromDb(searchQuery)
}
