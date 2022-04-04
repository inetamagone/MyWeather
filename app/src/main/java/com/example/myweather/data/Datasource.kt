package com.example.myweather.data

import com.example.myweather.*
import com.example.myweather.model.DateWeather

class Datasource {

    fun loadWeatherByDate() : List<DateWeather> {
        return listOf<DateWeather>(
            DateWeather(R.string.weatherDate1, R.string.weatherTemp1, R.string.weatherWind1, R.drawable.sun),
            DateWeather(R.string.weatherDate2, R.string.weatherTemp2, R.string.weatherWind2, R.drawable.wind),
            DateWeather(R.string.weatherDate3, R.string.weatherTemp3, R.string.weatherWind3, R.drawable.humidity),
            DateWeather(R.string.weatherDate4, R.string.weatherTemp4, R.string.weatherWind4, R.drawable.sun),
            DateWeather(R.string.weatherDate5, R.string.weatherTemp5, R.string.weatherWind5, R.drawable.wind),
            DateWeather(R.string.weatherDate1, R.string.weatherTemp1, R.string.weatherWind1, R.drawable.sun),
            DateWeather(R.string.weatherDate2, R.string.weatherTemp2, R.string.weatherWind2, R.drawable.humidity),
            DateWeather(R.string.weatherDate3, R.string.weatherTemp3, R.string.weatherWind3, R.drawable.wind),
            DateWeather(R.string.weatherDate4, R.string.weatherTemp4, R.string.weatherWind4, R.drawable.sun),
            DateWeather(R.string.weatherDate5, R.string.weatherTemp5, R.string.weatherWind5, R.drawable.wind)
        )
    }

}