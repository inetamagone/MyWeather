package com.example.myweather.network

import com.example.myweather.network.data.CurrentWeatherData
import com.example.myweather.utils.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

private const val TAG = "ApiService"
//const val LAT = "57"
//const val LON = "24.0833"
//private var baseUrlFirst =
//    "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API_Key"

// https://api.openweathermap.org/data/2.5/weather?q=Riga&units=metric&appid=91db09ff13832921fd93739ff0fcc890

interface ApiService {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q")
        cityName: String = "Riga",
        @Query("units")
        unitName: String = "metric",
        @Query("appid")
        apiKey: String = API_KEY
    ): Call<CurrentWeatherData>
}