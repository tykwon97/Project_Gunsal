package com.example.project_gunsal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project_gunsal.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    val textarr = arrayListOf<String>("오늘의기록", "월간통계", "건강뉴스", "스트레칭", "설정")
    val iconarr = arrayListOf<Int>(R.drawable.ic_home_select, R.drawable.ic_monthly, R.drawable.ic_news, R.drawable.ic_category, R.drawable.ic_setting)

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()



    }

    private fun init() {
        binding.viewPager.adapter = MyTabFragStateAdapter(this)

        TabLayoutMediator(binding.myTabIconview, binding.viewPager){
                tab, position ->
            tab.text = textarr[position]
            tab.setIcon(iconarr[position])
        }.attach() //꼭 attach해야함.
    }
}