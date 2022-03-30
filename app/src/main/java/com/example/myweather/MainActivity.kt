package com.example.myweather

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val API_Key = "91db09ff13832921fd93739ff0fcc890"
    private val CITY = "Riga"
    val BASE_URL =
        "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API_Key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide statusBar
        setContentView(R.layout.activity_main)
        gettingWeather().execute()

    }

    inner class gettingWeather() : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL(BASE_URL).readText(
                    Charsets.UTF_8
                )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

//        private fun getImageBitmap(url: String): Bitmap? {
//            var bm: Bitmap? = null
//            try {
//                val aURL = URL(url)
//                val conn = aURL.openConnection()
//                conn.connect()
//                val `is` = conn.getInputStream()
//                val bis = BufferedInputStream(`is`)
//                bm = BitmapFactory.decodeStream(bis)
//                bis.close()
//                `is`.close()
//                println("image URL cached")
//            } catch (e: IOException) {
//                println("Error getting bitmap: $e")
//            }
//            return bm
//        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val visibility = jsonObj.getInt("visibility")

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
//                val icon = weather.getString("icon")
//                val imageUrl = "http://openweathermap.org/img/wn/$icon@2x.png"
                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                /* Populating extracted data into the views */
                
//                val image = findViewById<ImageView>(R.id.image_main)
//                image.setImageBitmap(getImageBitmap(imageUrl))

                findViewById<TextView>(R.id.city_name).text = address
                findViewById<TextView>(R.id.updated_time).text = updatedAtText
                findViewById<TextView>(R.id.conditions).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temperature).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax

                findViewById<TextView>(R.id.wind_data).text = "$windSpeed m/s"
                findViewById<TextView>(R.id.humidity_data).text = "$humidity %"
                findViewById<TextView>(R.id.pressure).text = "$pressure hPa"
                findViewById<TextView>(R.id.visibility_data).text = "$visibility m"

            } catch (e: Exception) {
                println("Exception: $e")
            }
        }
    }
}

