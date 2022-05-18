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
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import com.example.myweather.databinding.FragmentFirstBinding
import com.example.myweather.network.DatabaseWorker
import com.example.myweather.network.SearchDatabaseWorker
import com.example.myweather.network.currentData.*
import com.example.myweather.utils.DEFAULT_CITY
import com.example.myweather.viewModels.factories.CurrentModelFactory
import com.example.myweather.viewModels.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

private var city = ""
private var lat = ""
private var lon = ""
private var nameString = ""
private var updatedAtString = ""
private var iconString = ""
private var conditionsString = ""
private var temperatureString = ""
private var tempMinString = ""
private var tempMaxString = ""
private var windString = ""
private var humidityString = ""
private var pressureString = ""
private var countryString = ""

private const val TAG = "FirstFragment"

class FirstFragment : Fragment(R.layout.fragment_first) {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        val factory = CurrentModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        viewModel.savedStateData.observe(viewLifecycleOwner) { savedData ->
            if (savedData == null) {
                viewModel.getWeatherWithWorker(requireContext())
                    .observe(viewLifecycleOwner) { workData ->
                        if (workData == null) {
                            return@observe
                        } else {
                            onStateChange(workData, binding)
                        }
                    }
            } else {
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

        // Search function
        val searchIcon = binding.searchIcon
        searchIcon.setOnClickListener {
            city = getCity()

            viewModel.searchWeatherWithWorker(requireContext(), city)
                .observe(viewLifecycleOwner) { workData ->
                    if (workData == null) {
                        Log.d(TAG, "WorkData is null")
                        return@observe
                    } else {
                        onSearchStateChange(workData, binding)
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
        val coord = Coord(lat = lat.toDouble(), lon = lon.toDouble())
        val main = Main(
            humidity = humidityString.toInt(),
            pressure = pressureString.toInt(),
            temp = temperatureString.toDouble(),
            tempMax = tempMaxString.toDouble(),
            tempMin = tempMinString.toDouble()
        )
        val sys = Sys(country = countryString)

        val weather = Weather(description = conditionsString, icon = iconString)
        val weatherList = mutableListOf<Weather>()
        weatherList.add(weather)
        val list: List<Weather> = weatherList.toList()
        val wind = Wind(speed = windString.toDouble())
        val currentWeatherData = CurrentWeatherData(
            coord = coord,
            dt = updatedAtString.toInt(),
            main = main,
            name = nameString,
            sys = sys,
            weather = list,
            wind = wind
        )
        viewModel.saveState(currentWeatherData)
    }

    private fun onStateChange(workData: WorkInfo, binding: FragmentFirstBinding) =
        binding.apply {
            val outPutData = workData.outputData.getStringArray(DatabaseWorker.DB_DATA)
            if (!outPutData.isNullOrEmpty()) {
                setViews(outPutData)
            } else {
                Log.d(TAG, "dataArray is NULL or empty")
            }
        }

    private fun onSearchStateChange(workData: WorkInfo, binding: FragmentFirstBinding) =
        binding.apply {
            val outPutData = workData.outputData.getStringArray(SearchDatabaseWorker.DB_SEARCH_DATA)
            if (!outPutData.isNullOrEmpty()) {
                setViews(outPutData)
            } else {
                Log.d(TAG, "dataArray is NULL or empty")
            }
        }

    private fun setViews(dataArray: Array<String>) {
        nameString = dataArray[0]
        updatedAtString = dataArray[1]
        val updatedAtLong = updatedAtString.toLongOrNull()
        val updatedText = SimpleDateFormat(
            "dd/MM/yyyy  HH:mm",
            Locale.ENGLISH
        ).format(
            Date(updatedAtLong?.times(1000) ?: 0)
        )
        iconString = dataArray[2]
        lat = dataArray[3]
        lon = dataArray[4]
        conditionsString = dataArray[5]
        temperatureString = dataArray[6]
        tempMinString = dataArray[7]
        tempMaxString = dataArray[8]
        windString = dataArray[9]
        humidityString = dataArray[10]
        pressureString = dataArray[11]
        countryString = dataArray[12]

        binding.apply {
            cityName.text = getString(R.string.text_city, nameString, countryString)
            updatedTime.text = getString(R.string.text_updated, updatedText)
            conditions.text = getString(R.string.text_conditions, conditionsString)
            temperature.text = getString(R.string.text_temp, temperatureString)
            tempMin.text = getString(R.string.text_min_temp, tempMinString)
            tempMax.text = getString(R.string.text_max_temp, tempMaxString)
            windData.text = getString(R.string.text_wind, windString)
            humidityData.text =
                getString(R.string.text_humidity, "$humidityString %")
            pressure.text = getString(R.string.text_pressure, pressureString)
        }

        // Image icon
        Glide.with(requireContext())
            .load("https://openweathermap.org/img/wn/$iconString@2x.png")
            // http://openweathermap.org/img/wn/04d@2x.png
            .into(binding.imageMain)

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

//private fun String?.formattingToLong(): Long? {
//    return this?.toInt()?.toLong()
//}