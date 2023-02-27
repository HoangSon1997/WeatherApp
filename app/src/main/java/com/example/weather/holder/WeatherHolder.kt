package com.example.weather.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R

class WeatherHolder: RecyclerView.ViewHolder {
    public lateinit var mIcon: ImageView
    public lateinit var mDay: TextView
    public lateinit var mTemp: TextView
    public lateinit var mCondition: TextView

    constructor(item: View) : super(item) {
        mIcon = item.findViewById(R.id.weather_img)
        mDay = item.findViewById(R.id.day)
        mTemp = item.findViewById(R.id.temp)
        mCondition = item.findViewById(R.id.desc)
    }
}