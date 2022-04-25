package com.example.myweather.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.HistoryWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "HistoryViewModel"
class HistoryViewModel(private val repository: HistoryWeatherRepository): ViewModel() {

    fun getAllHistory(): LiveData<List<CurrentWeatherData>> {
        return repository.getHistory()
    }

    fun deleteAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllHistory()
        }
    }

    fun deleteEntry(currentWeatherData: CurrentWeatherData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEntry(currentWeatherData)
        }
    }

    fun filterItems(sortBy: Int): LiveData<List<CurrentWeatherData>> {
        return repository.filterWeather(sortBy)
    }
}