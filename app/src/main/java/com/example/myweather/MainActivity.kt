package com.example.myweather

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MainActivity"

private val API_Key = "91db09ff13832921fd93739ff0fcc890"
private var CITY = "Riga"
val BASE_URL =
    "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API_Key"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.d(TAG, "onCreate Called")
        supportActionBar?.hide() // Hide statusBar
        setContentView(R.layout.activity_main)
//        gettingWeather().execute()
//
//        val searchIcon = findViewById<ImageView>(R.id.search_icon)
//        searchIcon.setOnClickListener {
//            searchCity().execute()
//            Log.d(TAG, "Button clicked")
//        }
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

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

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
                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                /* Populating extracted data into the views */
                findViewById<TextView>(R.id.city_name).text = address
                findViewById<TextView>(R.id.updated_time).text = updatedAtText
                findViewById<TextView>(R.id.conditions).text = weatherDescription
                findViewById<TextView>(R.id.temperature).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax

                findViewById<TextView>(R.id.wind_data).text = "$windSpeed m/s"
                findViewById<TextView>(R.id.humidity_data).text = "$humidity %"
                findViewById<TextView>(R.id.pressure).text = "$pressure hPa"

                Log.d(TAG, "onPostExecute Called")
            } catch (e: Exception) {
                Log.d(TAG,"Exception: $e")
            }
        }
    }

    inner class searchCity() : AsyncTask<String, Void, String>() {

        private var CITY = getCity()
        val BASE_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API_Key"

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL(BASE_URL).readText(
                    Charsets.UTF_8
                )
                Log.d(TAG, "doInBackground Called")
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        @SuppressLint("StringFormatMatches", "StringFormatInvalid")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

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
                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                /* Populating extracted data into the views */
                findViewById<TextView>(R.id.city_name).text = address
                findViewById<TextView>(R.id.updated_time).text = updatedAtText
                findViewById<TextView>(R.id.conditions).text = weatherDescription
                findViewById<TextView>(R.id.temperature).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax

                findViewById<TextView>(R.id.wind_data).text = "$windSpeed m/s"
                findViewById<TextView>(R.id.humidity_data).text = "$humidity %"
                findViewById<TextView>(R.id.pressure).text = "$pressure hPa"

                Log.d(TAG, "onPostExecute Called")
            } catch (e: Exception) {
                Log.d(TAG,"Exception: $e")
            }
        }
    }

    fun getCity(): String {
        val editCity = findViewById<TextInputEditText>(R.id.edit_city)
        CITY = editCity.text.toString()
        Log.v(TAG, "Change cityName: $CITY")
        editCity.setText("")
        return CITY
    }
}

