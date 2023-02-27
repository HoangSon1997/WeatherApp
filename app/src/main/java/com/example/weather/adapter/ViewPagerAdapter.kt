package com.example.weather.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weather.fragment.AboutFragment
import com.example.weather.fragment.DailyFragment
import com.example.weather.fragment.TodayFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    var fragList = arrayListOf(TodayFragment(), DailyFragment(), AboutFragment())

    override fun getItemCount(): Int = fragList.size


    override fun createFragment(position: Int): Fragment = fragList[position]
}