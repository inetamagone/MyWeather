package com.example.myweather.network.currentData

import androidx.room.*

@Entity(
    tableName = "current_weather"
)
data class CurrentWeatherData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var timestamp: Long = System.currentTimeMillis()/1000,
    @Embedded
    val coord: Coord,
    val dt: Int,
    @Embedded
    val main: Main,
    val name: String,
    @Embedded
    val sys: Sys,
    val visibility: Int,
    val weather: List<Weather>,
    @Embedded
    val wind: Wind
)