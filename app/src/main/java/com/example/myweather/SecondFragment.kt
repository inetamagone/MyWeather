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
import com.example.myweather.model.DateWeather
import com.example.myweather.network.ApiService
import com.example.myweather.network.dateData.DateWeatherData
import com.example.myweather.utils.BASE_URL
import com.example.myweather.viewModels.DateViewModel
import com.example.myweather.viewModels.DateViewModelFactory
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
        viewModel = ViewModelProvider(this, factory)[DateViewModel::class.java]
        dateRecycler = view.findViewById(R.id.recycler_view)
        arrayList = viewModel.newList

        initialiseAdapter()
        // API call
        getWeatherByDate()
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

    private fun getWeatherByDate() {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
        apiService.searchWeatherForecast(lat, lon).enqueue(
            object : Callback<DateWeatherData> {
                override fun onResponse(
                    call: Call<DateWeatherData>,
                    response: Response<DateWeatherData>
                ) {
                    Log.d(TAG, response.toString())
                    if (!response.isSuccessful) {
                        Log.d(TAG, "Unsuccessful network call")
                        return
                    }
                    val body = response.body()!!
                    val list = body.list
                    for (element in list) {
                        val main = element.main
                        val temp = main.temp.toString()

                        val wind = element.wind
                        val windSpeed = wind.speed.toString()
                        val weatherArray = element.weather
                        val weather = weatherArray[0]
                        val iconId = weather.icon
                        val dateText = element.dt.toLong()

                        val dateTextFormatted =
                            SimpleDateFormat("d MMM yyyy    HH:mm", Locale.ENGLISH).format(
                                Date(dateText * 1000)
                            )
                        val dateWeatherData =
                            DateWeather(dateTextFormatted, temp, windSpeed, iconId)
                        viewModel.add(dateWeatherData)
                    }
                }
                override fun onFailure(call: Call<DateWeatherData>, t: Throwable) {
                    Log.d(TAG, t.message ?: "Null message")
                }
            })
    }
}