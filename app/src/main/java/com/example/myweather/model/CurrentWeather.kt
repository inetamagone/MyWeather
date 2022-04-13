package com.example.myweather.model

// Class used after getting FirstFragment data from db, passing it to the view list in viewModel
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
    val lat: String,
    val lon: String
)

