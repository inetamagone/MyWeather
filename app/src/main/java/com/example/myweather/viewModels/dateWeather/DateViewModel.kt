package com.example.myweather.viewModels.dateWeather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.model.DateWeatherResponse
import com.example.myweather.network.dateData.DateWeatherData
import com.example.myweather.repository.DateWeatherRepository
import com.example.myweather.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class DateViewModel(
    private val dateWeatherRepository: DateWeatherRepository
): ViewModel() {

    val dateList: MutableLiveData<Resource<DateWeatherResponse>> = MutableLiveData()

    // TODO: Here get values lat and lon from the FirstFragment
    init {
        getDateWeather("57", "24.0833")
    }

    private fun getDateWeather(latString: String, lonString: String) = viewModelScope.launch {
        dateList.postValue(Resource.Loading())
        val response = dateWeatherRepository.getDateWeather(latString, lonString)
        dateList.postValue(handleGetWeatherResponse(response))
    }

    private fun handleGetWeatherResponse(response: Response<DateWeatherResponse>) : Resource<DateWeatherResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveWeather(dateWeatherData: DateWeatherData) = viewModelScope.launch {
        dateWeatherRepository.upsert(dateWeatherData)
    }

    fun getSavedWeather() = dateWeatherRepository.getSavedWeather()

    fun deleteWeather(dateWeatherData: DateWeatherData) = viewModelScope.launch {
        dateWeatherRepository.deleteWeather(dateWeatherData)
    }

//    var list = MutableLiveData<ArrayList<DateWeather>>()
//    var newList = arrayListOf<DateWeather>()
//
//    fun add(dateWeather: DateWeather) {
//        newList.add(dateWeather)
//        list.value = newList
//    }
}