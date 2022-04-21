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
import com.example.myweather.network.dateData.DataList
import java.text.SimpleDateFormat
import java.util.*

class DateViewAdapter(
) :
    RecyclerView.Adapter<DateViewAdapter.DateViewHolder>() {

    private val weatherComparatorDifferCallback = object : DiffUtil.ItemCallback<DataList>() {
        override fun areItemsTheSame(oldItem: DataList, newItem: DataList): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DataList, newItem: DataList): Boolean {
            return oldItem.id == newItem.id
        }
    }

    val differ = AsyncListDiffer(this, weatherComparatorDifferCallback)

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

    inner class DateViewHolder(private val binding: View) : RecyclerView.ViewHolder(binding) {
        fun bind(dataList: DataList) {
            val weatherDate = dataList.dt.toLong()
            val dateTextFormatted =
                SimpleDateFormat("d MMM yyyy    HH:mm", Locale.ENGLISH).format(
                    Date(weatherDate * 1000)
                )

            binding.findViewById<TextView>(R.id.date_text).text =
                binding.resources.getString(R.string.second_date, dateTextFormatted)
            binding.findViewById<TextView>(R.id.date_temp).text =
                binding.resources.getString(R.string.date_temp, dataList.main.temp.toString())
            binding.findViewById<TextView>(R.id.date_wind).text =
                binding.resources.getString(R.string.date_wind, dataList.wind.speed.toString())

            // Setting picture icon
            val imageUrl = "https://openweathermap.org/img/wn/${dataList.weather[0].icon}@2x.png"

            Glide.with(binding)
                .load(imageUrl)
                .into(binding.findViewById(R.id.date_icon))
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
