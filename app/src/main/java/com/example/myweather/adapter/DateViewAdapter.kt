package com.example.myweather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.R
import com.example.myweather.SecondFragment
import com.example.myweather.model.DateWeather

class DateViewAdapter(private val context: SecondFragment, private val dataset: List<DateWeather>) :
RecyclerView.Adapter<DateViewAdapter.DateViewHolder>() {

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateView: TextView = view.findViewById(R.id.date_text)
        val tempView: TextView = view.findViewById(R.id.date_temp)
        val windView: TextView = view.findViewById(R.id.date_wind)
        val iconView: ImageView = view.findViewById(R.id.date_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return DateViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val item = dataset[position]
        holder.dateView.text = context.resources.getString(item.dateResourceId)
        holder.tempView.text = context.resources.getString(item.temperatureResourceId)
        holder.windView.text = context.resources.getString(item.windResourceId)
        holder.iconView.setImageResource(item.icon_imgResourceId)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}