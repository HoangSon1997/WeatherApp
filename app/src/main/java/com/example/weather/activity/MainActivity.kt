package com.example.weather.activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.weather.R
import com.example.weather.adapter.ViewPagerAdapter
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.dialog.SearchDialog
import com.example.weather.fragment.DailyFragment
import com.example.weather.fragment.TodayFragment
import com.example.weather.utils.NetworkHelper
import com.example.weather.utils.PreferenceHelper

class MainActivity : AppCompatActivity(), SearchDialog.SaveListener {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "sondeptrai"


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
        var viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(this)
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
        when(item.itemId) {
            R.id.action_search -> launchDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchDialog() {
        val searchDialog = SearchDialog()
        searchDialog.show(supportFragmentManager, "search")
    }

    override fun save(city: String, days: String) {
        Log.d(TAG, "save: " + city + "-" + days)

        val pref = PreferenceHelper.getPref(this, PreferenceHelper.PREF_KEY)
        val editor = pref.edit()
        editor.apply {
            putString(PreferenceHelper.CITY_KEY, city)
            putString(PreferenceHelper.DAYS_KEY, days)
        }.apply()

        passDataToViewPager(city, days)
    }

    private fun getFrag(position: Int) = supportFragmentManager.findFragmentByTag("f${position}")

    private fun passDataToViewPager(city: String, days: String) {
        for (i in 0..1) {
            when(val frag = getFrag(i)) {
                is TodayFragment -> frag.show(city)
                is DailyFragment -> frag.show(city, days)
            }
        }
    }

}