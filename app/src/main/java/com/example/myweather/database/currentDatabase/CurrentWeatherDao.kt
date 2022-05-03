package com.example.myweather.database.currentDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myweather.network.currentData.CurrentWeatherData

@Dao
interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(currentWeatherData: CurrentWeatherData)

    @Query("SELECT * FROM current_weather WHERE timestamp = (SELECT MAX(timestamp) FROM current_weather)")
    fun getWeatherDataFromDb(): LiveData<CurrentWeatherData>

    @Query("SELECT * FROM current_weather " +
            "WHERE timestamp = (SELECT MAX(timestamp) FROM current_weather) " +
            " AND (name LIKE :searchQuery)")
    fun getWeatherSearchFromDb(searchQuery: String): LiveData<CurrentWeatherData>

    // History Fragment
    @Query("SELECT * FROM current_weather ORDER BY timestamp DESC")
    fun getHistory(): LiveData<List<CurrentWeatherData>>

    @Query("DELETE FROM current_weather")
    suspend fun deleteAllHistory()

    @Delete
    suspend fun deleteEntry(currentWeatherData: CurrentWeatherData)

    // Filtering
    @Query("SELECT * FROM current_weather ORDER BY " +
            "CASE WHEN :sortBy = 1 THEN name END ASC , " +
            "CASE WHEN :sortBy = 2 THEN name END DESC , " +
            "CASE WHEN :sortBy = 3 THEN `temp` END ASC , " +
            "CASE WHEN :sortBy = 4 THEN `temp` END DESC ")
    fun filterWeather(sortBy : Int?): LiveData<List<CurrentWeatherData>>
}