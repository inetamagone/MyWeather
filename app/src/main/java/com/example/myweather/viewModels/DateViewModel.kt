package com.example.myweather.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myweather.model.DateWeather

class DateViewModel: ViewModel() {
    var list = MutableLiveData<ArrayList<DateWeather>>()
    var newList = arrayListOf<DateWeather>()

    fun add(dateWeather: DateWeather) {
        newList.add(dateWeather)
        list.value = newList
    }
}