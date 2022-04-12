package com.example.myweather.model

import com.example.myweather.network.currentData.CurrentWeatherData

class WeatherResponse(
    val weatherList: List<CurrentWeatherData>,
    val message: String
)