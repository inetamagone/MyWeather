package com.example.myweather.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.*
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

private const val TAG = "WeatherWorker"

class WeatherWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val DATABASE_DATA = "database_data"
    }

    @SuppressLint("SimpleDateFormat")
    override suspend fun doWork(): Result {

        return try {

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
                            val dao = CurrentWeatherDatabase.createDatabase(applicationContext)
                                .getWeatherDao()
                            dao.insertData(apiResponseData)
                            Log.d(TAG, "Inserted from Worker: $apiResponseData")

                            val name = apiResponseData.name
                            val updatedAt = apiResponseData.dt.toLong()
                            val updatedText = SimpleDateFormat(
                                "dd/MM/yyyy  HH:mm",
                                Locale.ENGLISH
                            ).format(
                                Date(updatedAt * 1000)
                            )
                            val icon = apiResponseData.weather[0].icon
                            val lat = apiResponseData.coord.lat.toString()
                            val lon = apiResponseData.coord.lon.toString()
                            val conditions = apiResponseData.weather[0].description
                            val temperature = apiResponseData.main.temp.toString()
                            val tempMin = apiResponseData.main.tempMin.toString()
                            val tempMax = apiResponseData.main.tempMax.toString()
                            val windData = apiResponseData.wind.speed.toString()
                            val humidityData = apiResponseData.main.humidity.toString()
                            val pressure = apiResponseData.main.pressure.toString()

                            val dataArray = arrayOf(name, updatedText, icon, lat, lon, conditions, temperature, tempMin, tempMax, windData, humidityData, pressure)

                            Data.Builder()
                                .putStringArray(DATABASE_DATA, dataArray)
                                .build()
                           }
                    }
                    @SuppressLint("LongLogTag")
                    override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
                        Log.d(
                            TAG, t.message ?: context.getString(R.string.null_message)
                        )
                    }
                })
            return Result.success()

        } catch (throwable: Throwable) {
            Log.e(TAG, "Error in catch")
            throwable.printStackTrace()
            Result.failure()
        }
    }
}