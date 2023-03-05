package com.example.weather.entity

class Weather {
    var date: String? = null
    var icon: String? = null
    var temp: String? = null
    var day_status: String? = null
    var night_status: String? = null

    constructor(
        date: String?,
        icon: String?,
        temp: String?,
        day_status: String?,
        night_status: String?
    ) {
        this.date = date
        this.icon = icon
        this.temp = temp
        this.day_status = day_status
        this.night_status = night_status
    }
}