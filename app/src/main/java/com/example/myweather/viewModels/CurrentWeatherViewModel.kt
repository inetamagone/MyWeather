package com.example.myweather.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myweather.model.CurrentWeather

class CurrentWeatherViewModel: ViewModel() {
    var currentList = MutableLiveData<CurrentWeather>()

    fun add(currentWeather: CurrentWeather) {
        currentList.value = currentWeather
    }
}