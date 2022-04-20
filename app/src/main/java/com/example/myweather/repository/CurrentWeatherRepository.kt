package com.example.myweather.repository

import android.content.Context
import android.os.AsyncTask
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

private const val TAG = "Repository"

class CurrentWeatherRepository {
    companion object {

        lateinit var database: CurrentWeatherDatabase
        lateinit var weatherDataFromDb: LiveData<CurrentWeatherData>
        lateinit var historyDataList: LiveData<List<CurrentWeatherData>>

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
                            insertData(context, apiResponseData)
                        }
                    }
                    override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
                        Log.d(TAG, t.message ?: "Null message")
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
                    override fun onResponse(
                        call: Call<CurrentWeatherData>,
                        response: Response<CurrentWeatherData>
                    ) {
                        Log.d(TAG, "City name: $searchQuery")
                        Log.d(TAG, response.toString())
                        if (!response.isSuccessful) {
                            Log.d(TAG, "Unsuccessful network call")
                            return
                        }
                        val apiResponseData = response.body()!!

                        CoroutineScope(Dispatchers.IO).launch {
                            insertData(context, apiResponseData)
                        }
                    }
                    override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
                        Log.d(TAG, t.message ?: "Null message")
                    }
                })
        }

        suspend fun insertData(context: Context, currentWeatherData: CurrentWeatherData) {
            database = initializeDB(context)
            CoroutineScope(Dispatchers.IO).launch {
                database.getWeatherDao().insertData(currentWeatherData)
                Log.d(TAG,  "Data inserted into db: $currentWeatherData")
            }
        }

        fun getWeatherDataFromDb(context: Context): LiveData<CurrentWeatherData> {
            database = initializeDB(context)
            weatherDataFromDb = database.getWeatherDao().getWeatherDataFromDb()
            Log.d(TAG,  "Data got back from db: $weatherDataFromDb")
            return weatherDataFromDb
        }

        fun getWeatherSearchFromDb(context: Context, searchQuery: String): LiveData<CurrentWeatherData> {
            database = initializeDB(context)
            weatherDataFromDb = database.getWeatherDao().getWeatherSearchFromDb(searchQuery)
            Log.d(TAG,  "Data got back from db: $weatherDataFromDb")
            return weatherDataFromDb
        }

        private fun initializeDB(context: Context): CurrentWeatherDatabase {
            return CurrentWeatherDatabase.createDatabase(context)
        }

        // History Fragment
        fun getHistory(context: Context): LiveData<List<CurrentWeatherData>> {
            database = initializeDB(context)

            historyDataList = database.getWeatherDao().getHistory()
            return historyDataList
        }

        suspend fun deleteAllHistory(context: Context){
            database = initializeDB(context)
            database.getWeatherDao().deleteAllHistory()
        }
    }
}