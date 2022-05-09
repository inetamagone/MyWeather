package com.example.myweather.viewModels

import android.content.Context
import androidx.lifecycle.*
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(val repository: CurrentWeatherRepository, state: SavedStateHandle) : ViewModel() {

    private var currentWeatherData: CurrentWeatherData? = null
    val savedStateData = state.getLiveData("liveData", currentWeatherData)

    fun saveState(currentWeatherData: CurrentWeatherData) : MutableLiveData<CurrentWeatherData?>{
        savedStateData.value = currentWeatherData
        return savedStateData
    }

    // Called from WeatherWorker
    fun getCurrentWeatherApi(context: Context) =
        viewModelScope.launch {
            repository.getCurrentWeatherApi(context)
        }

    fun searchCurrentWeatherApi(context: Context, searchQuery: String) =
        viewModelScope.launch {
            repository.searchCurrentWeatherApi(context, searchQuery)
        }

    fun getDataFromDb(): LiveData<CurrentWeatherData> {
        return repository.getWeatherDataFromDb()
    }

    fun getSearchFromDb(searchQuery: String): LiveData<CurrentWeatherData> =
        repository.getWeatherSearchFromDb(searchQuery)
}