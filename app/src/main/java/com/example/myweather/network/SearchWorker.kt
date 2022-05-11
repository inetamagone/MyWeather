package com.example.myweather.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.myweather.R
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.utils.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "SearchWorker"


class SearchWorker(val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    @SuppressLint("SimpleDateFormat")
    override suspend fun doWork(): Result {
        val city = inputData.getString("QUERY_CITY")
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
        // API request
        apiService.searchCurrentWeather(city!!).enqueue(
            object : Callback<CurrentWeatherData> {
                @SuppressLint("LongLogTag")
                override fun onResponse(
                    call: Call<CurrentWeatherData>,
                    response: Response<CurrentWeatherData>
                ) {
                    if (!response.isSuccessful) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.unsuccessful_network_call),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return
                    }
                    val apiResponseData = response.body()!!
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = CurrentWeatherDatabase.createDatabase(applicationContext).getWeatherDao()
                        dao.insertData(apiResponseData)
                        Log.d(TAG, "Inserted from Worker: $apiResponseData")
                    }
                }

                @SuppressLint("LongLogTag")
                override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
                    Log.d(TAG, t.message ?: context.getString(R.string.null_message))
                }
            })

        // For api call logging
        val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = time.format(Date())
        Log.i(TAG,"Completed $currentDate")

        return Result.success()
    }
}