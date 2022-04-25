package com.example.myweather.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import kotlinx.coroutines.launch

private const val TAG = "WeatherViewModel"

class WeatherViewModel(val repository: CurrentWeatherRepository) : ViewModel() {
    lateinit var currentList: LiveData<CurrentWeatherData>
    lateinit var searchList: LiveData<CurrentWeatherData>

    fun getCurrentWeatherApi() {
        viewModelScope.launch {
            repository.getCurrentWeatherApi()
        }
    }

    fun searchCurrentWeatherApi(searchQuery: String) {
        viewModelScope.launch {
            repository.searchCurrentWeatherApi(searchQuery)
        }
    }

    fun getDataFromDb(): LiveData<CurrentWeatherData> {
        currentList = repository.getWeatherDataFromDb()
        Log.d(TAG, "Data got back from db: $currentList")
        return currentList
    }

    fun getSearchFromDb(context: Context, searchQuery: String): LiveData<CurrentWeatherData> {
        searchList = repository.getWeatherSearchFromDb(searchQuery)
        Log.d(TAG, "Data got back from db: $searchList")
        return searchList
    }
}