package com.example.myweather

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.adapter.DateViewAdapter
import com.example.myweather.model.DateWeather
import com.example.myweather.utils.API_KEY
import com.example.myweather.viewModels.DateViewModel
import com.example.myweather.viewModels.DateViewModelFactory
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "SecondFragment"
private var lat: String = ""
private var lon: String = ""

class SecondFragment : Fragment() {
    private var viewManager = LinearLayoutManager(context)
    private lateinit var viewModel: DateViewModel
    private lateinit var dateRecycler: RecyclerView
    private lateinit var arrayList: ArrayList<DateWeather>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        // Getting String values from the FirstFragment
        val cityString = requireArguments().getString("cityName")
        val latString = requireArguments().getString("latString")
        val lonString = requireArguments().getString("lonString")
        // View title text
        view.findViewById<TextView>(R.id.title_text).text =
            resources.getString(R.string.weather_in_city, cityString)

        lat = latString.toString()
        lon = lonString.toString()

        val factory = DateViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(DateViewModel::class.java)
        dateRecycler = view.findViewById(R.id.recycler_view)
        arrayList = viewModel.newList

        initialiseAdapter()
        GetWeatherByDate().execute()
        Log.d(TAG, "OnCreateView Called")
        return view
    }

    private fun initialiseAdapter() {
        dateRecycler.layoutManager = viewManager
        observeData()
    }

    private fun observeData() {
        viewModel.list.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            dateRecycler.adapter = DateViewAdapter(arrayList)
        })
    }

    inner class GetWeatherByDate() : AsyncTask<String, Void, String>() {

        private var baseUrlSecond =
            "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&units=metric&appid=$API_KEY"
        // https://api.openweathermap.org/data/2.5/forecast?lat=57&lon=24.0833&units=metric&appid=91db09ff13832921fd93739ff0fcc890

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL(baseUrlSecond).readText(
                    Charsets.UTF_8
                )
                Log.d(TAG, "doInBackground Called, $response")
            } catch (e: Exception) {
                Log.d(TAG, "doInBackground Catch Exception: $e")
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON from the API */
                //TODO Now when it works let simplify code by Moshi usage
                val jsonObj = JSONObject(result)
                val jsonList = jsonObj.getJSONArray("list")//.getJSONObject(0)
                for (i in 0 until jsonList.length()) {
                    val objects = jsonList.getJSONObject(i)
                    val main: JSONObject = objects["main"] as JSONObject
                    val temp = main.getString("temp")
                    val wind: JSONObject = objects["wind"] as JSONObject
                    val windSpeed = wind.getString("speed")
                    val weatherArray = objects.getJSONArray("weather")
                    val weather = weatherArray.getJSONObject(0)
                    val iconId = weather.getString("icon")
                    val dateText: Long = objects.getLong("dt")

                    val dateTextFormatted =
                        SimpleDateFormat("d MMM yyyy    HH:mm", Locale.ENGLISH).format(
                            Date(dateText * 1000)
                        )

                    val dateWeatherData = DateWeather(dateTextFormatted, temp, windSpeed, iconId)
                    viewModel.add(dateWeatherData)
                }

                Log.d(TAG, "onPostExecute called")
            } catch (e: Exception) {
                Log.d(TAG, "Exception onPostExecute: $e")
            }
        }
    }
}