package com.example.myweather.viewModels

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.work.*
import com.example.myweather.network.DatabaseWorker
import com.example.myweather.network.SearchWorker
import com.example.myweather.network.WeatherWorker
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import java.util.concurrent.TimeUnit

private const val TAG = "WeatherViewModel"
class WeatherViewModel(val repository: CurrentWeatherRepository, state: SavedStateHandle) : ViewModel() {

    var liveWeatherData: LiveData<CurrentWeatherData>? = null
    var liveSearchData: LiveData<CurrentWeatherData>? = null

    //lateinit var outputApiWorkInfo: LiveData<WorkInfo>
    lateinit var outputWorkInfo: LiveData<WorkInfo>
    private var currentWeatherData: CurrentWeatherData? = null
    var savedStateData = state.getLiveData("live_data", currentWeatherData)

    fun getWeatherApiWithWorker(context: Context) {
        Log.d(TAG, "getWeatherApiWithWorker called")
        val workManager = WorkManager.getInstance(context)

        val periodicWorkRequest = PeriodicWorkRequest
            .Builder(WeatherWorker::class.java, 15, TimeUnit.MINUTES)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val currentRequest = periodicWorkRequest
            .setConstraints(constraints)
            .build()

        workManager
            .enqueueUniquePeriodicWork("unique_work", ExistingPeriodicWorkPolicy.KEEP, currentRequest)


//        outputApiWorkInfo = workManager
//            .getWorkInfoByIdLiveData(currentRequest.id)
//        return outputApiWorkInfo
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getDbDataWithWorker(context: Context): LiveData<WorkInfo> {
        Log.d(TAG, "getDbDataWithWorker called")
        val workManager = WorkManager.getInstance(context)

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DatabaseWorker::class.java)

        val constraints = Constraints.Builder()
            .setTriggerContentMaxDelay(2, TimeUnit.SECONDS)
            .build()

        val databaseRequest = oneTimeWorkRequest
            .setConstraints(constraints)
            .build()

        workManager
            .enqueueUniqueWork("unique_work", ExistingWorkPolicy.KEEP, databaseRequest)

        outputWorkInfo = workManager
            .getWorkInfoByIdLiveData(databaseRequest.id)
        return outputWorkInfo
    }

    fun saveState(currentWeatherData: CurrentWeatherData): MutableLiveData<CurrentWeatherData?> {
        savedStateData.value = currentWeatherData
        return savedStateData
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

//    fun getDataFromDb(): LiveData<CurrentWeatherData>? {
//        liveWeatherData = repository.getWeatherDataFromDb()
//        return liveWeatherData
//    }

    fun getSearchFromDb(searchQuery: String): LiveData<CurrentWeatherData>? {
        liveSearchData = repository.getWeatherSearchFromDb(searchQuery)
        return liveSearchData
    }
}