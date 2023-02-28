package com.example.weather.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.adapter.DailyAdapter
import com.example.weather.databinding.FragmentDailyBinding
import com.example.weather.entity.Weather
import com.example.weather.utils.ApiKey
import com.example.weather.utils.PreferenceHelper
import com.example.weather.utils.StringHelper
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DailyFragment : Fragment() {
    var mWeatherList: ArrayList<Weather> = ArrayList()
    private lateinit var mAdapter: DailyAdapter

    private lateinit var binding: FragmentDailyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDailyBinding.inflate(inflater, container, false)

        val pref = PreferenceHelper.getPref(requireContext(), PreferenceHelper.PREF_KEY)
        val cityPref = pref.getString(PreferenceHelper.CITY_KEY, "Hanoi")
        val daysPref = pref.getString(PreferenceHelper.DAYS_KEY, "16")
        if (cityPref != null && daysPref != null) {
            setRecycleView()
            setData(cityPref, daysPref)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setData(cityPref: String?, daysPref: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = ApiKey.getForecastUrl(cityPref, daysPref)
        val stringRequest = StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
            override fun onResponse(response: String?) {
                try {
                    mWeatherList.clear()

                    val obj = JSONObject(response)

                    val cityObj = obj.getJSONObject("city")
                    binding.city.setText(cityObj.getString("name"))

                    val listArr = obj.getJSONArray("list")

                    val obj1 = listArr.getJSONObject(1)
                    val dayMilis1 = java.lang.Long.valueOf(obj1.getString("dt"))
                    val date1 = Date(dayMilis1 * 1000L)
                    val format1 = SimpleDateFormat("EEEE dd MMM yyyy")
                    val day1 = format1.format(date1)
                    binding.date.text = day1
                    val weatherArr1 = obj1.getJSONArray("weather")
                    val weatherObj1 = weatherArr1.getJSONObject(0)
                    binding.condition.text = StringHelper.getUpperCase(weatherObj1.getString("description"))
                    Picasso.with(context).load(ApiKey.getIconUrl(weatherObj1.getString("icon"))).into(binding.weatherResource)
                    val tempObj1 = obj1.getJSONObject("temp")
                    var temp1 = (java.lang.Double.valueOf(tempObj1.getString("day").toString())).toInt() - 273

                    binding.tempCondition.text = "$temp1°C"

                    for (i in 2..listArr.length() - 1) {
                        val obj = listArr.getJSONObject(i)
                        val weatherArr = obj.getJSONArray("weather")
                        val weatherObj = weatherArr.getJSONObject(0)
                        val icon = weatherObj.getString("icon")
                        val desc = weatherObj.getString("description")

                        val dayMilis = java.lang.Long.valueOf(obj.getString("dt"))
                        val date = Date(dayMilis * 1000L)
                        val format = SimpleDateFormat("EEE")
                        val day = format.format(date)

                        val tempObj = obj.getJSONObject("temp")
                        var min = (java.lang.Double.valueOf(tempObj.getString("min").toString())).toInt() - 273
                        var max = (java.lang.Double.valueOf(tempObj.getString("max").toString())).toInt() -273

                        val temp = "$min°C/$max°C"

                        val weather = Weather(day, StringHelper.getUpperCase(desc), icon, temp)
                        mWeatherList.add(weather)
                    }
                    mAdapter.notifyDataSetChanged()
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
        mAdapter.notifyDataSetChanged()
    }

    private fun setRecycleView() {

        mAdapter = DailyAdapter(requireContext(), mWeatherList)
        binding.recyclerview.adapter = mAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
    }

    fun show(city: String, days: String) {
        if (city != null && days != null) {
            Log.d("sondeptrai", "show: " + city + days)
            setRecycleView()
            setData(city, days)
        }
    }

    fun show(lat: String, lon: String, address: String) {
        val pref = PreferenceHelper.getPref(requireContext(), PreferenceHelper.PREF_KEY)
        val daysPref = pref.getString(PreferenceHelper.DAYS_KEY, "16")

        val requestQueue = Volley.newRequestQueue(context)
        val url = ApiKey.getForecastUrlLocale(lat, lon, daysPref)
        val stringRequest =
            StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        Log.d("sondeptrai", "onResponse: " + response)
                        val obj = JSONObject(response)
                        val cityObj = obj.getJSONObject("city")
                        // City
                        val city = cityObj.getString("name")
                        if (city != null && daysPref != null) {
                            setRecycleView()
                            setData(city, daysPref)
                        }


                    } catch (e: JSONException) {
                        throw RuntimeException(e);
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.e(context.toString(), "onErrorResponse: " + error.toString())
                }
            })
        requestQueue.add(stringRequest)
    }
}