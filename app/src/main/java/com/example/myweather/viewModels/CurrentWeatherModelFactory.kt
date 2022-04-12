package com.example.myweather.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.repository.CurrentWeatherRepository

class CurrentWeatherModelFactory(
    val currentWeatherRepository: CurrentWeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrentWeatherViewModel(currentWeatherRepository) as T
    }
}