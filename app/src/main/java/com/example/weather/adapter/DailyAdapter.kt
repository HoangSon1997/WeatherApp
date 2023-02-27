package com.example.weather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.*
import com.example.weather.R
import com.example.weather.entity.Weather
import com.example.weather.holder.WeatherHolder
import com.example.weather.utils.ApiKey
import com.squareup.picasso.Picasso

class DailyAdapter() : Adapter<WeatherHolder>() {

    lateinit var mContext: Context
    lateinit var mList: ArrayList<Weather>

    constructor(mContext: Context, mList: ArrayList<Weather>) : this() {
        this.mContext = mContext
        this.mList = mList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
        val inflater = LayoutInflater.from(mContext)
        val view: View = inflater.inflate(R.layout.daily_items, parent, false)
        return WeatherHolder(view)
    }

    override fun getItemCount(): Int =  mList.size

    override fun onBindViewHolder(holder: WeatherHolder, position: Int) {
        Picasso.with(mContext).load(ApiKey.getIconUrl(mList.get(position).icon)).into(holder.mIcon)
        holder.mDay.text = mList.get(position).day
        holder.mTemp.text = mList.get(position).temp
        holder.mCondition.text = mList.get(position).status
    }
}