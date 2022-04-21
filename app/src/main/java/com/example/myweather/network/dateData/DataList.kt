package com.example.myweather.network.dateData

import androidx.room.Embedded
import androidx.room.PrimaryKey

data class DataList(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val dt: Int,
    @Embedded
    val main: Main,
    val weather: List<Weather>,
    @Embedded
    val wind: Wind
)