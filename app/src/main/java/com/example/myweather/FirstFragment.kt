package com.example.myweather

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "FirstFragment"

private val API_Key = "91db09ff13832921fd93739ff0fcc890"
private var CITY = "Riga"
val BASE_URL =
    "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API_Key"

class FirstFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gettingWeather().execute()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Search function
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        val searchIcon = view.findViewById<ImageView>(R.id.search_icon)
        searchIcon.setOnClickListener {
            searchCity().execute()
            Log.d(TAG, "Search Button clicked")
        }
        return view
    }

    // Navigation to the SecondFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)

        view.findViewById<Button>(R.id.button_next).setOnClickListener {
            navController.navigate(R.id.action_firstFragment_to_secondFragment)
        }
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
                val upDatedAtText =
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
                view?.findViewById<TextView>(R.id.city_name)?.text = address
                view?.findViewById<TextView>(R.id.updated_time)?.text = upDatedAtText
                view?.findViewById<TextView>(R.id.conditions)?.text = weatherDescription
                view?.findViewById<TextView>(R.id.temperature)?.text = temp
                view?.findViewById<TextView>(R.id.temp_min)?.text = tempMin
                view?.findViewById<TextView>(R.id.temp_max)?.text = tempMax
                view?.findViewById<TextView>(R.id.pressure)?.text = pressure
                view?.findViewById<TextView>(R.id.wind_data)?.text = "$windSpeed m/s"
                view?.findViewById<TextView>(R.id.humidity_data)?.text = "$humidity %"
                view?.findViewById<TextView>(R.id.pressure)?.text = "$pressure hPa"
                Log.d(TAG, "onPostExecute Called")
            } catch (e: Exception) {
                Log.d(TAG, "Exception: $e")
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
                val upDatedAtText =
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
                view?.findViewById<TextView>(R.id.city_name)?.text = address
                view?.findViewById<TextView>(R.id.updated_time)?.text = upDatedAtText
                view?.findViewById<TextView>(R.id.conditions)?.text = weatherDescription
                view?.findViewById<TextView>(R.id.temperature)?.text = temp
                view?.findViewById<TextView>(R.id.temp_min)?.text = tempMin
                view?.findViewById<TextView>(R.id.temp_max)?.text = tempMax
                view?.findViewById<TextView>(R.id.pressure)?.text = pressure
                view?.findViewById<TextView>(R.id.wind_data)?.text = "$windSpeed m/s"
                view?.findViewById<TextView>(R.id.humidity_data)?.text = "$humidity %"
                view?.findViewById<TextView>(R.id.pressure)?.text = "$pressure hPa"

                Log.d(TAG, "onPostExecute Called")
            } catch (e: Exception) {
                Log.d(TAG, "Exception: $e")
            }
        }
    }

    fun getCity(): String {
        val editCity = view?.findViewById<TextInputEditText>(R.id.edit_city)
        CITY = editCity?.text.toString()
        Log.v(TAG, "Change cityName: $CITY")
        editCity?.setText("")
        return CITY
    }
}