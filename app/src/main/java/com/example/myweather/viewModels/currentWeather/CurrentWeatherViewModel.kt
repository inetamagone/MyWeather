package com.example.myweather.viewModels.currentWeather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.model.CurrentWeather
import com.example.myweather.model.WeatherResponse
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class CurrentWeatherViewModel(
    private val currentWeatherRepository: CurrentWeatherRepository
): ViewModel() {

    val currentList: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()
    val searchList: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()

   init {
       getCurrentWeather()
   }

    private fun getCurrentWeather() = viewModelScope.launch {
        currentList.postValue(Resource.Loading())
        val response = currentWeatherRepository.getCurrentWeather()
        currentList.postValue(handleGetWeatherResponse(response))
    }

    fun searchCurrentWeather(searchQuery: String) = viewModelScope.launch {
        searchList.postValue(Resource.Loading())
        val response = currentWeatherRepository.searchCurrentWeather(searchQuery)
        searchList.postValue(handleSearchWeatherResponse(response))
    }

    private fun handleGetWeatherResponse(response: Response<WeatherResponse>) : Resource<WeatherResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchWeatherResponse(response: Response<WeatherResponse>) : Resource<WeatherResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    fun saveWeather(currentWeatherData: CurrentWeatherData) = viewModelScope.launch {
        currentWeatherRepository.upsert(currentWeatherData)
    }

    fun getSavedWeather() = currentWeatherRepository.getSavedWeather()

    fun deleteWeather(currentWeatherData: CurrentWeatherData) = viewModelScope.launch {
        currentWeatherRepository.deleteWeather(currentWeatherData)
    }

    // After getting data from database, displaying in the views
    var currentWeatherFromDb = MutableLiveData<CurrentWeather>()
    fun add(currentWeather: CurrentWeather) {
        currentWeatherFromDb.value = currentWeather
    }
}