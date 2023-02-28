package com.example.weather.utils

object ApiKey {
    val KEY = "edbb4b34f94bc7481fc8bed648b91f18"
    fun getUrlCity(city: String?): String = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + KEY
    fun getIconUrl(icon: String?): String = "https://openweathermap.org/img/wn/$icon.png"
    fun getForecastUrl(city: String?, days: String?): String = "https://api.openweathermap.org/data/2.5/forecast/daily?q=$city&cnt=$days&appid=$KEY"

    fun getUrlLocale(lat: String?, lon: String?): String = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$KEY"
    fun getForecastUrlLocale(lat: String?, lon: String?, days: String?): String = "https://api.openweathermap.org/data/2.5/forecast/daily?lat=$lat&lon=$lon&cnt=$days&appid=$KEY"
}