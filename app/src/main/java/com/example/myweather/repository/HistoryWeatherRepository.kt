package com.example.myweather.repository

import androidx.lifecycle.LiveData
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.network.currentData.CurrentWeatherData

class HistoryWeatherRepository(val database: CurrentWeatherDatabase) {

    fun getHistory(): LiveData<List<CurrentWeatherData>> {
        return database.getWeatherDao().getHistory()
    }

    suspend fun deleteAllHistory() {
        database.getWeatherDao().deleteAllHistory()
    }

    suspend fun deleteEntry(currentWeatherData: CurrentWeatherData) =
        database.getWeatherDao().deleteEntry(currentWeatherData)

    fun filterWeather(sortBy: Int) : LiveData<List<CurrentWeatherData>>{
        return database.getWeatherDao().filterWeather(sortBy)
    }
}

