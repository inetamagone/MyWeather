package com.example.myweather.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(val repository: CurrentWeatherRepository) : ViewModel() {

    fun getCurrentWeatherApi(context: Context) =
        viewModelScope.launch {
            repository.getCurrentWeatherApi(context)
        }

    fun searchCurrentWeatherApi(context: Context, searchQuery: String) =
        viewModelScope.launch {
            repository.searchCurrentWeatherApi(context, searchQuery)
        }

    fun getDataFromDb(): LiveData<CurrentWeatherData> =
        repository.getWeatherDataFromDb()

    fun getSearchFromDb(searchQuery: String): LiveData<CurrentWeatherData> =
        repository.getWeatherSearchFromDb(searchQuery)
}