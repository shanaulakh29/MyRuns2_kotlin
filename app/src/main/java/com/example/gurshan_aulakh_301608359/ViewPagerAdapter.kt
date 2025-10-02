package com.example.gurshan_aulakh_301608359

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

//Understood from lecture notes about how to use ViewPagerAdapter
class ViewPagerAdapter(activity: FragmentActivity, var fragmentList: ArrayList<Fragment>) : FragmentStateAdapter(activity){
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}