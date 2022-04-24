package com.example.myweather.network.currentData

import androidx.room.*
import com.example.myweather.database.currentDatabase.Converters
import java.util.*

@Entity(
    tableName = "current_weather"
)
data class CurrentWeatherData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @TypeConverters(Converters::class)
    var current_date: Date?,
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