package com.example.myweather.network.dateData

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "date_weather"
)
data class DateWeatherData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val list: List<DataList>,
) : Serializable