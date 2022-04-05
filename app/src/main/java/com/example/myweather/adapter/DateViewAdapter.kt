package com.example.myweather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.R
import com.example.myweather.SecondFragment
import com.example.myweather.model.DateWeather
import com.example.myweather.viewModels.DateViewModel

class DateViewAdapter(
    private val viewModel: DateViewModel,
    private val context: SecondFragment,
    private val arrayList: ArrayList<DateWeather>
) :
    RecyclerView.Adapter<DateViewAdapter.DateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): DateViewAdapter.DateViewHolder {
        var root = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return DateViewHolder(root)
    }

    override fun onBindViewHolder(holder: DateViewAdapter.DateViewHolder, position: Int) {
        holder.bind(arrayList.get(position))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class DateViewHolder(private val binding: View) : RecyclerView.ViewHolder(binding) {
        fun bind(dateWeather: DateWeather) {
            binding.findViewById<TextView>(R.id.date_text).text = dateWeather.dateText
            binding.findViewById<TextView>(R.id.date_temp).text = dateWeather.temperature
            binding.findViewById<TextView>(R.id.date_wind).text = dateWeather.windSpeed
            //binding.findViewById<ImageView>(R.id.date_icon).setImageResource()
        }
    }
}
