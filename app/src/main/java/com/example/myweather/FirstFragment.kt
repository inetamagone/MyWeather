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
// https://api.openweathermap.org/data/2.5/weather?q=Riga&units=metric&appid=91db09ff13832921fd93739ff0fcc890
private val API_Key = "91db09ff13832921fd93739ff0fcc890"
private var CITY = ""

class FirstFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        // API call here
        getWeather().execute()
        // Search function
        val searchIcon = view.findViewById<ImageView>(R.id.search_icon)
        searchIcon.setOnClickListener {
            getWeather().execute()
            Log.d(TAG, "Search Button clicked")
        }
        Log.d(TAG, "OnCreateView called")
        return view
    }

    // Navigation to the SecondFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)

        view.findViewById<Button>(R.id.button_next).setOnClickListener {
            Log.d(TAG, "Button clicked to navigate to the SecondFragment")
            navController.navigate(R.id.action_firstFragment_to_secondFragment)
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        Log.d(TAG, "Saved Instance State")
//        val cityString = view?.findViewById<TextView>(R.id.city_name)?.text
//        outState.putCharSequence("Saved city", cityString)
//        Log.d(TAG, "Saved city $cityString")
//    }
//     /* On rotation onCreateView() calls twice, the first time CITY is stored but on the second time is null and displays null */
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        Log.d(TAG, "View State Restored")
//        var storedCity = savedInstanceState?.getCharSequence("Saved city")
//        CITY = storedCity.toString()
//        Log.d(TAG, "Restored city $storedCity")
//    }

    inner class getWeather() : AsyncTask<String, Void, String>() {

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
                //val coord = jsonObj.getJSONArray("coord").getJSONObject(0)

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
                // For the API call in the SecondFragment
//                val lat = coord.getString("lat")
//                val lon = coord.getString("lon")

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
        if (editCity?.text.isNullOrEmpty()) {
            CITY = "Riga"
        } else {
            CITY = editCity?.text.toString()
            Log.v(TAG, "Change cityName: $CITY")
            editCity?.setText("")
        }
        return CITY
    }
}