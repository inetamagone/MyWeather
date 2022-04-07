package com.example.myweather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.R
import com.example.myweather.model.DateWeather

class DateViewAdapter (
    private val arrayList: ArrayList<DateWeather>
) :
    RecyclerView.Adapter<DateViewAdapter.DateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DateViewAdapter.DateViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return DateViewHolder(root)
    }

    override fun onBindViewHolder(holder: DateViewAdapter.DateViewHolder, position: Int) {
        holder.bind(arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class DateViewHolder(private val binding: View) : RecyclerView.ViewHolder(binding) {
        fun bind(dateWeather: DateWeather) {
            binding.findViewById<TextView>(R.id.date_text).text = dateWeather.dateText
            binding.findViewById<TextView>(R.id.date_temp).text = dateWeather.temperature
            binding.findViewById<TextView>(R.id.date_wind).text = dateWeather.windSpeed

            // Setting picture icon
            val imageUrl = "https://openweathermap.org/img/wn/${dateWeather.iconId}@2x.png"

            Glide.with(binding)
                .load(imageUrl)
                .into(binding.findViewById<ImageView>(R.id.date_icon))
        }
    }
}
