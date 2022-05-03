package com.example.myweather.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.network.ApiService
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

private const val TAG = "CurrentWeatherRepository"

class CurrentWeatherRepository(val database: CurrentWeatherDatabase) {

        fun getCurrentWeatherApi() {
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
                            Log.d(TAG, "Unsuccessful network call")
                            return
                        }
                        val apiResponseData = response.body()!!
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.d(TAG, "Unsuccessful network call")
                            insertData(apiResponseData)
                        }
                    }

                    @SuppressLint("LongLogTag")
                    override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
                        Log.d(TAG, t.message ?: "Null message")
                    }
                })
        }

        fun searchCurrentWeatherApi(searchQuery: String) {
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
                            Log.d(TAG, "Unsuccessful network call")
                            return
                        }
                        val apiResponseData = response.body()!!
                        CoroutineScope(Dispatchers.IO).launch {
                            insertData(apiResponseData)
                        }
                    }

                    @SuppressLint("LongLogTag")
                    override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
                        Log.d(TAG, t.message ?: "Null message")
                    }
                })
        }

        @SuppressLint("LongLogTag")
        suspend fun insertData(currentWeatherData: CurrentWeatherData) {
            CoroutineScope(Dispatchers.IO).launch {
                database.getWeatherDao().insertData(currentWeatherData)
                Log.d(TAG, "Data inserted into db: $currentWeatherData")
            }
        }

        @SuppressLint("LongLogTag")
        fun getWeatherDataFromDb(): LiveData<CurrentWeatherData> {
            return database.getWeatherDao().getWeatherDataFromDb()
        }

        @SuppressLint("LongLogTag")
        fun getWeatherSearchFromDb(
            searchQuery: String
        ): LiveData<CurrentWeatherData> {
            return database.getWeatherDao().getWeatherSearchFromDb(searchQuery)
        }
}
