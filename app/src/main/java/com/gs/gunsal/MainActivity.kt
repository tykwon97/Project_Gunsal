package com.gs.gunsal

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gs.gunsal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val textarr = arrayListOf<String>("오늘의기록", "월간통계", "건강뉴스", "스트레칭", "설정")
    val iconarr = arrayListOf<Int>(R.drawable.ic_home, R.drawable.ic_monthly, R.drawable.ic_news, R.drawable.ic_category, R.drawable.ic_setting)
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()


    }
    private fun init() {
        binding.viewPager.adapter = MyTabFragStateAdapter(this)
        initIconColor()

        TabLayoutMediator(binding.myTabIconview, binding.viewPager){
                tab, position ->
            tab.text = textarr[position]
            tab.setIcon(iconarr[position])
        }.attach() //꼭 attach해야함.


    }
    fun initIconColor(){
        binding.myTabIconview.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var tabiconColor = ContextCompat.getColor(applicationContext, R.color.select_color)
                tab?.icon?.setColorFilter(tabiconColor, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                var tabiconColor = ContextCompat.getColor(applicationContext, R.color.unselect_color)
                tab?.icon?.setColorFilter(tabiconColor, PorterDuff.Mode.SRC_IN)

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

        })
    }
}