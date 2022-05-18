package com.example.myweather.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.example.myweather.R
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.network.ApiService
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.utils.BASE_URL
import com.example.myweather.viewModels.WeatherViewModel
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

private const val TAG = "CurrentWeatherRepository"

class CurrentWeatherRepository(val database: CurrentWeatherDatabase) {

    private val viewModel: WeatherViewModel? = null

    fun getCurrentWeatherApi(context: Context) {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
        // API request
        apiService.getCurrentWeather().enqueue(
            object : Callback<CurrentWeatherData> {
                @SuppressLint("LongLogTag")
                override fun onResponse(
                    call: Call<CurrentWeatherData>,
                    response: Response<CurrentWeatherData>
                ) {
                    Log.d(TAG, response.toString())
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
                        insertData(apiResponseData)
                        viewModel?.saveState(apiResponseData)
                    }
                }

                @SuppressLint("LongLogTag")
                override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
                    Log.d(TAG, t.message ?: context.getString(R.string.null_message))
                }
            })
    }

    fun searchCurrentWeatherApi(context: Context, searchQuery: String) {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
        // API request
        apiService.searchCurrentWeather(searchQuery).enqueue(
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
                        insertData(apiResponseData)
                    }
                }

                @SuppressLint("LongLogTag")
                override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
                    Log.d(TAG, t.message ?: context.getString(R.string.null_message))
                }
            })
    }

    suspend fun insertData(currentWeatherData: CurrentWeatherData) =
        CoroutineScope(Dispatchers.IO).launch {
            database.getWeatherDao().insertData(currentWeatherData)
        }

    fun getWeatherDataFromDb(): LiveData<CurrentWeatherData> =
        database.getWeatherDao().getWeatherDataFromDb()

    fun getWeatherSearchFromDb(
        searchQuery: String
    ): LiveData<CurrentWeatherData> =
        database.getWeatherDao().getWeatherSearchFromDb(searchQuery)
}
