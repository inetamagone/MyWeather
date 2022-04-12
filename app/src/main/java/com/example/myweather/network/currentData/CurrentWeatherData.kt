package com.example.myweather.network.currentData

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "current_weather"
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
) : Serializable