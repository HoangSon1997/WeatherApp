package com.example.weather.utils

object StringHelper {
    fun getUpperCase(s: String?): String {
        return s!!.substring(0, 1).uppercase() + s.substring(1)
    }
}