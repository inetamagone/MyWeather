package com.example.myweather.network.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

data class CurrentWeatherData(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)