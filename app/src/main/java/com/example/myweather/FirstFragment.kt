package com.example.myweather

import android.annotation.SuppressLint
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
import com.example.myweather.databinding.FragmentFirstBinding
import com.example.myweather.utils.DEFAULT_CITY
import com.example.myweather.viewModels.WeatherViewModel
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "FirstFragment"
private var city = ""

private var lat = ""
private var lon = ""

class FirstFragment : Fragment() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: FragmentFirstBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first, container, false)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        binding.lifecycleOwner = this


        Log.d(TAG, "OnCreateView called")
        return binding.root
    }

    // Navigation to the SecondFragment
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Here making API call, getting data from database, observing currentList and populating into the views
        viewModel.getCurrentWeatherApi(requireContext())
        val dbData = viewModel.getDataFromDb(requireContext())
        Log.d(TAG, "onLaunch DBdata on launch: $dbData")
        viewModel.currentList.observe(viewLifecycleOwner) {
            if (it == null) {
                Log.d(TAG, "Data was not found onLaunch")
            } else {
                Log.d(TAG, "onLaunch DBdata found to populate: $it")
                val updatedAt = it.dt.toLong()
                val icon = it.weather[0].icon
                val imageUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
                // http://openweathermap.org/img/wn/04d@2x.png

                // For the API call in the SecondFragment
                lat = it.coord.lat.toString()
                lon = it.coord.lon.toString()

                requireActivity().findViewById<TextView>(R.id.city_name).text =
                    it.name + ", " + it.sys.country
                requireActivity().findViewById<TextView>(R.id.updated_time).text =
                    "Updated at: " + SimpleDateFormat(
                        "dd/MM/yyyy  HH:mm",
                        Locale.ENGLISH
                    ).format(
                        Date(updatedAt * 1000)
                    ) + "h"
                requireActivity().findViewById<TextView>(R.id.conditions).text =
                    it.weather[0].description
                requireActivity().findViewById<TextView>(R.id.temperature).text =
                    it.main.temp.toString() + "°C"
                requireActivity().findViewById<TextView>(R.id.temp_min).text =
                    "Min Temp: " + it.main.tempMin + "°C"
                requireActivity().findViewById<TextView>(R.id.temp_max).text =
                    "Max Temp: " + it.main.tempMax + "°C"
                requireActivity().findViewById<TextView>(R.id.wind_data).text =
                    it.wind.speed.toString() + " m/s"
                requireActivity().findViewById<TextView>(R.id.humidity_data).text =
                    it.main.humidity.toString() + " %"
                requireActivity().findViewById<TextView>(R.id.pressure).text =
                    it.main.pressure.toString() + " hPa"


                // Image icon
                Glide.with(requireContext())
                    .load(imageUrl)
                    .into(view?.findViewById(R.id.image_main)!!)
                Log.d(TAG, "So I found data...")
            }

        }

        // Search function
        val searchIcon = binding.root.findViewById<ImageView>(R.id.search_icon)
        searchIcon.setOnClickListener {
            Log.d(TAG, "Search Button clicked")
            city = getCity()
            val searchApi = viewModel.searchCurrentWeatherApi(requireContext(), city)
            Log.d(TAG, "SearchApi called: $searchApi")
            val dbData = viewModel.getSearchFromDb(requireContext(), city)
            Log.d(TAG, "Search DbData called: $dbData")
            viewModel.currentList.observe(viewLifecycleOwner) {
                if (it == null) {
                    Log.d(TAG, "Data was not found in search")
                } else {
                    Log.d(TAG, "Search CurrentWeatherData it got: $it")
                    val updatedAt = it.dt.toLong()
                    val icon = it.weather[0].icon
                    val imageUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
                    // http://openweathermap.org/img/wn/04d@2x.png

                    // For the API call in the SecondFragment
                    lat = it.coord.lat.toString()
                    lon = it.coord.lon.toString()

                    requireActivity().findViewById<TextView>(R.id.city_name).text =
                        it.name + ", " + it.sys.country
                    requireActivity().findViewById<TextView>(R.id.updated_time).text =
                        "Updated at: " + SimpleDateFormat(
                            "dd/MM/yyyy  HH:mm",
                            Locale.ENGLISH
                        ).format(
                            Date(updatedAt * 1000)
                        ) + "h"
                    requireActivity().findViewById<TextView>(R.id.conditions).text =
                        it.weather[0].description
                    requireActivity().findViewById<TextView>(R.id.temperature).text =
                        it.main.temp.toString() + "°C"
                    requireActivity().findViewById<TextView>(R.id.temp_min).text =
                        "Min Temp: " + it.main.tempMin + "°C"
                    requireActivity().findViewById<TextView>(R.id.temp_max).text =
                        "Max Temp: " + it.main.tempMax + "°C"
                    requireActivity().findViewById<TextView>(R.id.wind_data).text =
                        it.wind.speed.toString() + " m/s"
                    requireActivity().findViewById<TextView>(R.id.humidity_data).text =
                        it.main.humidity.toString() + " %"
                    requireActivity().findViewById<TextView>(R.id.pressure).text =
                        it.main.pressure.toString() + " hPa"


                    // Image icon
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(view?.findViewById(R.id.image_main)!!)
                    Log.d(TAG, "So I populated searched data...")
                }

            }
        }


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

        // Search function
        val historyButton = view.findViewById<Button>(R.id.button_history)
        historyButton.setOnClickListener {
            Log.d(TAG, "History Button clicked")

            navController.navigate(R.id.action_firstFragment_to_historyFragment, Bundle())
        }
    }

    private fun getCity(): String {
        requireActivity().findViewById<TextInputEditText>(R.id.edit_city).apply {
            return if (text.isNullOrBlank()) DEFAULT_CITY else text.toString()
        }
    }
}