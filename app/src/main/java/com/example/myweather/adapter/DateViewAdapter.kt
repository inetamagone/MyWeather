package com.example.myweather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.network.dateData.DateWeatherData
import java.text.SimpleDateFormat
import java.util.*

class DateViewAdapter : RecyclerView.Adapter<DateViewAdapter.DateViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<DateWeatherData>() {
        override fun areItemsTheSame(oldItem: DateWeatherData, newItem: DateWeatherData): Boolean {
            return oldItem.list[0].dt == newItem.list[0].dt
        }

        override fun areContentsTheSame(oldItem: DateWeatherData, newItem: DateWeatherData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DateViewAdapter.DateViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return DateViewHolder(root)
    }

    override fun onBindViewHolder(holder: DateViewAdapter.DateViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class DateViewHolder(private val binding: View) : RecyclerView.ViewHolder(binding) {
        fun bind(dateWeatherData: DateWeatherData) {

            val dateText = dateWeatherData.list[0].dt.toLong()
            val dateTextFormatted =
                SimpleDateFormat("d MMM yyyy    HH:mm", Locale.ENGLISH).format(
                    Date(dateText * 1000)
                )

            binding.findViewById<TextView>(R.id.date_text).text =
                binding.resources.getString(R.string.second_date, dateTextFormatted + "h")
            binding.findViewById<TextView>(R.id.date_temp).text =
                binding.resources.getString(R.string.date_temp, dateWeatherData.list[0].main.temp.toString())
            binding.findViewById<TextView>(R.id.date_wind).text =
                binding.resources.getString(R.string.date_wind, dateWeatherData.list[0].wind.speed.toString())

            // Setting picture icon
            val imageUrl = "https://openweathermap.org/img/wn/${dateWeatherData.list[0].weather[0].icon}@2x.png"

            Glide.with(binding)
                .load(imageUrl)
                .into(binding.findViewById(R.id.date_icon))
        }
    }
}
