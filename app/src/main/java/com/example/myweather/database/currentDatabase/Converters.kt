package com.example.myweather.database.currentDatabase

import androidx.room.TypeConverter
import com.example.myweather.network.currentData.Weather
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

//    @TypeConverter
//    fun fromTimestamp(value: Long?): Date? {
//        return value?.let { Date(it) }
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: Date?): Long? {
//        return date?.time?.toLong()
//    }
}