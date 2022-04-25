package com.example.myweather.database.currentDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myweather.network.currentData.CurrentWeatherData
import java.util.concurrent.Flow

@Dao
interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(currentWeatherData: CurrentWeatherData)

    @Query("SELECT * FROM current_weather WHERE dt = (SELECT MAX(dt) FROM current_weather)")
    fun getWeatherDataFromDb(): LiveData<CurrentWeatherData>

    @Query("SELECT * FROM current_weather WHERE name LIKE :searchQuery")
    fun getWeatherSearchFromDb(searchQuery: String): LiveData<CurrentWeatherData>

    // History Fragment
    @Query("SELECT * FROM current_weather ORDER BY dt DESC")
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

//    @Query("SELECT * FROM current_weather ORDER BY " +
//            "CASE WHEN :sortBy = 1 THEN `temp` END ASC , " +
//            "CASE WHEN :sortBy = 2 THEN `temp` END DESC ")
//    fun filterByTemp(sortBy : Int?): LiveData<List<CurrentWeatherData>>
}