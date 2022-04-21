package com.example.myweather.network.dateData

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "date_weather"
)
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