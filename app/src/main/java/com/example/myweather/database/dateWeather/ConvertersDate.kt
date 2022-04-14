package com.example.myweather.database.dateWeather

import androidx.room.TypeConverter
import com.example.myweather.network.currentData.Weather
import com.example.myweather.network.dateData.DataList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class ConvertersDate {
    @TypeConverter
    fun stringToDataList(data: String?): List<DataList?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<DataList?>?>() {}.type
        val gson = Gson()
        return gson.fromJson<List<DataList?>>(data, listType)
    }
    @TypeConverter
    fun dataListToString(objects: List<DataList?>?): String? {
        val gson = Gson()
        return gson.toJson(objects)
    }

    @TypeConverter
    fun stringToWeather(data: String?): List<Weather?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<Weather?>?>() {}.type
        val gson = Gson()
        return gson.fromJson<List<Weather?>>(data, listType)
    }
    @TypeConverter
    fun WeatherToString(objects: List<Weather?>?): String? {
        val gson = Gson()
        return gson.toJson(objects)
    }
}