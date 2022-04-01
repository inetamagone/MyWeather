package com.example.myweather.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class DateWeather (
    @StringRes val dateResourceId: Int,
    @StringRes val temperatureResourceId: Int,
    @StringRes val windResourceId: Int,
    @DrawableRes val icon_imgResourceId: Int
    )
