package com.example.myweather.model

import com.example.myweather.network.dateData.DateWeatherData

class DateWeatherResponse(
    val weatherList: List<DateWeatherData>,
    val message: String
)

