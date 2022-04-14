package com.example.myweather.viewModels.dateWeather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.repository.DateWeatherRepository
import java.lang.IllegalArgumentException

class DateViewModelFactory(
    val dateWeatherRepository: DateWeatherRepository
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DateViewModel::class.java)) {
            return DateViewModel(dateWeatherRepository) as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }
}