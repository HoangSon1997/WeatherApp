package com.example.weather.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.utils.ApiKey
import com.example.weather.databinding.FragmentTodayBinding
import com.example.weather.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TodayFragment : Fragment() {

    private lateinit var binding: FragmentTodayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref = PreferenceHelper.getPref(requireContext(), PreferenceHelper.PREF_KEY)
        val cityPref = pref.getString(PreferenceHelper.CITY_KEY, "Hanoi")
        if (cityPref != null) {
            setView(cityPref)
        }
    }

    private fun setView(cityPref: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = ApiKey.getUrlCity(cityPref)
        val stringRequest = StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
            override fun onResponse(response: String?) {
                try {
                    Log.d("sondeptrai", "onResponse: " + response)
                    val obj = JSONObject(response)
                    // City
                    binding.city.text = obj.getString("name")

                    // Country
                    val sysObj = obj.getJSONObject("sys")
                    binding.country.setText(sysObj.getString("country"))

                    // Date
                    val dayMilis = java.lang.Long.valueOf(obj.getString("dt"))
                    val date = Date(dayMilis * 1000L)
                    val format = SimpleDateFormat("EEEE dd MMM yyyy")
                    val day = format.format(date)
                    binding.date.setText(day)

                    // Icon, Condition
                    val weatherJsonArr = obj.getJSONArray("weather")
                    val weatherJsonObj = weatherJsonArr.getJSONObject(0)
                    val condition = weatherJsonObj.getString("description")
                    val icon = weatherJsonObj.getString("icon")
                    Picasso.with(context)
                        .load(ApiKey.getIconUrl(icon)).into(binding.weatherResource)
                    binding.condition.text = condition

                    // Temparature, humidity, feelikes, temp min - max
                    val mainJsonObj = obj.getJSONObject("main")
                    val temp = java.lang.Double.valueOf(mainJsonObj.getString("temp")).toInt().toString()
                    val humidity = mainJsonObj.getString("humidity")
                    binding.tempCondition.setText("$temp°C")
                    binding.humidityValue.setText("$humidity%")
                    val feelsLike = mainJsonObj.getString("feels_like")
                    binding.temperature.setText("$feelsLike°C Today")
                    val tempMin = java.lang.Double.valueOf(mainJsonObj.getString("temp_min")).toInt()
                    var tempMax = java.lang.Double.valueOf(mainJsonObj.getString("temp_max")).toInt()
                    if (tempMax == tempMin) {
                        tempMax = tempMin + 3
                    }
                    binding.tempValue.text = "$tempMin°C - $tempMax°C"

                    // wind
                    val windObj = obj.getJSONObject("wind")
                    val speedWind = windObj.getString("speed")
                    binding.windValue.text = "$speedWind m/s"

                } catch (e: JSONException) {
                    throw RuntimeException(e);
                }
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                Log.e(context.toString(), "onErrorResponse: " + error.toString())

                Toast.makeText(context, "Can't find city", Toast.LENGTH_SHORT).show()
                val pref = PreferenceHelper.getPref(requireContext(), PreferenceHelper.PREF_KEY)
                val editor = pref.edit()
                editor.apply {
                    putString(PreferenceHelper.CITY_KEY, "Hanoi")
                    putString(PreferenceHelper.DAYS_KEY, "16")
                }.apply()
            }
        })
        requestQueue.add(stringRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun show(city: String) {
        setView(city)
    }
}