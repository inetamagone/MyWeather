package com.example.myweather

import android.annotation.SuppressLint
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
import com.example.myweather.database.currentWeather.WeatherDatabase
import com.example.myweather.database.dateWeather.DateWeatherDatabase
import com.example.myweather.model.DateWeather
import com.example.myweather.network.ApiService
import com.example.myweather.network.dateData.DateWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.repository.DateWeatherRepository
import com.example.myweather.utils.BASE_URL
import com.example.myweather.utils.Resource
import com.example.myweather.viewModels.dateWeather.DateViewModel
import com.example.myweather.viewModels.dateWeather.DateViewModelFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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
    private lateinit var dateViewAdapter: DateViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dateRepository = DateWeatherRepository(
            DateWeatherDatabase.createDatabase(
                this
            )
        )
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        // Getting String values from the FirstFragment
        val cityString = requireArguments().getString("cityName")
        val latString = requireArguments().getString("latString")
        val lonString = requireArguments().getString("lonString")
        // View title text
        view.findViewById<TextView>(R.id.title_text).text =
            resources.getString(R.string.weather_in_city, cityString)

        // TODO
        lat = latString.toString()
        lon = lonString.toString()

        val factory = DateViewModelFactory(dateRepository)
        viewModel = ViewModelProvider(this, factory)[DateViewModel::class.java]
        dateRecycler = view.findViewById(R.id.recycler_view)

        dateRecycler.layoutManager = viewManager
        // API call
        getWeatherByDate()
        Log.d(TAG, "OnCreateView Called")
        return view
    }

    private fun getWeatherByDate() {
        viewModel.dateList.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { dateWeatherResponse ->
                        dateViewAdapter.differ.submitList(dateWeatherResponse.weatherList)
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Log.e(TAG, "Resource.Error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    Log.e(TAG, "Resource.Loading")
                }
            }
        })
    }
}