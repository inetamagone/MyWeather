package com.example.myweather.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.R
import com.example.myweather.SecondFragment
import com.example.myweather.model.DateWeather
import com.example.myweather.viewModels.DateViewModel
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL

private const val TAG = "DateViewAdapter"

class DateViewAdapter (
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

            // Setting picture icon
            val imageUrl = "https://openweathermap.org/img/wn/${dateWeather.iconId}@2x.png"
            val thread = Thread {
                try {
                    binding.findViewById<ImageView>(R.id.date_icon).setImageBitmap(getImageBitmap(imageUrl))
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: $e")
                }
            }
            thread.start()
        }
    }

    // Picture icon
    private fun getImageBitmap(url: String): Bitmap? {
        var bm: Bitmap? = null
        try {
            val aURL = URL(url)
            val conn = aURL.openConnection()
            conn.connect()
            val `is` = conn.getInputStream()
            val bis = BufferedInputStream(`is`)
            bm = BitmapFactory.decodeStream(bis)
            bis.close()
            `is`.close()
        } catch (e: IOException) {
            Log.d(TAG,"Error getting bitmap: $e")
        }
        return bm
    }
}
