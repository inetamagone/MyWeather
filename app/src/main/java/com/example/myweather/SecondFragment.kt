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
import com.example.myweather.viewModels.WeatherViewModel
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

private const val TAG = "SecondFragment"
private var lat: String = ""
private var lon: String = ""

class SecondFragment : Fragment() {
    private lateinit var viewModel: WeatherViewModel
    private lateinit var recyclerView: RecyclerView

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

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        recyclerView = view.findViewById(R.id.recycler_view)
        val adapter = DateViewAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getDateWeatherApi(requireContext(), lat, lon)
        viewModel.getAllByDate(requireContext())
            .observe(viewLifecycleOwner) { byDate ->
                if (byDate == null) {
                    Log.d(TAG, "Date fragment data was not found!")
                } else {
                    byDate.let {
                        adapter.differ.submitList(byDate)
                        Log.d(TAG, "Data submitted to adapter!")
                    }
                }
            }
        viewModel.deleteAllDateList(requireContext())
        return view
    }


//    private fun getWeatherByDate() {
//        val moshi = Moshi.Builder()
//            .addLast(KotlinJsonAdapterFactory()).build()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//
//        val apiService: ApiService = retrofit.create(ApiService::class.java)
//        apiService.searchWeatherForecast(lat, lon).enqueue(
//            object : Callback<DateWeatherData> {
//                override fun onResponse(
//                    call: Call<DateWeatherData>,
//                    response: Response<DateWeatherData>
//                ) {
//                    Log.d(TAG, response.toString())
//                    if (!response.isSuccessful) {
//                        Log.d(TAG, "Unsuccessful network call")
//                        return
//                    }
//                    val body = response.body()!!
//                    val list = body.list
//                    for (element in list) {
//                        val main = element.main
//                        val temp = main.temp.toString()
//
//                        val wind = element.wind
//                        val windSpeed = wind.speed.toString()
//                        val weatherArray = element.weather
//                        val weather = weatherArray[0]
//                        val iconId = weather.icon
//                        val dateText = element.dt.toLong()
//
//                        val dateTextFormatted =
//                            SimpleDateFormat("d MMM yyyy    HH:mm", Locale.ENGLISH).format(
//                                Date(dateText * 1000)
//                            )
//                        val dateWeatherData =
//                            DateWeather(dateTextFormatted, temp, windSpeed, iconId)
//                        viewModel.add(dateWeatherData)
//                    }
//                }
//                override fun onFailure(call: Call<DateWeatherData>, t: Throwable) {
//                    Log.d(TAG, t.message ?: "Null message")
//                }
//            })
//    }
}