package com.example.myweather.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.network.dateData.DateWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.repository.DateWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    lateinit var currentList: LiveData<CurrentWeatherData>
    lateinit var weatherList: LiveData<List<CurrentWeatherData>>
    lateinit var dateWeatherList: LiveData<List<DateWeatherData>>


    fun getCurrentWeatherApi(context: Context) {
        viewModelScope.launch {
            CurrentWeatherRepository.getCurrentWeatherApi(context)
        }
    }

    fun searchCurrentWeatherApi(context: Context, searchQuery: String) {
        viewModelScope.launch {
            CurrentWeatherRepository.searchCurrentWeatherApi(context, searchQuery)
        }
    }

    fun getDataFromDb(context: Context): LiveData<CurrentWeatherData> {
        currentList = CurrentWeatherRepository.getWeatherDataFromDb(context)
        Log.d("ViewModel",  "Data got back from db: $currentList")
        return currentList
    }

    fun getSearchFromDb(context: Context, searchQuery: String): LiveData<CurrentWeatherData> {
        currentList = CurrentWeatherRepository.getWeatherSearchFromDb(context, searchQuery)
        Log.d("ViewModel",  "Data got back from db: $currentList")
        return currentList
    }

    // History Fragment
    fun getAllHistory(context: Context): LiveData<List<CurrentWeatherData>> {
        weatherList = CurrentWeatherRepository.getHistory(context)
        return weatherList
    }

    fun deleteAllHistory(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            CurrentWeatherRepository.deleteAllHistory(context)
        }
    }

    // Second Fragment
    fun getDateWeatherApi(context: Context, lat: String, lon: String) {
        viewModelScope.launch {
            DateWeatherRepository.getDateWeatherApi(context, lat, lon)
        }
    }
    fun getAllByDate(context: Context): LiveData<List<DateWeatherData>> {
        dateWeatherList = DateWeatherRepository.getDbByDate(context)
        return dateWeatherList
    }
}