package com.example.myweather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.network.currentData.CurrentWeatherData
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewAdapter(private val weatherList: List<CurrentWeatherData>) : RecyclerView.Adapter<HistoryViewAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(root)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(weatherList[position])
    }

    class HistoryViewHolder(private val binding: View) : RecyclerView.ViewHolder(binding) {
        @SuppressLint("SetTextI18n")
        fun bind(currentWeatherData: CurrentWeatherData) {
            val historyCity = currentWeatherData.name
            val historyDate = currentWeatherData.dt.toLong()
            val historyTemp = currentWeatherData.main.temp.toString()
            val historyWind = currentWeatherData.wind.speed.toString()
            val historyIcon = currentWeatherData.weather[0].icon
            val dateTextFormatted =
                SimpleDateFormat("d MMM yyyy    HH:mm", Locale.ENGLISH).format(
                    Date(historyDate * 1000)
                ) + "h"
            binding.findViewById<TextView>(R.id.history_city).text = historyCity
            dateTextFormatted.also { binding.findViewById<TextView>(R.id.history_date).text = it }
            binding.findViewById<TextView>(R.id.history_temp).text = "$historyTemp°C"
            binding.findViewById<TextView>(R.id.history_wind).text = "Wind: $historyWind m/s"

            // Setting picture icon
            val imageUrl = "https://openweathermap.org/img/wn/${historyIcon}@2x.png"

            Glide.with(binding)
                .load(imageUrl)
                .into(binding.findViewById(R.id.history_icon))
        }
    }

    fun getItemByID(id: Int): CurrentWeatherData {
        return weatherList[id]
    }

    override fun getItemCount(): Int {
        return weatherList.size
        //return differ.currentList.size
    }
}