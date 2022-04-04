package com.example.myweather.network

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.net.URL

private const val TAG = "ApiService"
//private val API_Key = "91db09ff13832921fd93739ff0fcc890"
//var LAT = "57"
//var LON = "24.0833"

interface ApiService {


    // For SecondFragment
//    class getWeatherByDate() : AsyncTask<String, Void, String>() {
//
//        val BASE_URL =
//            "api.openweathermap.org/data/2.5/forecast?lat=$LAT&lon=$LON&appid=$API_Key"
//
//        override fun doInBackground(vararg params: String?): String? {
//            var response: String?
//            try {
//                response = URL(BASE_URL).readText(
//                    Charsets.UTF_8
//                )
//                Log.d(TAG, "doInBackground Called")
//            } catch (e: Exception) {
//                response = null
//            }
//            return response
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            try {
//                /* Extracting JSON from the API */
//                val jsonObj = JSONObject(result)
//                val main = jsonObj.getJSONObject("main")
////                val wind = jsonObj.getJSONObject("wind")
////                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
////                val dateText: Long = jsonObj.getLong("dt")
////
////                val dateTextFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(
////                    Date(dateText * 1000))
////                val temp = main.getString("temp") + "°C"
////                val windSpeed = wind.getString("speed")
////                val iconId = weather.getString("icon")
//
//                Log.d(TAG, "jsonObj: ${jsonObj}")
//                Log.d(TAG, "main: $main")
////                Log.d(TAG, "onPostExecute Called")
////                Log.d(TAG, "dateTextFormatted: $dateTextFormatted")
////                Log.d(TAG, "temp: $temp")
////                Log.d(TAG, "windSpeed: $windSpeed")
////                Log.d(TAG, "iconId: $iconId")
//            } catch (e: Exception) {
//                Log.d(TAG, "Exception onPostExecute: $e")
//            }
//        }
//    }
}