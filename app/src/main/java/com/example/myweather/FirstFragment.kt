package com.example.myweather

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.example.myweather.database.currentDatabase.CurrentWeatherDatabase
import com.example.myweather.databinding.FragmentFirstBinding
import com.example.myweather.network.DatabaseWorker
import com.example.myweather.network.WeatherWorker
import com.example.myweather.network.currentData.*
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
private var icon = ""

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
                viewModel.getWeatherApiWithWorker(requireContext())
                viewModel.getDbDataWithWorker(requireContext()).observe(viewLifecycleOwner) { workData ->
                //viewModel.outputWorkInfo.observe(viewLifecycleOwner) { workData ->
                    if (workData != null) {
                        Log.d(TAG, "Workdata is not null")
                        onStateChange(workData, binding)
                    }
                }
            } else {
                Log.d(TAG, "OnViewCreated, SavedStateData, populating views")
                val updatedAt = savedData.dt.toLong()
                val updatedText = SimpleDateFormat(
                    "dd/MM/yyyy  HH:mm",
                    Locale.ENGLISH
                ).format(
                    Date(updatedAt * 1000)
                )
                val icon = savedData.weather[0].icon
                binding.apply {

                    cityName.text =
                        getString(R.string.text_city, savedData.name, savedData.sys.country)
                    updatedTime.text = getString(R.string.text_updated, updatedText)
                    conditions.text =
                        getString(R.string.text_conditions, savedData.weather[0].description)
                    temperature.text = getString(R.string.text_temp, savedData.main.temp.toString())
                    tempMin.text =
                        getString(R.string.text_min_temp, savedData.main.tempMin.toString())
                    tempMax.text =
                        getString(R.string.text_max_temp, savedData.main.tempMax.toString())
                    windData.text = getString(R.string.text_wind, savedData.wind.speed.toString())
                    humidityData.text =
                        getString(R.string.text_humidity, savedData.main.humidity.toString() + " %")
                    pressure.text =
                        getString(R.string.text_pressure, savedData.main.pressure.toString())
                }
                Glide.with(requireContext())
                    .load("https://openweathermap.org/img/wn/$icon@2x.png")
                    .into(binding.imageMain)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val workManager = WorkManager.getInstance(requireContext())
//        val periodicWorkRequest = PeriodicWorkRequest
//            .Builder(WeatherWorker::class.java, 16, TimeUnit.MINUTES)
//
////        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.build().id)
////            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
////                if(it.state == WorkInfo.State.SUCCEEDED) {
////                    val stringArray = it.outputData.getStringArray(WeatherWorker.DATABASE_DATA)
////                    Log.d(TAG, "StringArray: $stringArray")
////                }
////            })
//        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.build().id)
//            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//                it.state.name
//                if (it.state.isFinished) {
//                    val array = it.outputData.getStringArray(WeatherWorker.DATABASE_DATA)
//                    Log.d(TAG, "StringArray: $array")
//                }
//            })
//
//        viewModel.getDataFromDb()?.observe(viewLifecycleOwner) {
//            if (it == null) {
//                Log.d(TAG, getString(R.string.data_not_found))
//            } else {
//                Log.d(TAG, "OnViewCreated, populating views")
//                val updatedAt = it.dt.toLong()
//                val updatedText = SimpleDateFormat(
//                    "dd/MM/yyyy  HH:mm",
//                    Locale.ENGLISH
//                ).format(
//                    Date(updatedAt * 1000)
//                )
//                val icon = it.weather[0].icon
//
//                // For the API call in the SecondFragment
//                lat = it.coord.lat.toString()
//                lon = it.coord.lon.toString()
//
//                binding.apply {
//                    cityName.text = getString(R.string.text_city, it.name, it.sys.country)
//                    updatedTime.text = getString(R.string.text_updated, updatedText)
//                    conditions.text = getString(R.string.text_conditions, it.weather[0].description)
//                    temperature.text = getString(R.string.text_temp, it.main.temp.toString())
//                    tempMin.text = getString(R.string.text_min_temp, it.main.tempMin.toString())
//                    tempMax.text = getString(R.string.text_max_temp, it.main.tempMax.toString())
//                    windData.text = getString(R.string.text_wind, it.wind.speed.toString())
//                    humidityData.text =
//                        getString(R.string.text_humidity, it.main.humidity.toString() + " %")
//                    pressure.text = getString(R.string.text_pressure, it.main.pressure.toString())
//                }
//
//                // Image icon
//                Glide.with(requireContext())
//                    .load("https://openweathermap.org/img/wn/$icon@2x.png")
//                    // http://openweathermap.org/img/wn/04d@2x.png
//                    .into(binding.imageMain)
//            }



        // Search function
        val searchIcon = binding.searchIcon
        searchIcon.setOnClickListener {
            city = getCity()
            // API call and save to db here
            viewModel.searchWeatherApiWithWorker(requireContext(), city)

            viewModel.getSearchFromDb(city)?.observe(viewLifecycleOwner) {
                Log.d(TAG, "getSearchFromDb(city)")
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

    override fun onStop() {
        super.onStop()
        val name = binding.cityName.formatTextView()
        val updated = binding.updatedTime.formatTextView()
        val conditions = binding.conditions.formatTextView()
        val temp = binding.temperature.formatTextView()
        val tempMin = binding.tempMin.formatTextView()
        val tempMax = binding.tempMax.formatTextView()
        val windSpeed = binding.windData.formatTextView()
        val humidity = binding.humidityData.formatTextView()
        val pressure = binding.pressure.formatTextView()

        val coord = Coord(lat = lat.toDouble(), lon = lon.toDouble())
        val main = Main(feelsLike = temp.toDouble(), humidity = humidity.toInt(), pressure = pressure.toInt(), temp = temp.toDouble(), tempMax = tempMax.toDouble(), tempMin = tempMin.toDouble())
        val sys = Sys(country = name)

        val weather = Weather(description = conditions, icon = icon)
        val weatherList = mutableListOf<Weather>()
        weatherList.add(weather)
        val list: List<Weather> = weatherList.toList()
        val wind = Wind(speed = windSpeed.toDouble())
        val currentWeatherData = CurrentWeatherData(coord = coord, dt = updated.toInt(), main = main, name = name, sys = sys, visibility = 0, weather = list, wind = wind)

        viewModel.saveState(currentWeatherData)
    }

    private fun onStateChange(workData: WorkInfo, binding: FragmentFirstBinding) =
        binding.apply {
            Log.d(TAG, "State: ${workData.state}")
            Log.d(TAG, "Progress: ${workData.progress}")
            val outPutData = workData.outputData.getStringArray(DatabaseWorker.DB_DATA)
            Log.d(TAG, "outPutData size: ${outPutData?.size}")
            //setViews(outPutData)
        }
    private fun setViews(dataArray: Array<String>?) {
        val name = dataArray?.get(0)
        Log.d(TAG, "dataArray.name: $name")
        val updatedText = dataArray?.get(1)
//        val updatedAt = dataArray.dt.toLong()
//                val updatedText = SimpleDateFormat(
//                    "dd/MM/yyyy  HH:mm",
//                    Locale.ENGLISH
//                ).format(
//                    Date(updatedAt * 1000)
//                )
//                val icon = it.weather[0].icon
//
//                // For the API call in the SecondFragment
//                lat = it.coord.lat.toString()
//                lon = it.coord.lon.toString()

                binding.apply {
                    cityName.text = getString(R.string.text_city, name, "LV")
                    updatedTime.text = getString(R.string.text_updated, updatedText)
//                    conditions.text = getString(R.string.text_conditions, it.weather[0].description)
//                    temperature.text = getString(R.string.text_temp, it.main.temp.toString())
//                    tempMin.text = getString(R.string.text_min_temp, it.main.tempMin.toString())
//                    tempMax.text = getString(R.string.text_max_temp, it.main.tempMax.toString())
//                    windData.text = getString(R.string.text_wind, it.wind.speed.toString())
//                    humidityData.text =
//                        getString(R.string.text_humidity, it.main.humidity.toString() + " %")
//                    pressure.text = getString(R.string.text_pressure, it.main.pressure.toString())
                }

                // Image icon
//                Glide.with(requireContext())
//                    .load("https://openweathermap.org/img/wn/$icon@2x.png")
//                    // http://openweathermap.org/img/wn/04d@2x.png
//                    .into(binding.imageMain)
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

private fun TextView?.formatTextView(): String =
    this?.text.toString()