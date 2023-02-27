package com.example.weather.utils

object ApiKey {
    val KEY = "edbb4b34f94bc7481fc8bed648b91f18"
    fun getUrlCity(city: String?): String = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + KEY
    fun getIconUrl(icon: String?): String = "https://openweathermap.org/img/wn/$icon.png"
    fun getForecastUrl(city: String?, days: String?): String = "https://api.openweathermap.org/data/2.5/forecast/daily?q=$city&cnt=$days&appid=$KEY"
}