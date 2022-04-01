package com.example.myweather.model

data class CurrentWeather (
    val address: String,
    val valUpdatedAtText: String,
    val temp: String,
    val tempMin: String,
    val tempMax: String,
    val pressure: String,
    val humidity: String,
    val windSpeed: String,
    val weatherDescription: String,
)

