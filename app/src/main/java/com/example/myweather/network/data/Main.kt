package com.example.myweather.network.data

import com.squareup.moshi.Json

data class Main(
    @Json(name = "feels_like")
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    @Json(name = "temp_max")
    val tempMax: Double,
    @Json(name = "temp_min")
    val tempMin: Double
)