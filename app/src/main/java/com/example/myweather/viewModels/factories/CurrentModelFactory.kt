package com.example.myweather.viewModels.factories

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.viewModels.WeatherViewModel

class CurrentModelFactory(owner: SavedStateRegistryOwner,
                              private val currentWeatherRepository: CurrentWeatherRepository,
                         defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T = WeatherViewModel(currentWeatherRepository, SavedStateHandle()) as T
}
//class CurrentModelFactory(private val currentWeatherRepository: CurrentWeatherRepository): ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            if(modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
//                return WeatherViewModel(currentWeatherRepository) as T
//            }
//            throw IllegalArgumentException ("UnknownViewModel")
//        }
//
//}