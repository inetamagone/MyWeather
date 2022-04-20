package com.example.myweather.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.myweather.FirstFragment
import com.example.myweather.model.CurrentWeather
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentWeatherViewModel : ViewModel() {
    lateinit var currentList: LiveData<CurrentWeatherData>
    lateinit var historyList: LiveData<List<CurrentWeatherData>>

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
        historyList = CurrentWeatherRepository.getHistory(context)
        return historyList
    }

    fun deleteAllHistory(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            CurrentWeatherRepository.deleteAllHistory(context)
        }
    }
}