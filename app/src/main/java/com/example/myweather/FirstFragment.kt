package com.example.myweather

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
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.myweather.database.currentWeather.WeatherDatabase
import com.example.myweather.databinding.FragmentFirstBinding
import com.example.myweather.model.CurrentWeather
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.utils.Resource
import com.example.myweather.viewModels.currentWeather.CurrentWeatherModelFactory
import com.example.myweather.viewModels.currentWeather.CurrentWeatherViewModel
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "FirstFragment"

private var lat = ""
private var lon = ""

class FirstFragment : Fragment() {

    private lateinit var currentViewModel: CurrentWeatherViewModel
    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val currentRepository = CurrentWeatherRepository(
            WeatherDatabase.createDatabase(
                this
            )
        )
        val currentWeatherModelFactory = CurrentWeatherModelFactory(currentRepository)
        currentViewModel =
            ViewModelProvider(this, currentWeatherModelFactory)[CurrentWeatherViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first, container, false)
        binding.lifecycleOwner = this
        // viewModel declared in XML equals currentViewModel
        //binding.viewModel = currentViewModel

        // Search function
        val searchIcon = binding.root.findViewById<ImageView>(R.id.search_icon)
        searchIcon.setOnClickListener {
            val city = getCity()
            getAndDisplayData()
            currentViewModel.searchCurrentWeather(city)
            Log.d(TAG, "Search Button clicked")
        }
        getAndDisplayData()
        Log.d(TAG, "OnCreateView called")
        return binding.root
    }

    private fun getAndDisplayData() {
        currentViewModel.currentList.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { weatherResponse ->
                            // TODO: Assign weatherResponse to currentViewModel.currentList
                            val list = weatherResponse.weatherList

                            val body = list[0]
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

                            binding.cityName.text = address
                            binding.temperature.text = temp
                            binding.tempMin.text = tempMin
                            binding.tempMax.text = tempMax
                            binding.pressure.text = pressure
                            binding.humidityData.text = humidity
                            binding.windData.text = windSpeed
                            binding.conditions.text = weatherDescription

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

    private fun getCity(): String {
        var city = ""
        val editCity = requireActivity().findViewById<TextInputEditText>(R.id.edit_city)
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