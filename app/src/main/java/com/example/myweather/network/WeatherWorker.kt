package com.example.myweather.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myweather.viewModels.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "WeatherWorker"
class WeatherWorker(val context: Context, params: WorkerParameters): Worker(context, params) {

    private val viewModel: WeatherViewModel? = null

    @SuppressLint("SimpleDateFormat")
    override fun doWork(): Result {
        viewModel?.getCurrentWeatherApi(context)
        // For api call logging
        val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = time.format(Date())
        Log.i(TAG,"Completed $currentDate")
        return Result.success()
    }
}