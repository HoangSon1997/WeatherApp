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
import com.example.weather.utils.StringHelper
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TodayFragment : Fragment() {

    private lateinit var binding: FragmentTodayBinding
    val TAG = "sondeptrai TodayFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setView(cityName: String, countryName: String, cityKey: String) {
        setTemperature(cityKey)
        binding.city.text = cityName
        var countryNameEdit: String
        if (cityName.equals("Saigon")) {
            countryNameEdit = "VN"
        } else {
            countryNameEdit = countryName
        }
        binding.country.text = countryNameEdit
        val requestQueue = Volley.newRequestQueue(context)
        val url = ApiKey.get1Day(cityKey)
        val stringRequest =
            StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        Log.d(TAG, "onResponse: " + response)
                        val obj = JSONObject(response)

                        // date, status
                        val headObj = obj.getJSONObject("Headline")
                        val dayMilis =
                            java.lang.Long.valueOf(headObj.getString("EffectiveEpochDate"))
                        val date = Date(dayMilis * 1000L)
                        val format1 = SimpleDateFormat("EEEE dd MMM yyyy")
                        val day = format1.format(date)
                        binding.date.text = "$day, Today"
                        binding.condition.text = headObj.getString("Text")

                        // Min, max
                        val dailyArray = obj.getJSONArray("DailyForecasts")
                        val dailyObj = dailyArray.getJSONObject(0)
                        val tempObj = dailyObj.getJSONObject("Temperature")
                        val minObj = tempObj.getJSONObject("Minimum")
                        val maxObj = tempObj.getJSONObject("Maximum")
                        val min = ((java.lang.Double.valueOf(minObj.getString("Value").toString()).toInt() - 32) / 1.8).toInt()
                        val max = ((java.lang.Double.valueOf(maxObj.getString("Value").toString()).toInt() - 32) / 1.8).toInt()
                        binding.minMaxTemperature!!.text = "$min°C - $max°C"

                        // Category
                        binding.categoryValue!!.text = StringHelper.getUpperCase(headObj.getString("Category"))

                        // Day
                        val dayObj = dailyObj.getJSONObject("Day")
                        binding.dayTemp!!.text = dayObj.getString("IconPhrase")

                        // Night
                        val nightObj = dailyObj.getJSONObject("Night")
                        binding.nightTemp!!.text = nightObj.getString("IconPhrase")

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
    }

    fun setTemperature(cityKey: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val url = ApiKey.getTemp(cityKey)
        Log.d(TAG, "setTemperature: $url")
        val stringRequest = StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    Log.d(TAG, "onResponse: Temp" + response)
                    try {
                        val objArr = JSONArray(response)
                        val obj = objArr.getJSONObject(0)
                        val tempObj = obj.getJSONObject("Temperature")
                        val metricObj = tempObj.getJSONObject("Metric")
                        val temp = (java.lang.Double.valueOf(metricObj.getString("Value").toString())).toInt()
                        binding.tempCondition.text = "$temp°C"

                    } catch (e: JSONException) {
                        throw RuntimeException(e);
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.e(context.toString(), "onErrorResponse: " + error.toString())

                    Toast.makeText(context, "Can't find temp", Toast.LENGTH_SHORT).show()
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayBinding.inflate(inflater, container, false)
        val pref = PreferenceHelper.getPref(requireContext(), PreferenceHelper.PREF_KEY)
        val cityKeyPref = pref.getString(PreferenceHelper.CITY_KEY, "353412")
        val cityNamePref = pref.getString(PreferenceHelper.CITY_NAME, "Hanoi")
        val countryNamePref = pref.getString(PreferenceHelper.COUNTRY_NAME, "VN")
        if (cityKeyPref != null && cityNamePref != null && countryNamePref != null) {
            setView(cityNamePref, countryNamePref, cityKeyPref)
        }
        return binding.root
    }

    fun show(cityName: String, countryName: String, cityKey: String) {
        setView(cityName, countryName, cityKey)
    }

//    fun show(lat: String, lon: String, address: String) {
//        val requestQueue = Volley.newRequestQueue(context)
//        val url = ApiKey.getUrlLocale(lat, lon)
//        val stringRequest =
//            StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
//                override fun onResponse(response: String?) {
//                    try {
//                        Log.d("sondeptrai", "onResponse: " + response)
//                        val obj = JSONObject(response)
//                        // City
//                        val city = obj.getString("name")
//                        setView(city, false)
//
//                        binding.city.text = address
//                        Toast.makeText(context, "Current location: $address", Toast.LENGTH_SHORT).show()
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