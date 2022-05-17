package com.example.myweather.viewModels.factories

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.viewModels.WeatherViewModel

class CurrentModelFactory(
    owner: SavedStateRegistryOwner,
    private val currentWeatherRepository: CurrentWeatherRepository,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T = WeatherViewModel(currentWeatherRepository, SavedStateHandle()) as T
}