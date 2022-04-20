package com.example.myweather.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.myweather.database.dateDatabase.DateWeatherDatabase
import com.example.myweather.network.ApiService
import com.example.myweather.network.dateData.DateWeatherData
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

private const val TAG = "DateWeatherRepository"

class DateWeatherRepository {

    companion object {

        lateinit var database: DateWeatherDatabase
        lateinit var dateWeatherDataList: LiveData<List<DateWeatherData>>

        // Second Fragment
        fun getDateWeatherApi(context: Context, lat: String, lon: String) {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory()).build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            val apiService: ApiService = retrofit.create(ApiService::class.java)
            apiService.searchWeatherForecast(lat, lon).enqueue(
                object : Callback<DateWeatherData> {
                    override fun onResponse(
                        call: Call<DateWeatherData>,
                        response: Response<DateWeatherData>
                    ) {
                        Log.d(TAG, response.toString())
                        if (!response.isSuccessful) {
                            Log.d(TAG, "Unsuccessful network call")
                            return
                        }
                        val apiResponse = response.body()!!
                        val list = apiResponse.list
                        // TODO: Correction here
                        for (element in list) {
                            CoroutineScope(Dispatchers.IO).launch {
                                insertDataByDate(context, apiResponse)
                            }
                        }
                    }
                    override fun onFailure(call: Call<DateWeatherData>, t: Throwable) {
                        Log.d(TAG, t.message ?: "Null message")
                    }
                })
        }

        suspend fun insertDataByDate(context: Context, dateWeatherData: DateWeatherData) {
            database = initializeDateDB(context)
            CoroutineScope(Dispatchers.IO).launch {
               database.getDateWeatherDao().insertDataByDate(dateWeatherData)
                Log.d(TAG, "Data inserted into db: $dateWeatherData")
            }
        }

        fun getDbByDate(context: Context): LiveData<List<DateWeatherData>> {
            database = initializeDateDB(context)

           dateWeatherDataList = database.getDateWeatherDao().getByDate()
            return dateWeatherDataList
        }

        private fun initializeDateDB(context: Context): DateWeatherDatabase {
            return DateWeatherDatabase.createDatabase(context)
        }
    }
}