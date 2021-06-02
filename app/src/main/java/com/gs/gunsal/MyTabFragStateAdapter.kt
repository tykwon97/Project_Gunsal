package com.gs.gunsal

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gs.gunsal.fragment.*

class MyTabFragStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Today_Viewer_Fragment()
            1 -> Monthly_Statistics_Fragment()
            2 -> Health_News_Fragment()
            3 -> Stretching_Player_Fragment()
            4 -> Setting_Fragment()
            else -> Today_Viewer_Fragment()
        }
    }



}