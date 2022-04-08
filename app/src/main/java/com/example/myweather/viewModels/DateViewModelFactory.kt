package com.example.myweather.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class DateViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DateViewModel::class.java)) {
            return DateViewModel() as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }
}