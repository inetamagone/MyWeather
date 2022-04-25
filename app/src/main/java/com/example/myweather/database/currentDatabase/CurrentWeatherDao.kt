package com.example.myweather.database.currentDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myweather.network.currentData.CurrentWeatherData

@Dao
interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(currentWeatherData: CurrentWeatherData)

    @Query("SELECT * FROM current_weather WHERE dt = (SELECT MAX(dt) FROM current_weather)")
    fun getWeatherDataFromDb(): LiveData<CurrentWeatherData>

    @Query("SELECT * FROM current_weather WHERE name = :searchQuery")
    fun getWeatherSearchFromDb(searchQuery: String): LiveData<CurrentWeatherData>

    // History Fragment
    @Query("SELECT * FROM current_weather ORDER BY dt DESC")
    fun getHistory(): LiveData<List<CurrentWeatherData>>

    @Query("DELETE FROM current_weather")
    suspend fun deleteAllHistory()

    @Delete
    suspend fun deleteEntry(currentWeatherData: CurrentWeatherData)
}