package com.example.weather.utils

object ApiKey {
    val KEY = "v0ZzlTam0fujdlmS51bLAY8ZqdPa3F2x"
    fun getCityKey(city: String?): String = "https://dataservice.accuweather.com/locations/v1/cities/search?apikey=$KEY&q=$city"
    fun getTemp(cityKey: String?): String = "https://dataservice.accuweather.com/currentconditions/v1/$cityKey?apikey=$KEY"
    fun get1Day(cityKey: String?): String = "https://dataservice.accuweather.com/forecasts/v1/daily/1day/$cityKey?language=en&apikey=$KEY"
    fun get5Day(cityKey: String?): String = "https://dataservice.accuweather.com/forecasts/v1/daily/5day/$cityKey?language=en&apikey=$KEY"
    fun getIconUrl(icon: String?): String = "https://developer.accuweather.com/sites/default/files/$icon-s.png"
    fun getUrlLocale(lat: String?, lon: String?): String = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=$KEY&q=$lat%2C$lon"
}