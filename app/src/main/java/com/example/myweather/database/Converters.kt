package com.example.myweather.database

import androidx.room.TypeConverter
import com.example.myweather.network.currentData.Coord
import com.example.myweather.network.currentData.Main
import javax.xml.transform.Source

// To access data subclasses
// TODO: functions for all other subclasses
class Converters {

    @TypeConverter
    fun fromCoordLat(coord: Coord): String {
        return coord.lat.toString()
    }
    @TypeConverter
    fun fromCoordLon(coord: Coord): String {
        return coord.lon.toString()
    }
    @TypeConverter
    fun fromMainFeelsLike(main: Main): String {
        return main.feelsLike.toString()
    }


    @TypeConverter
    fun toCoordLatLon(lat: String, lon: String): Coord {
        return Coord(lat.toDouble(), lon.toDouble())
    }
}