package com.example.myweather

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.databinding.FragmentFirstBinding
import com.example.myweather.network.currentData.CurrentWeatherData
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

    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        val repository = CurrentWeatherRepository(CurrentWeatherDatabase(requireContext()))
        val factory = CurrentModelFactory(this, repository)
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        viewModel.savedStateData.observe(viewLifecycleOwner) { savedData ->
            if (savedData == null) {
                // Api call
                viewModel.getCurrentWeatherApi(requireContext())

                viewModel.getDataFromDb().observe(viewLifecycleOwner) { dbData ->
                    if (dbData == null) {
                        Log.d(TAG, getString(R.string.data_not_found_view_created))
                    } else {
                        // Save to viewModel
                        viewModel.saveState(dbData)
                    }
                }
            } else {
                Log.d(TAG, "OnCreateView SavedStateData, populating views")
                setViews(requireContext(), savedData, binding)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Search function
        val searchIcon = binding.searchIcon
        searchIcon.setOnClickListener {
            city = getCity()
            viewModel.searchCurrentWeatherApi(requireContext(), city)
            viewModel.getSearchFromDb(city).observe(viewLifecycleOwner) {
                if (it == null) {
                    Log.d(TAG, getString(R.string.data_not_found_in_search))
                } else {
                    setViews(requireContext(), it, binding)
                    viewModel.saveState(it)
                }
            }
        }
        // Navigation to the Second Fragment
        val navController = Navigation.findNavController(view)

        binding.buttonNext.setOnClickListener {
            val cityString = binding.cityName.text.toString()

            navController.navigate(R.id.action_firstFragment_to_secondFragment, Bundle().apply {
                putString("cityName", cityString)
                putString("latString", lat)
                putString("lonString", lon)
            })
        }

        // History view
        binding.buttonHistory.setOnClickListener {
            navController.navigate(R.id.action_firstFragment_to_historyFragment, Bundle())
        }
    }

    private fun getCity(): String {
        binding.editCity
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
private fun setViews(context: Context, currentWeatherData: CurrentWeatherData, binding: FragmentFirstBinding) {
    val updatedAt = currentWeatherData.dt.toLong()
    val updatedText = SimpleDateFormat(
        "dd/MM/yyyy  HH:mm",
        Locale.ENGLISH
    ).format(
        Date(updatedAt * 1000)
    )
    val icon = currentWeatherData.weather[0].icon

    // For the API call in the SecondFragment
    lat = currentWeatherData.coord.lat.toString()
    lon = currentWeatherData.coord.lon.toString()

    binding.apply {
        cityName.text = context.getString(R.string.text_city, currentWeatherData.name, currentWeatherData.sys.country)
        updatedTime.text = context.getString(R.string.text_updated, updatedText)
        conditions.text = context.getString(R.string.text_conditions, currentWeatherData.weather[0].description)
        temperature.text = context.getString(R.string.text_temp, currentWeatherData.main.temp.toString())
        tempMin.text = context.getString(R.string.text_min_temp, currentWeatherData.main.tempMin.toString())
        tempMax.text = context.getString(R.string.text_max_temp, currentWeatherData.main.tempMax.toString())
        windData.text = context.getString(R.string.text_wind, currentWeatherData.wind.speed.toString())
        humidityData.text = context.getString(R.string.text_humidity, currentWeatherData.main.humidity.toString() + " %")
        pressure.text = context.getString(R.string.text_pressure, currentWeatherData.main.pressure.toString())
    }
    // Image icon
    Glide.with(context)
        .load("https://openweathermap.org/img/wn/$icon@2x.png")
        .into(binding.imageMain)
}

private fun Editable?.formatting(): String {
    return this.toString().trim().capitalize()
}