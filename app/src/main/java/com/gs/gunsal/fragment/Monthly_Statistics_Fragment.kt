package com.gs.gunsal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.gs.gunsal.MainActivity
import com.gs.gunsal.R
import com.gs.gunsal.databinding.FragmentMonthlyStatisticsBinding

class Monthly_Statistics_Fragment : Fragment() {

    var binding: FragmentMonthlyStatisticsBinding?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMonthlyStatisticsBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }
    var isPageOpen=false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val translatedown = AnimationUtils.loadAnimation(getContext(), R.anim.translate_down)
        val translateup = AnimationUtils.loadAnimation(getContext(), R.anim.translate_up)

        binding!!.apply {
            monthlyTextColor.bringToFront()
            monthly
            monthly.setOnDateChangeListener { view, year, month, dayOfMonth ->
                //클릭한 날짜 받아오기
                todayTranslateUpDateWalk.text=year.toString()+"."+month.toString()+"."+dayOfMonth.toString()
                if(!isPageOpen){
                    isPageOpen=true
                    (activity as MainActivity).tabbargone() //탭바사라지게게
                    monthly.visibility=View.GONE
                    slideViewer.visibility=View.VISIBLE
                    todayBlackBackground.visibility=View.VISIBLE
                    monthlyTextColor.visibility=View.GONE
                    slideViewer.startAnimation(translateup)
                }
            }
            barchartAccept.setOnClickListener{
                if(isPageOpen){
                    slideViewer.visibility=View.GONE
                    todayBlackBackground.visibility=View.GONE
                    //slideViewer.startAnimation(translatedown)
                    (activity as MainActivity).tabbarvisible()
                    isPageOpen=false
                    monthly.visibility=View.VISIBLE
                    monthlyTextColor.visibility=View.VISIBLE
                }
            }

            monthly.setOnClickListener {
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}