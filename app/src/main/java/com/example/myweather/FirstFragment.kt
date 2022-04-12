package com.example.myweather

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.Glide
import com.example.myweather.database.WeatherDatabase
import com.example.myweather.databinding.FragmentFirstBinding
import com.example.myweather.model.CurrentWeather
import com.example.myweather.model.WeatherResponse
import com.example.myweather.network.ApiService
import com.example.myweather.network.currentData.CurrentWeatherData
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.utils.BASE_URL
import com.example.myweather.utils.Resource
import com.example.myweather.viewModels.CurrentWeatherModelFactory
import com.example.myweather.viewModels.CurrentWeatherViewModel
import com.google.android.material.textfield.TextInputEditText
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "FirstFragment"
private var city = ""

private var lat = ""
private var lon = ""

class FirstFragment : Fragment() {

    private lateinit var currentViewModel: CurrentWeatherViewModel
    private lateinit var binding: FragmentFirstBinding

//    companion object {
//        fun FirstFragmentInstance(): FirstFragment = FirstFragment()
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val currentRepository = CurrentWeatherRepository(WeatherDatabase.createDatabase(
            FirstFragment()))
        val currentWeatherModelFactory = CurrentWeatherModelFactory(currentRepository)
        currentViewModel = ViewModelProvider(this, currentWeatherModelFactory).get(CurrentWeatherViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first, container, false)
        binding.lifecycleOwner = this
        // viewModel declared in XML equals currentViewModel
        binding.viewModel = currentViewModel

        // API call here
        //getWeather()
        // Search function
        val searchIcon = binding.root.findViewById<ImageView>(R.id.search_icon)
        searchIcon.setOnClickListener {
            //searchWeather()
            Log.d(TAG, "Search Button clicked")
        }
        Log.d(TAG, "OnCreateView called")
        return binding.root
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


        currentViewModel.currentList.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { weatherResponse ->
                            // TODO: Here send data to be saved
                            val body = weatherResponse.weatherList[0]
                            /* Getting API data */
                            val updatedAt = body.dt.toLong()
                            val upDatedAtText =
                                "Updated at: " + SimpleDateFormat(
                                    "dd/MM/yyyy  HH:mm",
                                    Locale.ENGLISH
                                ).format(
                                    Date(updatedAt * 1000)
                                ) + "h"

                            val address = body.name + ", " + body.sys.country
                            val temp = body.main.temp.toString() + "°C"
                            val tempMin = "Min Temp: " + body.main.tempMin + "°C"
                            val tempMax = "Max Temp: " + body.main.tempMax + "°C"
                            val pressure = body.main.pressure.toString() + " hPa"
                            val humidity = body.main.humidity.toString() + " %"

                            val windSpeed = body.wind.speed.toString() + " m/s"
                            val weatherDescription = body.weather[0].description
                            val icon = body.weather[0].icon
                            val imageUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
                            // http://openweathermap.org/img/wn/04d@2x.png

                            // For the API call in the SecondFragment
                            lat = body.coord.lat.toString()
                            lon = body.coord.lon.toString()

//                            val currentWeather = CurrentWeather(
//                                address,
//                                upDatedAtText,
//                                temp,
//                                tempMin,
//                                tempMax,
//                                pressure,
//                                humidity,
//                                windSpeed,
//                                weatherDescription,
//                                lat,
//                                lon
//                            )
//                            currentViewModel.add(currentWeather)

                            // Image icon
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .into(view?.findViewById(R.id.image_main)!!)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val cityString = view?.findViewById<TextView>(R.id.city_name)?.text
        outState.putCharSequence("Saved city", cityString)
        Log.d(TAG, "Saved city $cityString")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val storedCity = savedInstanceState?.getCharSequence("Saved city")
        city = storedCity.toString()
        Log.d(TAG, "Restored city $storedCity")
    }

    // Using Retrofit and Moshi
//    private fun getWeather() {
//        val moshi = Moshi.Builder()
//            .addLast(KotlinJsonAdapterFactory()).build()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//
//        val apiService: ApiService = retrofit.create(ApiService::class.java)
//        // API request
//        apiService.getCurrentWeather().enqueue(
//            object : Callback<CurrentWeatherData> {
//                override fun onResponse(
//                    call: Call<CurrentWeatherData>,
//                    response: Response<CurrentWeatherData>
//                ) {
//                    Log.d(TAG, response.toString())
//                    if (!response.isSuccessful) {
//                        Log.d(TAG, "Unsuccessful network call")
//                        return
//                    }
//                    val body = response.body()!!
//                    /* Getting API data and sending to the currentViewModel */
//                    val updatedAt = body.dt.toLong()
//                    val upDatedAtText =
//                        "Updated at: " + SimpleDateFormat(
//                            "dd/MM/yyyy  HH:mm",
//                            Locale.ENGLISH
//                        ).format(
//                            Date(updatedAt * 1000)
//                        ) + "h"
//
//                    val address = body.name + ", " + body.sys.country
//                    val temp = body.main.temp.toString() + "°C"
//                    val tempMin = "Min Temp: " + body.main.tempMin + "°C"
//                    val tempMax = "Max Temp: " + body.main.tempMax + "°C"
//                    val pressure = body.main.pressure.toString() + " hPa"
//                    val humidity = body.main.humidity.toString() + " %"
//
//                    val windSpeed = body.wind.speed.toString() + " m/s"
//                    val weatherDescription = body.weather[0].description
//                    val icon = body.weather[0].icon
//                    val imageUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
//                    // http://openweathermap.org/img/wn/04d@2x.png
//
//                    // For the API call in the SecondFragment
//                    lat = body.coord.lat.toString()
//                    lon = body.coord.lon.toString()
//
//                    val currentWeather = CurrentWeather(
//                        address,
//                        upDatedAtText,
//                        temp,
//                        tempMin,
//                        tempMax,
//                        pressure,
//                        humidity,
//                        windSpeed,
//                        weatherDescription,
//                        lat,
//                        lon
//                    )
//                    currentViewModel.add(currentWeather)
//
//                    // Image icon
//                    Glide.with(context!!)
//                        .load(imageUrl)
//                        .into(view?.findViewById(R.id.image_main)!!)
//                }
//
//                override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
//                    Log.d(TAG, t.message ?: "Null message")
//                }
//            })
//    }
//
//    private fun searchWeather() {
//        val moshi = Moshi.Builder()
//            .addLast(KotlinJsonAdapterFactory()).build()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//
//        val apiService: ApiService = retrofit.create(ApiService::class.java)
//        // Get the search query for the city
//        city = getCity()
//        // API request
//        apiService.searchCurrentWeather(city).enqueue(
//            object : Callback<CurrentWeatherData> {
//                override fun onResponse(
//                    call: Call<CurrentWeatherData>,
//                    response: Response<CurrentWeatherData>
//                ) {
//                    Log.d(TAG, response.toString())
//                    if (!response.isSuccessful) {
//                        Log.d(TAG, "Unsuccessful network call")
//                        return
//                    }
//                    val body = response.body()!!
//                    /* Getting API data and sending to the currentViewModel */
//                    val updatedAt = body.dt.toLong()
//                    val upDatedAtText =
//                        "Updated at: " + SimpleDateFormat(
//                            "dd/MM/yyyy  HH:mm",
//                            Locale.ENGLISH
//                        ).format(
//                            Date(updatedAt * 1000)
//                        ) + "h"
//
//                    val address = body.name + ", " + body.sys.country
//                    val temp = body.main.temp.toString() + "°C"
//                    val tempMin = "Min Temp: " + body.main.tempMin + "°C"
//                    val tempMax = "Max Temp: " + body.main.tempMax + "°C"
//                    val pressure = body.main.pressure.toString() + " hPa"
//                    val humidity = body.main.humidity.toString() + " %"
//
//                    val windSpeed = body.wind.speed.toString() + " m/s"
//                    val weatherDescription = body.weather[0].description
//                    val icon = body.weather[0].icon
//                    val imageUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
//                    // http://openweathermap.org/img/wn/04d@2x.png
//
//                    // For the API call in the SecondFragment
//                    lat = body.coord.lat.toString()
//                    lon = body.coord.lon.toString()
//
//                    val currentWeather = CurrentWeather(
//                        address,
//                        upDatedAtText,
//                        temp,
//                        tempMin,
//                        tempMax,
//                        pressure,
//                        humidity,
//                        windSpeed,
//                        weatherDescription,
//                        lat,
//                        lon
//                    )
//                    currentViewModel.add(currentWeather)
//
//                    // Image icon
//                    Glide.with(context!!)
//                        .load(imageUrl)
//                        .into(view?.findViewById(R.id.image_main)!!)
//                }
//
//                override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
//                    Log.d(TAG, t.message ?: "Null message")
//                }
//            })
//    }
//
//    private fun getCity(): String {
//        val editCity = requireActivity().findViewById<TextInputEditText>(R.id.edit_city)
//        if (editCity?.text.isNullOrEmpty()) {
//            city = "Riga"
//        } else {
//            city = editCity?.text.toString()
//            Log.v(TAG, "Change cityName: $city")
//            editCity?.setText("")
//        }
//        return city
//    }
}