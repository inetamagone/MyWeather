package com.example.myweather.network.currentData

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "current_weather"
)
data class CurrentWeatherData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
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