package com.example.myweather

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.databinding.FragmentFirstBinding
import com.example.myweather.network.WeatherWorker
import com.example.myweather.repository.CurrentWeatherRepository
import com.example.myweather.utils.DEFAULT_CITY
import com.example.myweather.viewModels.factories.CurrentModelFactory
import com.example.myweather.viewModels.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "FirstFragment"
private var city = ""
private var lat = ""
private var lon = ""

class FirstFragment : Fragment(R.layout.fragment_first) {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = CurrentWeatherRepository(CurrentWeatherDatabase(requireContext()))
        val factory = CurrentModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        viewModel.getWeatherApiWithWorker(requireContext())
        Log.d(TAG, "OnCreate, doing API call")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        Log.d(TAG, "OnCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workManager = WorkManager.getInstance(requireContext())
        val periodicWorkRequest = PeriodicWorkRequest
            .Builder(WeatherWorker::class.java, 16, TimeUnit.MINUTES)

        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.build().id)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if(it.state == WorkInfo.State.SUCCEEDED) {
                    val stringArray = it.outputData.getStringArray("DATABASE_DATA")
                    Log.d(TAG, "StringArray: $stringArray")
                }
            })



        viewModel.getDataFromDb()?.observe(viewLifecycleOwner) {
            if (it == null) {
                Log.d(TAG, getString(R.string.data_not_found))
            } else {
                Log.d(TAG, "OnViewCreated, populating views")
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
                    conditions.text = getString(R.string.text_conditions, it.weather[0].description)
                    temperature.text = getString(R.string.text_temp, it.main.temp.toString())
                    tempMin.text = getString(R.string.text_min_temp, it.main.tempMin.toString())
                    tempMax.text = getString(R.string.text_max_temp, it.main.tempMax.toString())
                    windData.text = getString(R.string.text_wind, it.wind.speed.toString())
                    humidityData.text =
                        getString(R.string.text_humidity, it.main.humidity.toString() + " %")
                    pressure.text = getString(R.string.text_pressure, it.main.pressure.toString())
                }

                // Image icon
                Glide.with(requireContext())
                    .load("https://openweathermap.org/img/wn/$icon@2x.png")
                    // http://openweathermap.org/img/wn/04d@2x.png
                    .into(binding.imageMain)
            }

        }


        // Search function
        val searchIcon = binding.searchIcon
        searchIcon.setOnClickListener {
            city = getCity()
            // API call and save to db here
            viewModel.searchWeatherApiWithWorker(requireContext(), city)
            viewModel.getSearchFromDb(city)

            viewModel.liveSearchData?.observe(viewLifecycleOwner) {
                Log.d(TAG, "liveSearchData.observe in Search")
                if (it == null) {
                    Log.d(TAG, getString(R.string.data_not_found))
                } else {
                    Log.d(TAG, "Populating views in Search")
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
                        .into(binding.imageMain)
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

private fun Editable?.formatting(): String {
    return this.toString().trim().capitalize()
}