package com.example.myweather.viewModels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.viewModels.WeatherViewModel

class CurrentModelFactory(private val currentWeatherRepository: CurrentWeatherRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                return WeatherViewModel(currentWeatherRepository) as T
            }
            throw IllegalArgumentException ("UnknownViewModel")
        }

}