package com.example.weather.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R

class WeatherHolder: RecyclerView.ViewHolder {
    public lateinit var mIcon: ImageView
    public lateinit var mDate: TextView
    public lateinit var mTemp: TextView
    public lateinit var mDayStatus: TextView
    public lateinit var mNightStatus: TextView

    constructor(item: View) : super(item) {
        mIcon = item.findViewById(R.id.weather_img)
        mDate = item.findViewById(R.id.date)
        mTemp = item.findViewById(R.id.temp)
        mDayStatus = item.findViewById(R.id.day_status)
        mNightStatus = item.findViewById(R.id.night_status)
    }
}