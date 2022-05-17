package com.example.myweather.network.currentData

import com.squareup.moshi.Json

data class Main(
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    @Json(name = "temp_max")
    val tempMax: Double,
    @Json(name = "temp_min")
    val tempMin: Double
)