package com.example.myweather.network.dateData

data class DataList(
    val dt: Int,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)