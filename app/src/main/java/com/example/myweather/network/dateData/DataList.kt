package com.example.myweather.network.dateData

import androidx.room.Embedded
import com.squareup.moshi.Json

data class DataList(
    val dt: Int,
    @Embedded
    val main: Main,
    @Json(name = "date_weather")
    val weather: List<Weather>,
    @Embedded
    val wind: Wind
)