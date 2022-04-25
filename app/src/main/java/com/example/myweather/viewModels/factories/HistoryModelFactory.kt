package com.example.myweather.viewModels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.repository.HistoryWeatherRepository
import com.example.myweather.viewModels.HistoryViewModel


class HistoryModelFactory(private val historyWeatherRepository: HistoryWeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(historyWeatherRepository) as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }

}
