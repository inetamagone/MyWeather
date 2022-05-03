package com.example.myweather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.databinding.ListItemBinding
import com.example.myweather.network.dateData.DataList
import java.text.SimpleDateFormat
import java.util.*

class DateViewAdapter(private val context: Context, private val weatherList: List<DataList>
) :
    RecyclerView.Adapter<DateViewAdapter.DateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DateViewAdapter.DateViewHolder {
        return DateViewHolder(ListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: DateViewAdapter.DateViewHolder, position: Int) {
        holder.bind(weatherList[position])
    }

    inner class DateViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataList: DataList) {
            val weatherDate = dataList.dt.toLong()
            val dateTextFormatted =
                SimpleDateFormat("d MMM yyyy    HH:mm", Locale.ENGLISH).format(
                    Date(weatherDate * 1000)
                )
            binding.apply {
                dateText.text = context.getString(R.string.second_date, dateTextFormatted)
                dateTemp.text = context.getString(R.string.date_temp, dataList.main.temp.toString())
                dateWind.text = context.getString(R.string.date_wind, dataList.wind.speed.toString())
            }

            // Setting picture icon
            Glide.with(binding.root)
                .load("https://openweathermap.org/img/wn/${dataList.weather[0].icon}@2x.png")
                .into(binding.dateIcon)
        }
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }
}