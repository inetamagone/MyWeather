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

private const val TAG = "SecondFragment"
private var lat: String = ""
private var lon: String = ""


class SecondFragment : Fragment() {
    private var viewManager = LinearLayoutManager(context)
    private lateinit var viewModel: DateViewModel
    private lateinit var dateRecycler: RecyclerView
    private lateinit var arrayList: ArrayList<DateWeather>

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
        lat = latString.toString()
        lon = lonString.toString()

        val viewTitle = view.findViewById<TextView>(R.id.title_text)
        viewTitle.text = resources.getString(R.string.weather_in_city, cityString)
        Log.d(TAG, "Title text: ${viewTitle.text}")

        dateRecycler = view.findViewById(R.id.recycler_view)
        //dateRecycler.adapter = DateViewAdapter(viewModel, this, arrayList)
        //requireNotNull(this).view
        val factory = DateViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(DateViewModel::class.java)

        //TODO Wasn't initialized lateinit
        dateRecycler = view.findViewById(R.id.recycler_view)

        initialiseAdapter()
        getWeatherByDate().execute()
        //dateRecycler.adapter?.notifyDataSetChanged()

        return view
    }

    private fun initialiseAdapter() {
        dateRecycler.layoutManager = viewManager
        observeData()
    }

    //TODO green underlines
    private fun observeData() {
        viewModel.list.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            dateRecycler.adapter = DateViewAdapter(viewModel,this, arrayList)
        })
//        viewModel.list.observe(viewLifecycleOwner, Observer {
//            dateRecycler.adapter= DateViewAdapter(viewModel, this, arrayList)
//        })
    }

    //TODO Depricated + leaks may occur but let it stay as is
    inner class getWeatherByDate() : AsyncTask<String, Void, String>() {

        //TODO green underlines, val, const
        val BASE_URL_SECOND =
            "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&units=metric&appid=$API_KEY"
            // https://api.openweathermap.org/data/2.5/forecast?lat=57&lon=24.0833&units=metric&appid=91db09ff13832921fd93739ff0fcc890

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL(BASE_URL_SECOND).readText(
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
                val list = jsonObj.getJSONArray("list").getJSONObject(0)
                val main = list.getJSONObject("main")
                val weather = list.getJSONArray("weather").getJSONObject(0)
                val wind = list.getJSONObject("wind")
                val dateText: Long = list.getLong("dt")

                val dateTextFormatted = SimpleDateFormat("d/MMM/yyyy HH:mm", Locale.ENGLISH).format(
                    Date(dateText * 1000)
                )
                val temp = main.getString("temp") + "°C"
                val iconId = weather.getString("icon")
                val windSpeed = wind.getString("speed")

                var dateWeatherData = DateWeather(dateTextFormatted, temp, windSpeed, iconId)
                //TODO was declared new local variable and response data was added to it instead of the global viewmodel
                //val viewModel = DateViewModel()
                viewModel.add(dateWeatherData)
                //dateRecycler.adapter?.notifyDataSetChanged()

                Log.d(TAG, "date: $dateTextFormatted")
                Log.d(TAG, "temp: $temp")
                Log.d(TAG, "iconId: $iconId")
                Log.d(TAG, "windSpeed: $windSpeed")
            } catch (e: Exception) {
                Log.d(TAG, "Exception onPostExecute: $e")
            }
        }
    }
}