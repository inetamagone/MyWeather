package com.example.myweather.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.myweather.model.CurrentWeather
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentWeatherViewModel : ViewModel() {
    private var currentList: LiveData<CurrentWeatherData>? = null

    init {
        getCurrentWeatherApi()
    }

    private fun getCurrentWeatherApi() {
        viewModelScope.launch {
            CurrentWeatherRepository.getCurrentWeatherApi()
        }
    }

    fun getDataFromDb(context: Context): LiveData<CurrentWeatherData> {
        currentList = CurrentWeatherRepository.getWeatherDataFromDb(context)
        return currentList as LiveData<CurrentWeatherData>
    }
}