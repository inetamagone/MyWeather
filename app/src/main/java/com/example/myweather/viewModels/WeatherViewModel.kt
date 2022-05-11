package com.example.myweather.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.example.myweather.network.SearchWorker
import com.example.myweather.network.WeatherWorker
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import java.util.concurrent.TimeUnit

private const val TAG = "WeatherViewModel"
class WeatherViewModel(val repository: CurrentWeatherRepository) : ViewModel() {

    var liveWeatherData: LiveData<CurrentWeatherData>? = null
    var liveSearchData: LiveData<CurrentWeatherData>? = null

    fun getWeatherApiWithWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val periodicWorkRequest = PeriodicWorkRequest
            .Builder(WeatherWorker::class.java, 16, TimeUnit.MINUTES)

        val queryData = Data.Builder()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest
            .setConstraints(constraints)
            .setInputData(queryData.build())
            .build()

        workManager
            .enqueue(periodicWorkRequest.build())
//        workManager
//            .getWorkInfoByIdLiveData(periodicWorkRequest.build().id).observeForever(Observer {
//                val workerData = it.outputData.getStringArray(WeatherWorker.DATABASE_DATA)
//                Log.d(TAG, "workerSearchData: $workerData")
//            })
    }

    fun searchWeatherApiWithWorker(context: Context, cityQuery: String) {
        val workManager = WorkManager.getInstance(context)

        val periodicWorkRequestSearch = PeriodicWorkRequest
            .Builder(SearchWorker::class.java, 16, TimeUnit.MINUTES)

        val queryData = Data.Builder()

        queryData
            .putString("QUERY_CITY", cityQuery)
        Log.d(TAG, cityQuery)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequestSearch
            .setConstraints(constraints)
            .setInputData(queryData.build())
            .build()

        workManager
            .enqueue(periodicWorkRequestSearch.build())
    }

    fun getDataFromDb(): LiveData<CurrentWeatherData>? {
        liveWeatherData = repository.getWeatherDataFromDb()
        return liveWeatherData
    }

    fun getSearchFromDb(searchQuery: String): LiveData<CurrentWeatherData>? {
        liveSearchData = repository.getWeatherSearchFromDb(searchQuery)
        return liveSearchData
    }
}