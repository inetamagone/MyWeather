package com.example.myweather.database

import androidx.room.TypeConverter
import com.example.myweather.network.currentData.*

// To access data subclasses
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
    fun fromMainHumidity(main: Main): String {
        return main.humidity.toString()
    }
    @TypeConverter
    fun fromMainPressure(main: Main): String {
        return main.pressure.toString()
    }
    @TypeConverter
    fun fromMainTemp(main: Main): String {
        return main.temp.toString()
    }
    @TypeConverter
    fun fromMainTempMin(main: Main): String {
        return main.tempMin.toString()
    }
    @TypeConverter
    fun fromMainTempMax(main: Main): String {
        return main.tempMax.toString()
    }
    @TypeConverter
    fun fromSysCountry(sys: Sys): String {
        return sys.country
    }
    @TypeConverter
    fun fromWeatherDescription(weather: Weather): String {
        return weather.description
    }
    @TypeConverter
    fun fromWeatherIcon(weather: Weather): String {
        return weather.icon
    }
    @TypeConverter
    fun fromWindSpeed(wind: Wind): String {
        return wind.speed.toString()
    }


    @TypeConverter
    fun toCoord(lat: String, lon: String): Coord {
        return Coord(lat.toDouble(), lon.toDouble())
    }
    @TypeConverter
    fun toMain(feelsLike: Double, humidity: Int, pressure: Int, temp: Double, tempMax: Double, tempMin: Double): Main {
        return Main(feelsLike.toDouble(), humidity.toInt(), pressure.toInt(), temp.toDouble(), tempMin.toDouble(), tempMax.toDouble())
    }
    @TypeConverter
    fun toSys(country: String): Sys {
        return Sys(country)
    }
    @TypeConverter
    fun toWeather(description: String, icon: String): Weather {
        return Weather(description, icon)
    }
    @TypeConverter
    fun toWind(speed: String): Wind {
        return Wind(speed.toDouble())
    }
}