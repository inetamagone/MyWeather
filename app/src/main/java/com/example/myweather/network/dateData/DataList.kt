package com.example.myweather.network.dateData

import androidx.room.Embedded

data class DataList(
    val dt: Int,
    @Embedded
    val main: Main,
    val weather: List<Weather>,
    @Embedded
    val wind: Wind
)