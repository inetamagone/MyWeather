package com.example.myweather

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "FirstFragment"
private var city = ""
private var lat = ""
private var lon = ""

class FirstFragment : Fragment(R.layout.fragment_first) {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private var rootView: View? = null
    private var isFirstLoad = false

    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        if (rootView == null) {
            rootView = binding.root
            isFirstLoad = true
        } else {
            isFirstLoad = false
        }
        return rootView
    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isFirstLoad) {
            val repository = CurrentWeatherRepository(CurrentWeatherDatabase(requireContext()))
            val factory = CurrentModelFactory(repository)
            viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

            viewModel.getCurrentWeatherApi(requireContext())

            viewModel.getDataFromDb().observe(viewLifecycleOwner) {
                if (it == null) {
                    Log.d(TAG, getString(R.string.data_not_found_view_created))
                } else {
                    val updatedAt = it.dt.toLong()
                    val updatedText = SimpleDateFormat(
                        "dd/MM/yyyy  HH:mm",
                        Locale.ENGLISH
                    ).format(
                        Date(updatedAt * 1000)
                    )
                    val icon = it.weather[0].icon

                    // For the API call in the SecondFragment
                    lat = it.coord.lat.toString()
                    lon = it.coord.lon.toString()

                    binding.apply {
                        cityName.text = getString(R.string.text_city, it.name, it.sys.country)
                        updatedTime.text = getString(R.string.text_updated, updatedText)
                        conditions.text =
                            getString(R.string.text_conditions, it.weather[0].description)
                        temperature.text = getString(R.string.text_temp, it.main.temp.toString())
                        tempMin.text = getString(R.string.text_min_temp, it.main.tempMin.toString())
                        tempMax.text = getString(R.string.text_max_temp, it.main.tempMax.toString())
                        windData.text = getString(R.string.text_wind, it.wind.speed.toString())
                        humidityData.text =
                            getString(R.string.text_humidity, it.main.humidity.toString() + " %")
                        pressure.text =
                            getString(R.string.text_pressure, it.main.pressure.toString())
                    }

                    // Image icon
                    Glide.with(requireContext())
                        .load("https://openweathermap.org/img/wn/$icon@2x.png")
                        // http://openweathermap.org/img/wn/04d@2x.png
                        .into(binding.imageMain)
                }
                viewModel.saveToRestore(it)
                //mIsFirstLoad = false
            }

        } else {
            // Load from array in viewModel
            val updatedAt = viewModel.savedData[0].dt.toLong()
            val updatedText = SimpleDateFormat(
                "dd/MM/yyyy  HH:mm",
                Locale.ENGLISH
            ).format(
                Date(updatedAt * 1000)
            )
            val icon = viewModel.savedData[0].weather[0].icon
            binding.apply {
                cityName.text = getString(
                    R.string.text_city,
                    viewModel.savedData[0].name,
                    viewModel.savedData[0].sys.country
                )
                updatedTime.text = getString(R.string.text_updated, updatedText)
                conditions.text = getString(
                    R.string.text_conditions,
                    viewModel.savedData[0].weather[0].description
                )
                temperature.text =
                    getString(R.string.text_temp, viewModel.savedData[0].main.temp.toString())
                tempMin.text = getString(
                    R.string.text_min_temp,
                    viewModel.savedData[0].main.tempMin.toString()
                )
                tempMax.text = getString(
                    R.string.text_max_temp,
                    viewModel.savedData[0].main.tempMax.toString()
                )
                windData.text =
                    getString(R.string.text_wind, viewModel.savedData[0].wind.speed.toString())
                humidityData.text = getString(
                    R.string.text_humidity,
                    viewModel.savedData[0].main.humidity.toString() + " %"
                )
                pressure.text = getString(
                    R.string.text_pressure,
                    viewModel.savedData[0].main.pressure.toString()
                )
            }
            Glide.with(requireContext())
                .load("https://openweathermap.org/img/wn/$icon@2x.png")
                .into(binding.imageMain)
        }

        // Search function
        val searchIcon = requireActivity().findViewById<ImageView>(R.id.search_icon)

        searchIcon.setOnClickListener {
            Log.d(TAG, "Search button clicked")
//            val editText = requireActivity().findViewById<EditText>(R.id.edit_city)
//            val city = editText.text.formatting()
//            editText.setText("")
            city = getCity()
            Log.d(TAG, "the city searched: $city")
            viewModel.searchCurrentWeatherApi(requireContext(), city)
            viewModel.getSearchFromDb(city).observe(viewLifecycleOwner) {
                if (it == null) {
                    Log.d(TAG, "Data not found in search, it: $it")
                    //Log.d(TAG, getString(R.string.data_not_found_in_search))
                } else {
                    Log.d(TAG, "Data IS found in search, it: $it")
                    val updatedAt = it.dt.toLong()
                    val updatedText = SimpleDateFormat(
                        "dd/MM/yyyy  HH:mm",
                        Locale.ENGLISH
                    ).format(
                        Date(updatedAt * 1000)
                    )
                    val icon = it.weather[0].icon

                    // For the API call in the SecondFragment
                    lat = it.coord.lat.toString()
                    lon = it.coord.lon.toString()

                    requireActivity().findViewById<TextView>(R.id.city_name).text = getString(R.string.text_city, it.name, it.sys.country)
                    requireActivity().findViewById<TextView>(R.id.updated_time).text = getString(R.string.text_updated, updatedText)

                    requireActivity().findViewById<TextView>(R.id.conditions).text = getString(R.string.text_conditions, it.weather[0].description)
                    requireActivity().findViewById<TextView>(R.id.temperature).text = getString(R.string.text_temp, it.main.temp.toString())
                    requireActivity().findViewById<TextView>(R.id.temp_min).text = getString(R.string.text_min_temp, it.main.tempMin.toString())
                    requireActivity().findViewById<TextView>(R.id.temp_max).text = getString(R.string.text_max_temp, it.main.tempMax.toString())
                    requireActivity().findViewById<TextView>(R.id.wind_data).text = getString(R.string.text_wind, it.wind.speed.toString())
                    requireActivity().findViewById<TextView>(R.id.humidity_data).text =
                            getString(R.string.text_humidity, it.main.humidity.toString() + " %")
                    requireActivity().findViewById<TextView>(R.id.pressure).text =
                            getString(R.string.text_pressure, it.main.pressure.toString())

                    // Image icon
                    Glide.with(requireContext())
                        .load("https://openweathermap.org/img/wn/$icon@2x.png")
                        .into(requireActivity().findViewById(R.id.image_main))

                    viewModel.savedData.clear()
                    viewModel.saveToRestore(it)
                }
            }
        }

        // Navigation to the Second Fragment
        val navController = Navigation.findNavController(view)

        requireActivity().findViewById<Button>(R.id.button_next).setOnClickListener {
            val cityString = requireActivity().findViewById<TextView>(R.id.city_name).text.toString()

            navController.navigate(R.id.action_firstFragment_to_secondFragment, Bundle().apply {
                putString("cityName", cityString)
                putString("latString", lat)
                putString("lonString", lon)
            })
        }

        // History view
        requireActivity().findViewById<Button>(R.id.button_history).setOnClickListener {
            navController.navigate(R.id.action_firstFragment_to_historyFragment, Bundle())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCity(): String {
        requireActivity().findViewById<EditText>(R.id.edit_city)
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