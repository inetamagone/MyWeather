package com.example.myweather.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.viewModels.WeatherViewModel
import kotlinx.coroutines.delay


class SearchDatabaseWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val DB_SEARCH_DATA = "db_data"
    }

    override suspend fun doWork(): Result {
        return try {
            delay(300L)
            val queryCity = inputData.getString(WeatherViewModel.QUERY_CITY)
            val dao = CurrentWeatherDatabase.createDatabase(applicationContext)
                .getWeatherDao()

            val weatherData = queryCity?.let { dao.getWeatherSearchFromDb(it) }

            val name = weatherData?.name
            val updatedAt = weatherData?.dt?.toLong().toString()
            val icon = weatherData?.weather?.get(0)?.icon
            val lat = weatherData?.coord?.lat.toString()
            val lon = weatherData?.coord?.lon.toString()
            val conditions = weatherData?.weather?.get(0)?.description
            val temperature = weatherData?.main?.temp.toString()
            val tempMin = weatherData?.main?.tempMin.toString()
            val tempMax = weatherData?.main?.tempMax.toString()
            val windData = weatherData?.wind?.speed.toString()
            val humidityData = weatherData?.main?.humidity.toString()
            val pressure = weatherData?.main?.pressure.toString()
            val country = weatherData?.sys?.country

            val dataArray = arrayOf(
                name,
                updatedAt,
                icon,
                lat,
                lon,
                conditions,
                temperature,
                tempMin,
                tempMax,
                windData,
                humidityData,
                pressure,
                country
            )
            val outPutData = Data.Builder()
                .putStringArray(DB_SEARCH_DATA, dataArray)
                .build()

            return Result.success(outPutData)

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Result.failure()
        }
    }
}