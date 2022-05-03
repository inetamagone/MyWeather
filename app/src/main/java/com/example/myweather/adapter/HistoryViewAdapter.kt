package com.example.myweather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.databinding.HistoryItemBinding
import com.example.myweather.network.currentData.CurrentWeatherData
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewAdapter(val context: Context, private val weatherList: List<CurrentWeatherData>) : RecyclerView.Adapter<HistoryViewAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {
        return HistoryViewHolder(context,
            HistoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(weatherList[position])
    }

    class HistoryViewHolder(val context: Context, private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentWeatherData: CurrentWeatherData) {
            val hDate = currentWeatherData.dt.toLong()
            val hIcon = currentWeatherData.weather[0].icon
            val dateTextFormatted =
                SimpleDateFormat("d MMM yyyy    HH:mm", Locale.ENGLISH).format(
                    Date(hDate * 1000)
                )
            binding.apply {
                historyCity.text = context.getString(R.string.history_city, currentWeatherData.name)
                historyDate.text = context.getString(R.string.history_date, dateTextFormatted)
                historyTemp.text = context.getString(R.string.history_temp, currentWeatherData.main.temp.toString())
                historyWind.text = context.getString(R.string.history_wind, currentWeatherData.wind.speed.toString())
            }
            // Setting picture icon
            Glide.with(binding.root)
                .load("https://openweathermap.org/img/wn/${hIcon}@2x.png")
                .into(binding.historyIcon)
        }
    }

    fun getItemByID(id: Int): CurrentWeatherData {
        return weatherList[id]
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }
}