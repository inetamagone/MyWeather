package com.example.myweather

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.myweather.utils.API_KEY
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "FirstFragment"

private var city = "Riga"
private var baseUrlFirst =
    "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API_KEY"
    // https://api.openweathermap.org/data/2.5/weather?q=Riga&units=metric&appid=91db09ff13832921fd93739ff0fcc890
private var lat = ""
private var lon = ""

class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        // API call here
        GetWeather().execute()
        // Search function
        val searchIcon = view.findViewById<ImageView>(R.id.search_icon)
        searchIcon.setOnClickListener {
            SearchWeather().execute()
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
            val cityString = view.findViewById<TextView>(R.id.city_name).text.toString()


            navController.navigate(R.id.action_firstFragment_to_secondFragment, Bundle().apply {
                putString("cityName", cityString)
                putString("latString", lat)
                putString("lonString", lon)
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val cityString = view?.findViewById<TextView>(R.id.city_name)?.text
        outState.putCharSequence("Saved city", cityString)
        Log.d(TAG, "Saved city $cityString")
    }
     /* On rotation onCreateView() calls twice, the first time CITY is stored but on the second time is null and displays null */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val storedCity = savedInstanceState?.getCharSequence("Saved city")
        city = storedCity.toString()
        Log.d(TAG, "Restored city $storedCity")
    }

    inner class GetWeather() : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL(baseUrlFirst).readText(
                    Charsets.UTF_8
                )
                Log.d(TAG, "get from URL - City: $city")
                Log.d(TAG, "get from URL Called: $response")
            } catch (e: Exception) {
                Log.d(TAG, "doInBackground Catch Exception: $e")
                response = null
            }
            return response
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

        @SuppressLint("StringFormatMatches", "StringFormatInvalid")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val coord = jsonObj.getJSONObject("coord")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val upDatedAtText =
                    SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val temp = main.getString("temp")
                val tempMin = main.getString("temp_min")
                val tempMax = main.getString("temp_max")
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                val icon = weather.getString("icon")
                val imageUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
                // http://openweathermap.org/img/wn/04d@2x.png

                // For the API call in the SecondFragment
                lat = coord.getString("lat")
                lon = coord.getString("lon")

                /* Populating extracted data into the views */

                requireActivity().findViewById<TextView>(R.id.city_name)?.text = resources.getString(R.string.city_name, address)
                requireActivity().findViewById<TextView>(R.id.updated_time)?.text = resources.getString(R.string.last_updated, upDatedAtText)
                requireActivity().findViewById<TextView>(R.id.conditions)?.text = resources.getString(R.string.conditions, weatherDescription)
                requireActivity().findViewById<TextView>(R.id.temperature)?.text = resources.getString(R.string.current_temp, temp)
                requireActivity().findViewById<TextView>(R.id.temp_min)?.text = resources.getString(R.string.temp_min, tempMin)
                requireActivity().findViewById<TextView>(R.id.temp_max)?.text = resources.getString(R.string.temp_max, tempMax)
                requireActivity().findViewById<TextView>(R.id.pressure)?.text = resources.getString(R.string.current_pressure, pressure)
                requireActivity().findViewById<TextView>(R.id.wind_data)?.text = resources.getString(R.string.current_wind, windSpeed)
                requireActivity().findViewById<TextView>(R.id.humidity_data)?.text = resources.getString(R.string.current_humidity, humidity + " %")

                // Image
                // android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
                val thread = Thread {
                    try {
                        val image = view?.findViewById<ImageView>(R.id.image_main)
                        image?.setImageBitmap(getImageBitmap(imageUrl))
                    } catch (e: Exception) {
                        Log.d(TAG, "Exception on picture thread: $e")
                    }
                }
                thread.start()

                Log.d(TAG, "onPostExecute Called - got JSON and populated views")
            } catch (e: Exception) {
                Log.d(TAG, "Exception: $e")
            }
        }
    }

    inner class SearchWeather() : AsyncTask<String, Void, String>() {

        private var CITY = getCity()
        val BASE_URL_FIRST =
            "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API_KEY"

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL(BASE_URL_FIRST).readText(
                    Charsets.UTF_8
                )
                Log.d(TAG, "get from URL search method: $response")
            } catch (e: Exception) {
                Log.d(TAG, "doInBackground Catch Exception: $e")
                response = null
            }
            return response
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

        @SuppressLint("StringFormatMatches", "StringFormatInvalid")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val coord = jsonObj.getJSONObject("coord")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val upDatedAtText =
                    SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val temp = main.getString("temp")
                val tempMin = main.getString("temp_min")
                val tempMax = main.getString("temp_max")
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                val icon = weather.getString("icon")
                val imageUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
                // http://openweathermap.org/img/wn/04d@2x.png

                // For the API call in the SecondFragment
                lat = coord.getString("lat")
                lon = coord.getString("lon")

                /* Populating extracted data into the views */
                requireActivity().findViewById<TextView>(R.id.city_name)?.text = resources.getString(R.string.city_name, address)
                requireActivity().findViewById<TextView>(R.id.updated_time)?.text = resources.getString(R.string.last_updated, upDatedAtText)
                requireActivity().findViewById<TextView>(R.id.conditions)?.text = resources.getString(R.string.conditions, weatherDescription)
                requireActivity().findViewById<TextView>(R.id.temperature)?.text = resources.getString(R.string.current_temp, temp)
                requireActivity().findViewById<TextView>(R.id.temp_min)?.text = resources.getString(R.string.temp_min, tempMin)
                requireActivity().findViewById<TextView>(R.id.temp_max)?.text = resources.getString(R.string.temp_max, tempMax)
                requireActivity().findViewById<TextView>(R.id.pressure)?.text = resources.getString(R.string.current_pressure, pressure)
                requireActivity().findViewById<TextView>(R.id.wind_data)?.text = resources.getString(R.string.current_wind, windSpeed)
                requireActivity().findViewById<TextView>(R.id.humidity_data)?.text = resources.getString(R.string.current_humidity, humidity + " %")

                // Image
                // android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
                val thread = Thread {
                    try {
                        val image = view?.findViewById<ImageView>(R.id.image_main)
                        image?.setImageBitmap(getImageBitmap(imageUrl))
                    } catch (e: Exception) {
                        Log.d(TAG, "Exception on picture thread: $e")
                    }
                }
                thread.start()

                Log.d(TAG, "onPostExecute Called - got JSON and populated views")
            } catch (e: Exception) {
                Log.d(TAG, "Exception: $e")
            }
        }
    }

    fun getCity(): String {
        val editCity = view?.findViewById<TextInputEditText>(R.id.edit_city)
        if (editCity?.text.isNullOrEmpty()) {
            city = "Riga"
        } else {
            city = editCity?.text.toString()
            Log.v(TAG, "Change cityName: $city")
            editCity?.setText("")
        }
        return city
    }
}