package com.example.myweather

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.myweather.model.CurrentWeather
import com.example.myweather.network.ApiService
import com.example.myweather.network.data.CurrentWeatherData
import com.example.myweather.utils.API_KEY
import com.google.android.material.textfield.TextInputEditText
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "FirstFragment"

private var city = "Riga"
private var baseUrlFirst =
    "https://api.openweathermap.org/"

// https://api.openweathermap.org/data/2.5/weather?q=Riga&units=metric&appid=91db09ff13832921fd93739ff0fcc890
private var lat = ""
private var lon = ""

class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        // API call here
        //GetWeather().execute()
        // Search function
        val searchIcon = view.findViewById<ImageView>(R.id.search_icon)
        searchIcon.setOnClickListener {
            //SearchWeather().execute()
            Log.d(TAG, "Search Button clicked")
        }

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlFirst)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)
        // Get network request here
        apiService.getCurrentWeather().enqueue(object : Callback<CurrentWeatherData> {
            override fun onResponse(call: Call<CurrentWeatherData>, response: Response<CurrentWeatherData>) {
                Log.d(TAG, response.toString())
                if (!response.isSuccessful) {
                    Log.d(TAG, "Unsuccessful network call")
                    return
                }
                val body = response.body()!!
                val cityName = body.name
                val pressure = body.main.pressure
                Log.d(TAG, "City Name: $cityName")
                Log.d(TAG, "Pressure: $pressure")
            }
            override fun onFailure(call: Call<CurrentWeatherData>, t: Throwable) {
                Log.d(TAG, t.message ?: "Null message")
            }
        })

        Log.d(TAG, "OnCreateView called")
        return view
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

}