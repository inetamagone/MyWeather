package com.example.myweather.network.currentData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "currentWeather"
)
data class CurrentWeatherData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val coord: Coord,
    val dt: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)