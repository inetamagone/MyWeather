package com.example.myweather.network

import com.example.myweather.network.data.CurrentWeatherData
import com.example.myweather.utils.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// https://api.openweathermap.org/data/2.5/weather?q=Riga&units=metric&appid=91db09ff13832921fd93739ff0fcc890

interface ApiService {

    @GET("data/2.5/weather")
    fun searchCurrentWeather(
        @Query("q")
        searchQuery: String,
        @Query("units")
        unitName: String = "metric",
        @Query("appid")
        apiKey: String = API_KEY
    ): Call<CurrentWeatherData>
}