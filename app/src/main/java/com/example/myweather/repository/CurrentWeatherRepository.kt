package com.example.myweather.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.network.ApiService
import com.example.myweather.network.RetrofitInstance
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

class CurrentWeatherRepository {
    companion object {

        lateinit var database: CurrentWeatherDatabase
        lateinit var weatherDataFromDb: LiveData<CurrentWeatherData>

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
                        override fun onResponse(
                            call: Call<CurrentWeatherData>,
                            response: Response<CurrentWeatherData>
                        ) {
                            Log.d("Repository", response.toString())
                            if (!response.isSuccessful) {
                                Log.d("Repository", "Unsuccessful network call")
                                return
                            }
                            val apiResponseData = response.body()!!
                            CoroutineScope(Dispatchers.IO).launch {
                                database.getWeatherDao().insertData(apiResponseData)
                            }
                        }
                        override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
                            Log.d("Repository", t.message ?: "Null message")
                        }
                    })
            }


        fun searchCurrentWeatherApi(searchQuery: String) {
            RetrofitInstance.api.searchCurrentWeather(searchQuery)
        }

        suspend fun insertData(context: Context, currentWeatherData: CurrentWeatherData) {
            database = initializeDB(context)
            CoroutineScope(Dispatchers.IO).launch {
                database.getWeatherDao().insertData(currentWeatherData)
            }
        }

        fun getWeatherDataFromDb(context: Context): LiveData<CurrentWeatherData> {
            database = initializeDB(context)
            weatherDataFromDb = database.getWeatherDao().getWeatherDataFromDb()
            return weatherDataFromDb
        }

        private fun initializeDB(context: Context): CurrentWeatherDatabase {
            return CurrentWeatherDatabase.createDatabase(context)
        }
    }
}