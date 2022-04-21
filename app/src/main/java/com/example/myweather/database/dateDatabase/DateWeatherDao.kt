package com.example.myweather.database.dateDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myweather.network.dateData.DataList

@Dao
interface DateWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataByDate(dataList: DataList)

    @Query("SELECT * FROM date_weather ORDER BY dt ASC")
    fun getByDate(): LiveData<List<DataList>>

    @Query("DELETE FROM date_weather")
    suspend fun deleteAll()
}