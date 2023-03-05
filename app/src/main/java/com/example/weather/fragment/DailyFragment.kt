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
    val TAG = "sondeptrai"
    var mWeatherList: ArrayList<Weather> = ArrayList()
    private lateinit var mAdapter: DailyAdapter

    private lateinit var binding: FragmentDailyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDailyBinding.inflate(inflater, container, false)

        val pref = PreferenceHelper.getPref(requireContext(), PreferenceHelper.PREF_KEY)
        val cityKeyPref = pref.getString(PreferenceHelper.CITY_KEY, "353412")
        val cityNamePref = pref.getString(PreferenceHelper.CITY_NAME, "Hanoi")
        if (cityKeyPref != null && cityNamePref != null) {
            setRecycleView()
            setData(cityKeyPref, cityNamePref)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setData(cityKey: String?, cityName: String) {
        binding.city.text = cityName
        val requestQueue = Volley.newRequestQueue(context)
        val url = ApiKey.get5Day(cityKey)
        val stringRequest = StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
            override fun onResponse(response: String?) {
                try {
                    Log.d(TAG, "onResponse: DaiLyFragment" + response)
                    mWeatherList.clear()

                    val objAll = JSONObject(response)

                    // item 0
                    // Text condition
                    val headObj = objAll.getJSONObject("Headline")
                    binding.condition.setText(headObj.getString("Text"))

                    val listArr = objAll.getJSONArray("DailyForecasts")

                    val obj0 = listArr.getJSONObject(0)
                    val dayMilis0 = java.lang.Long.valueOf(obj0.getString("EpochDate"))
                    val date0 = Date(dayMilis0 * 1000L)
                    val format0 = SimpleDateFormat("EEEE dd MMM yyyy")
                    val day0 = format0.format(date0)
                    binding.date.text = day0

                    val tempObj0 = obj0.getJSONObject("Temperature")
                    val minObj0 = tempObj0.getJSONObject("Minimum")
                    val maxObj0 = tempObj0.getJSONObject("Maximum")

                    val min0 = ((java.lang.Double.valueOf(minObj0.getString("Value").toString()).toInt() - 32) / 1.8).toInt()
                    val max0 = ((java.lang.Double.valueOf(maxObj0.getString("Value").toString()).toInt() - 32) / 1.8).toInt()

                    binding.tempCondition.text = "$min0°C/$max0°C"

                    for (i in 1..listArr.length() - 1) {
                        val obj = listArr.getJSONObject(i)

                        val dayMilis = java.lang.Long.valueOf(obj.getString("EpochDate"))
                        val date = Date(dayMilis * 1000L)
                        val format = SimpleDateFormat("EEE")
                        val day = format.format(date)

                        val tempObj = obj.getJSONObject("Temperature")
                        val minObj = tempObj.getJSONObject("Minimum")
                        val maxObj = tempObj.getJSONObject("Maximum")

                        val min = ((java.lang.Double.valueOf(minObj.getString("Value").toString()).toInt() - 32) / 1.8).toInt()
                        val max = ((java.lang.Double.valueOf(maxObj.getString("Value").toString()).toInt() - 32) / 1.8).toInt()

                        val temp = "$min°C/$max°C"

                        val dayObj = obj.getJSONObject("Day")
                        val nightObj = obj.getJSONObject("Night")

                        val dayStatus = dayObj.getString("IconPhrase")
                        val nightStatus = nightObj.getString("IconPhrase")
                        var icon = dayObj.getString("Icon")
                        if (icon.length == 1) {
                            icon = "0" + icon
                        }

                        val weather = Weather(day, icon, temp, dayStatus, nightStatus)
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
                    putString(PreferenceHelper.CITY_KEY, "353412")
                    putString(PreferenceHelper.CITY_NAME, "Hanoi")
                    putString(PreferenceHelper.COUNTRY_NAME, "VN")
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

    fun show(cityKey: String, cityName: String) {
        if (cityName != null && cityKey != null) {
            Log.d("sondeptrai", "show: " + cityName + cityKey)
            setRecycleView()
            setData(cityName, cityKey)
        }
    }

//    fun show(lat: String, lon: String, address: String) {
//        val pref = PreferenceHelper.getPref(requireContext(), PreferenceHelper.PREF_KEY)
//        val daysPref = pref.getString(PreferenceHelper.DAYS_KEY, "16")
//
//        val requestQueue = Volley.newRequestQueue(context)
//        val url = ApiKey.getForecastUrlLocale(lat, lon, daysPref)
//        val stringRequest =
//            StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
//                override fun onResponse(response: String?) {
//                    try {
//                        Log.d("sondeptrai", "onResponse: " + response)
//                        val obj = JSONObject(response)
//                        val cityObj = obj.getJSONObject("city")
//                        // City
//                        val city = cityObj.getString("name")
//                        if (city != null && daysPref != null) {
//                            setRecycleView()
//                            setData(city, daysPref)
//                        }
//
//
//                    } catch (e: JSONException) {
//                        throw RuntimeException(e);
//                    }
//                }
//            }, object : Response.ErrorListener {
//                override fun onErrorResponse(error: VolleyError?) {
//                    Log.e(context.toString(), "onErrorResponse: " + error.toString())
//                }
//            })
//        requestQueue.add(stringRequest)
//    }
}