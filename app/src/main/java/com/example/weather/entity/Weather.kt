package com.example.weather.entity

class Weather {
    var day: String? = null
    var status: String? = null
    var icon: String? = null
    var temp: String? = null

    constructor(day: String?, status: String?, icon: String?, min: String?) {
        this.day = day
        this.status = status
        this.icon = icon
        this.temp = min
    }
}