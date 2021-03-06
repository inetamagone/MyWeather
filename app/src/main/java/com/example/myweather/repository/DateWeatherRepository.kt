package com.example.myweather.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.example.myweather.R
import com.example.myweather.database.dateDatabase.DateWeatherDatabase
import com.example.myweather.network.ApiService
import com.example.myweather.network.dateData.DataList
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
        lateinit var dateWeatherDataList: LiveData<List<DataList>>

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
                        if (!response.isSuccessful) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.unsuccessful_network_call),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return
                        }
                        val list = response.body()!!.list

                        for (element in list) {
                            CoroutineScope(Dispatchers.IO).launch {
                                insertDataByDate(context, element)
                            }
                        }
                    }

                    override fun onFailure(call: Call<DateWeatherData>, t: Throwable) {
                        Log.d(TAG, t.message ?: context.getString(R.string.null_message))
                    }
                })
        }

        suspend fun insertDataByDate(context: Context, dataList: DataList) {
            database = initializeDateDB(context)
            CoroutineScope(Dispatchers.IO).launch {
                database.getDateWeatherDao().insertDataByDate(dataList)
            }
        }

        fun getDbByDate(context: Context): LiveData<List<DataList>> {
            database = initializeDateDB(context)
            dateWeatherDataList = database.getDateWeatherDao().getByDate()
            return dateWeatherDataList
        }

        suspend fun deleteAll(context: Context) {
            database = initializeDateDB(context)
            database.getDateWeatherDao().deleteAll()
        }

        private fun initializeDateDB(context: Context): DateWeatherDatabase =
            DateWeatherDatabase.createDatabase(context)

    }
}