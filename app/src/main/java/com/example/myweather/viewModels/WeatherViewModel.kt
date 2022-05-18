package com.example.myweather.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.example.myweather.network.DatabaseWorker
import com.example.myweather.network.SearchDatabaseWorker
import com.example.myweather.network.SearchWorker
import com.example.myweather.network.WeatherWorker
import com.example.myweather.network.currentData.CurrentWeatherData
import java.util.concurrent.TimeUnit

private const val TAG = "WeatherViewModel"
class WeatherViewModel(state: SavedStateHandle) : ViewModel() {

    companion object {
        const val QUERY_CITY = "query_city"
    }

    lateinit var outputWorkInfo: LiveData<WorkInfo>
    lateinit var outputSearchWorkInfo: LiveData<WorkInfo>

    private var currentWeatherData: CurrentWeatherData? = null
    var savedStateData = state.getLiveData("live_data", currentWeatherData)

    fun getWeatherWithWorker(context: Context): LiveData<WorkInfo> {
        val workManager = WorkManager.getInstance(context)

        val apiWorkRequest = OneTimeWorkRequest
            .Builder(WeatherWorker::class.java)

        val dbWorkRequest = OneTimeWorkRequest
            .Builder(DatabaseWorker::class.java)
            .setInitialDelay(1000, TimeUnit.MILLISECONDS)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val apiRequest = apiWorkRequest
            .setConstraints(constraints)
            .build()

        workManager
            .beginUniqueWork("api_request", ExistingWorkPolicy.KEEP, apiRequest)
            .then(dbWorkRequest)
            .enqueue()

        outputWorkInfo = workManager
            .getWorkInfoByIdLiveData(dbWorkRequest.id)
        return outputWorkInfo
    }

    fun searchWeatherWithWorker(context: Context, cityQuery: String): LiveData<WorkInfo> {
        val workManager = WorkManager.getInstance(context)

        val searchWorkRequest = OneTimeWorkRequest
            .Builder(SearchWorker::class.java)

        val searchDbWorkRequest = OneTimeWorkRequest
            .Builder(SearchDatabaseWorker::class.java)

        val queryData = Data.Builder()

        val dataToSend = queryData
            .putString(QUERY_CITY, cityQuery)
            .build()
        Log.d(TAG, cityQuery)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val searchRequest = searchWorkRequest
            .setConstraints(constraints)
            .setInputData(dataToSend)
            .build()

        val dbSearchRequest = searchDbWorkRequest
            .setInputData(dataToSend)
            .build()

        workManager
            .beginUniqueWork("search_api_request", ExistingWorkPolicy.KEEP, searchRequest)
            .then(dbSearchRequest)
            .enqueue()

        outputSearchWorkInfo = workManager
            .getWorkInfoByIdLiveData(dbSearchRequest.id)
        return outputSearchWorkInfo
    }

    fun saveState(currentWeatherData: CurrentWeatherData): MutableLiveData<CurrentWeatherData?> {
        savedStateData.value = currentWeatherData
        return savedStateData
    }
}