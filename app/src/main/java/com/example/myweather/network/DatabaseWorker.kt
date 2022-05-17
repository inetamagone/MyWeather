package com.example.myweather.network

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DatabaseWorker"

class DatabaseWorker(val context: Context, params: WorkerParameters) :
    Worker(context, params) {

    companion object {
        const val DB_DATA = "db_data"
    }

    override fun doWork(): Result {
        return try {

            val dao = CurrentWeatherDatabase.createDatabase(applicationContext)
                .getWeatherDao()

            val weatherData = dao.getWeatherDataFromDb()

            val name = weatherData.name
            val updatedAt = weatherData.dt.toLong()
            val updatedText = SimpleDateFormat(
                "dd/MM/yyyy  HH:mm",
                Locale.ENGLISH
            ).format(
                Date(updatedAt * 1000)
            )
            val icon = weatherData.weather[0].icon
            val lat = weatherData.coord.lat.toString()
            val lon = weatherData.coord.lon.toString()
            val conditions = weatherData.weather[0].description
            val temperature = weatherData.main.temp.toString()
            val tempMin = weatherData.main.tempMin.toString()
            val tempMax = weatherData.main.tempMax.toString()
            val windData = weatherData.wind.speed.toString()
            val humidityData = weatherData.main.humidity.toString()
            val pressure = weatherData.main.pressure.toString()

            val dataArray = arrayOf(name, updatedText, icon, lat, lon, conditions, temperature, tempMin, tempMax, windData, humidityData, pressure)

            val outputData = Data.Builder()
                .putStringArray(DB_DATA, dataArray)
                .build()
            Log.d(TAG, "Passing back an array: $dataArray")
            return Result.success(outputData)

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Result.failure()
        }
    }
}