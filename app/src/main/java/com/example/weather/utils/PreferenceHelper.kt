package com.example.weather.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    val PREF_KEY = "shared_preference"
    val CITY_KEY = "city_preference"
    val DAYS_KEY = "days_preference"

    fun getPref(context: Context, key: String): SharedPreferences {
        val pref: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return pref
    }

}