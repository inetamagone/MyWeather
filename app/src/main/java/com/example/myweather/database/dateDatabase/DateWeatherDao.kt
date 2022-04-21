package com.example.myweather.database.dateDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myweather.network.dateData.DateWeatherData

@Dao
interface DateWeatherDao {
    // Second Fragment
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataByDate(dateWeatherData: DateWeatherData)

    @Query("SELECT * FROM date_weather ORDER BY id DESC")
    fun getByDate(): LiveData<List<DateWeatherData>>

    @Query("DELETE FROM date_weather")
    suspend fun deleteAll()
}