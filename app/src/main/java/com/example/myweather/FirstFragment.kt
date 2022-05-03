package com.example.myweather

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
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
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.databinding.FragmentFirstBinding
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.utils.DEFAULT_CITY
import com.example.myweather.viewModels.factories.CurrentModelFactory
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first, container, false)
        binding.lifecycleOwner = this

        val repository = CurrentWeatherRepository(CurrentWeatherDatabase(requireContext()))
        val factory = CurrentModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        viewModel.getCurrentWeatherApi(requireContext())
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDataFromDb().observe(viewLifecycleOwner) {
            if (it == null) {
                Log.d(TAG, getString(R.string.data_not_found_view_created))
            } else {
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
                    .into(requireActivity().findViewById(R.id.image_main)!!)
            }
        }

        // Search function
        val searchIcon = view.findViewById<ImageView>(R.id.search_icon)
        searchIcon.setOnClickListener {
            city = getCity()
            viewModel.searchCurrentWeatherApi(requireContext(), city)
            viewModel.getSearchFromDb(city).observe(viewLifecycleOwner) {
                if (it == null) {
                    Log.d(TAG, getString(R.string.data_not_found_in_search))
                } else {
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
                        .into(requireActivity().findViewById(R.id.image_main)!!)
                }
            }
        }
        // Navigation to the Second Fragment
        val navController = Navigation.findNavController(view)

        view.findViewById<Button>(R.id.button_next).setOnClickListener {
            val cityString = view.findViewById<TextView>(R.id.city_name).text.toString()

            navController.navigate(R.id.action_firstFragment_to_secondFragment, Bundle().apply {
                putString("cityName", cityString)
                putString("latString", lat)
                putString("lonString", lon)
            })
        }

        // History view
        val historyButton = view.findViewById<Button>(R.id.button_history)
        historyButton.setOnClickListener {
            navController.navigate(R.id.action_firstFragment_to_historyFragment, Bundle())
        }
    }

    private fun getCity(): String {
        requireActivity().findViewById<TextInputEditText>(R.id.edit_city)
            .apply {
                when {
                    text.isNullOrEmpty() -> city = DEFAULT_CITY
                    else -> {
                        city = text.formatting()
                        setText("")
                    }
                }
            }
        return city
    }
}

private fun Editable?.formatting(): String {
    return this.toString().trim().capitalize()
}