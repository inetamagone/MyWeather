package com.example.myweather.database.currentDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myweather.network.currentData.CurrentWeatherData

@Dao
interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(currentWeatherData: CurrentWeatherData)

    // Select the last entry by id
    @Query("SELECT * FROM current_weather WHERE id = (SELECT MAX(id) FROM current_weather)")
    fun getWeatherDataFromDb(): LiveData<CurrentWeatherData>
}