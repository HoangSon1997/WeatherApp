package com.example.weather.activity

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.R
import com.example.weather.adapter.ViewPagerAdapter
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.dialog.SearchDialog
import com.example.weather.fragment.DailyFragment
import com.example.weather.fragment.TodayFragment
import com.example.weather.utils.ApiKey
import com.example.weather.utils.NetworkHelper
import com.example.weather.utils.PreferenceHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), SearchDialog.SaveListener {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "sondeptrai"
    private lateinit var mFunsedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_CODE = 100;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigationBar()
        setViewPager()

        if (!NetworkHelper.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show()
        }
    }

    private fun setBottomNavigationBar() {
        binding.bottomNav.setOnNavigationItemSelectedListener { it ->
            when (it.itemId) {
                R.id.today_menu -> binding.viewPager.currentItem = 0
                R.id.daily_menu -> binding.viewPager.currentItem = 1
                R.id.about_menu -> binding.viewPager.currentItem = 2
                else -> binding.viewPager.currentItem = 0
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun setViewPager() {
        var viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> binding.bottomNav.menu.findItem(R.id.today_menu).setChecked(true)
                    1 -> binding.bottomNav.menu.findItem(R.id.daily_menu).setChecked(true)
                    2 -> binding.bottomNav.menu.findItem(R.id.about_menu).setChecked(true)
                    else -> binding.bottomNav.menu.findItem(R.id.today_menu).setChecked(true)
                }
            }
        })
        binding.viewPager.offscreenPageLimit = 3
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> launchDialog()
            R.id.action_map -> launchMap()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchDialog() {
        val searchDialog = SearchDialog()
        searchDialog.show(supportFragmentManager, "search")
    }

    override fun save(city: String) {
        Log.d(TAG, "save: " + city)
        getCityKey(city)
    }

    private fun getCityKey(city: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = ApiKey.getCityKey(city)
        val stringRequest =
            StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        Log.d("sondeptrai", "onResponse: " + response)
                        val objArray = JSONArray(response)
                        if (objArray.length() == 0) {
                            Toast.makeText(applicationContext, "Can't find this city", Toast.LENGTH_SHORT).show()
                            return
                        }
                        val obj = objArray.getJSONObject(0)

                        val cityName = obj.getString("LocalizedName")
                        val countryObj = obj.getJSONObject("Country")
                        val countryName = countryObj.getString("LocalizedName")
                        val cityKey = obj.getString("Key")

                        val pref = PreferenceHelper.getPref(applicationContext, PreferenceHelper.PREF_KEY)
                        val editor = pref.edit()
                        editor.apply {
                            putString(PreferenceHelper.CITY_KEY, cityKey)
                            putString(PreferenceHelper.CITY_NAME, cityName)
                            putString(PreferenceHelper.COUNTRY_NAME, countryName)
                        }.apply()

                        passDataToViewPager(cityName, countryName, cityKey)
                    } catch (e: JSONException) {
                        throw RuntimeException(e);
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.e(this.toString(), "onErrorResponse: " + error.toString())
                }
            })
        requestQueue.add(stringRequest)
    }

    private fun getFrag(position: Int) = supportFragmentManager.findFragmentByTag("f${position}")

    private fun passDataToViewPager(cityName: String, countryName: String, cityKey: String) {
        for (i in 0..1) {
            when (val frag = getFrag(i)) {
                is TodayFragment -> frag.show(cityName, countryName, cityKey)
                is DailyFragment -> frag.show(cityName, cityKey)
            }
        }
    }

    private fun launchMap() {
        if (!NetworkHelper.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show()
            return
        }

        mFunsedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFunsedLocationProviderClient.getLastLocation().addOnSuccessListener {
                if (it != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    Log.d(TAG, "launchMap: " + (address!![0].latitude) + "-" + address[0].longitude + "-" + address!![0].getAddressLine(0))

                    setDataFromLocation(address!![0].latitude.toString(), address[0].longitude.toString())
                }
            }
        } else {
            askPermission()
        }
    }

    fun setDataFromLocation(lat: String, lon: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = ApiKey.getUrlLocale(lat, lon)
        val stringRequest =
            StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        Log.d("sondeptrai", "onResponse: Location" + response)
                        val obj = JSONObject(response)
                        val cityObj = obj.getJSONObject("ParentCity")
                        val cityName = cityObj.getString("LocalizedName")
                        val cityKey = cityObj.getString("Key")

                        val countryObj = obj.getJSONObject("Country")
                        val countryName = countryObj.getString("LocalizedName")

                        val pref = PreferenceHelper.getPref(applicationContext, PreferenceHelper.PREF_KEY)
                        val editor = pref.edit()
                        editor.apply {
                            putString(PreferenceHelper.CITY_KEY, cityKey)
                            putString(PreferenceHelper.CITY_NAME, cityName)
                            putString(PreferenceHelper.COUNTRY_NAME, countryName)
                        }.apply()

                        passDataToViewPager(cityName, countryName, cityKey)
                    } catch (e: JSONException) {
                        throw RuntimeException(e);
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.e(this.toString(), "onErrorResponse: " + error.toString())
                }
            })
        requestQueue.add(stringRequest)
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchMap()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}