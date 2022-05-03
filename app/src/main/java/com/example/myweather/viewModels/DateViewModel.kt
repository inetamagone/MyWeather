package com.example.myweather.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.network.dateData.DataList
import com.example.myweather.repository.DateWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DateViewModel : ViewModel() {

    fun getDateWeatherApi(context: Context, lat: String, lon: String) =
        viewModelScope.launch {
            DateWeatherRepository.getDateWeatherApi(context, lat, lon)
        }

    fun getAllByDate(context: Context): LiveData<List<DataList>> =
        DateWeatherRepository.getDbByDate(context)

    fun deleteAllDateList(context: Context) =
        viewModelScope.launch(Dispatchers.IO) {
            DateWeatherRepository.deleteAll(context)
        }
}