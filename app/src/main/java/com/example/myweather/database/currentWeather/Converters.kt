package com.example.myweather.database.currentWeather

import androidx.room.TypeConverter
import com.example.myweather.network.currentData.*
import com.example.myweather.network.dateData.DataList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

// To access data subclasses for the FirstFragment
class Converters {

    @TypeConverter
    fun stringToListWeather(data: String?): List<Weather?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<Weather?>?>() {}.type
        val gson = Gson()
        return gson.fromJson<List<Weather?>>(data, listType)
    }
    @TypeConverter
    fun listWeatherToString(objects: List<Weather?>?): String? {
        val gson = Gson()
        return gson.toJson(objects)
    }
}