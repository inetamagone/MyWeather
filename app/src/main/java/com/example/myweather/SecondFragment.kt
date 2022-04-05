package com.example.myweather

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.adapter.DateViewAdapter
import com.example.myweather.model.DateWeather
import com.example.myweather.utils.API_Key
import com.example.myweather.utils.LAT
import com.example.myweather.utils.LON
import com.example.myweather.viewModels.DateViewModel
import com.example.myweather.viewModels.DateViewModelFactory
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "SecondFragment"

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

        val viewTitle = view.findViewById<TextView>(R.id.title_text)
        viewTitle.text = resources.getString(R.string.weather_in_city, "Riga")

        dateRecycler = view.findViewById(R.id.recycler_view)
        //dateRecycler.adapter = DateViewAdapter(viewModel, this, arrayList)
        //requireNotNull(this).view
        val factory = DateViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(DateViewModel::class.java)

        initialiseAdapter()
        getWeatherByDate().execute()
        dateRecycler.adapter?.notifyDataSetChanged()

        return view
    }

    private fun initialiseAdapter() {
        dateRecycler.layoutManager = viewManager
        observeData()
    }

    fun observeData() {
        viewModel.list.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            dateRecycler.adapter = DateViewAdapter(viewModel,this, arrayList)
        })
//        viewModel.list.observe(viewLifecycleOwner, Observer {
//            dateRecycler.adapter= DateViewAdapter(viewModel, this, arrayList)
//        })
    }

    class getWeatherByDate() : AsyncTask<String, Void, String>() {

        val BASE_URL_SECOND =
            "api.openweathermap.org/data/2.5/forecast?lat=$LAT&lon=$LON&appid=$API_Key"

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL(BASE_URL_SECOND).readText(
                    Charsets.UTF_8
                )
                Log.d(TAG, "doInBackground Called")
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val dateText: Long = jsonObj.getLong("dt")

                val dateTextFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(
                    Date(dateText * 1000)
                )
                val temp = main.getString("temp") + "°C"
                val windSpeed = wind.getString("speed")
                val iconId = weather.getString("icon")

                var dateWeatherData = DateWeather(dateTextFormatted, temp, windSpeed, iconId)
                val viewModel = DateViewModel()
                viewModel.add(dateWeatherData)

                //dateRecycler.adapter?.notifyDataSetChanged()

                Log.d(TAG, "jsonObj: $jsonObj")
            } catch (e: Exception) {
                Log.d(TAG, "Exception onPostExecute: $e")
            }
        }
    }
}