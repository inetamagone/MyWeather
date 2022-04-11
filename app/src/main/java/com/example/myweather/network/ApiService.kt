package com.example.myweather.network

import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.network.dateData.DateWeatherData
import com.example.myweather.utils.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// FirstFragment: https://api.openweathermap.org/data/2.5/weather?q=Riga&units=metric&appid=91db09ff13832921fd93739ff0fcc890
// SecondFragment: https://api.openweathermap.org/data/2.5/forecast?lat=57&lon=24.0833&units=metric&appid=91db09ff13832921fd93739ff0fcc890

interface ApiService {
    // FirstFragment
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q")
        defaultCity: String = "Riga",
        @Query("units")
        unitName: String = "metric",
        @Query("appid")
        apiKey: String = API_KEY
    ): Call<CurrentWeatherData>

    @GET("data/2.5/weather")
    fun searchCurrentWeather(
        @Query("q")
        searchQuery: String,
        @Query("units")
        unitName: String = "metric",
        @Query("appid")
        apiKey: String = API_KEY
    ): Call<CurrentWeatherData>

    // SecondFragment
    @GET("data/2.5/forecast")
    fun searchWeatherForecast(
        @Query("lat")
        latString: String,
        @Query("lon")
        lonString: String,
        @Query("units")
        unitName: String = "metric",
        @Query("appid")
        apiKey: String = API_KEY
    ): Call<DateWeatherData>
}