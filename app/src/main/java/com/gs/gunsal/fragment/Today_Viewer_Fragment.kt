package com.gs.gunsal.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.gs.gunsal.FirebaseRepository
import com.gs.gunsal.MainActivity
import com.gs.gunsal.R
import com.gs.gunsal.databinding.FragmentTodayViewerBinding
import kotlin.math.floor

class Today_Viewer_Fragment : Fragment() {

    var binding: FragmentTodayViewerBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentTodayViewerBinding.inflate(layoutInflater, container, false)
        binding!!.todayWaterImage.setOnClickListener {




        }
        return binding!!.root
    }

    var isPageOpenWater = false
    var isPageOpenWalk = false
    var isPageOpenStrech = false
    override fun onPause() {
        super.onPause()
        val translatedown = AnimationUtils.loadAnimation(getContext(), R.anim.translate_down)
        binding!!.apply {
            if (isPageOpenStrech) {
                slideViewerStrech.visibility = View.GONE
                todayBlackBackground.visibility = View.GONE
                slideViewerStrech.startAnimation(translatedown)
                (activity as MainActivity).tabbarvisible()
                isPageOpenStrech = false
            }
            if (isPageOpenWater) {

                slideViewerWater.visibility = View.GONE
                todayBlackBackground.visibility = View.GONE
                slideViewerWater.startAnimation(translatedown)
                (activity as MainActivity).tabbarvisible()
                isPageOpenWater = false
            }
            if (isPageOpenWalk) {

                slideViewerWalk.visibility = View.GONE
                todayBlackBackground.visibility = View.GONE
                slideViewerWalk.startAnimation(translatedown)
                (activity as MainActivity).tabbarvisible()
                isPageOpenWalk = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()

        binding!!.apply {

            val translatedown = AnimationUtils.loadAnimation(getContext(), R.anim.translate_down)
            val translateup = AnimationUtils.loadAnimation(getContext(), R.anim.translate_up)

            todayWater.setOnClickListener {
                if (!isPageOpenStrech && !isPageOpenWalk) {
                    if (!isPageOpenWater) {
                        isPageOpenWater = true
                        setChartViewWater(view)
                        (activity as MainActivity).tabbargone() //탭바사라지게게
                        slideViewerWater.visibility = View.VISIBLE
                        todayBlackBackground.visibility = View.VISIBLE
                        slideViewerWater.startAnimation(translateup)
                    }
                }

            }
            barchartAcceptWater.setOnClickListener {
                if (isPageOpenWater) {

                    slideViewerWater.visibility = View.GONE
                    todayBlackBackground.visibility = View.GONE
                    slideViewerWater.startAnimation(translatedown)
                    (activity as MainActivity).tabbarvisible()
                    isPageOpenWater = false
                }
            }



            todayWalk.setOnClickListener {
                if (!isPageOpenStrech && !isPageOpenWater) {
                    if (!isPageOpenWalk) {
                        isPageOpenWalk = true
                        setChartViewWalk(view)
                        (activity as MainActivity).tabbargone() //탭바사라지게게
                        slideViewerWalk.visibility = View.VISIBLE
                        todayBlackBackground.visibility = View.VISIBLE
                        slideViewerWalk.startAnimation(translateup)
                    }
                }

            }
            barchartAcceptWalk.setOnClickListener {
                if (isPageOpenWalk) {

                    slideViewerWalk.visibility = View.GONE
                    todayBlackBackground.visibility = View.GONE
                    slideViewerWalk.startAnimation(translatedown)
                    (activity as MainActivity).tabbarvisible()
                    isPageOpenWalk = false
                }
            }




            todayStrech.setOnClickListener {
                if (!isPageOpenWalk && !isPageOpenWater) {

                    if (!isPageOpenStrech) {
                        isPageOpenStrech = true
                        setChartViewStrech(view)
                        (activity as MainActivity).tabbargone() //탭바사라지게게
                        slideViewerStrech.visibility = View.VISIBLE
                        todayBlackBackground.visibility = View.VISIBLE
                        slideViewerStrech.startAnimation(translateup)
                    }
                }

            }
            barchartAcceptStrech.setOnClickListener {
                if (isPageOpenStrech) {

                    slideViewerStrech.visibility = View.GONE
                    todayBlackBackground.visibility = View.GONE
                    slideViewerStrech.startAnimation(translatedown)
                    (activity as MainActivity).tabbarvisible()
                    isPageOpenStrech = false
                }
            }
        }


    }

    private fun initData() {
        Log.e("initData", "PROGRESSING")
        FirebaseRepository.reference.child("walk_data").child("201710560")
            .child("2021-06-07").get().addOnSuccessListener { snapShot ->
                val walkData = snapShot.child("step_count").value.toString()
                binding!!.todayWalkNumber.text = walkData
                //binding!!.todayWalkNumberDetail = walkData
                //10000보 기준
                if (walkData.toInt() >= 10000) {
                    binding!!.todayWalkBarColor.width = 657
                } else {
                    binding!!.todayWalkBarColor.width =
                        ((walkData.toFloat() / 10000) * 656.25).toInt()
                }

                // Log.e("initData", "SUCCESS")

            }
        FirebaseRepository.reference.child("water_data").child("201710560")
            .child("2021-06-07").get().addOnSuccessListener { snapShot ->
                val waterData = snapShot.child("quantity").value.toString()
                val allwater: Float = (waterData.toFloat() / 1000.0).toFloat()
                binding!!.todayWaterNumber.text = (floor(allwater * 10) / 10).toString()
                //binding!!.todayWaterNumberDetail = waterData
                //2리터 기준,2000ml

                if (waterData.toFloat() >= 2000.0) {
                    binding!!.todayWaterBarColor.width = 657
                } else {
                    binding!!.todayWaterBarColor.width =
                        ((waterData.toFloat() / 2000) * 656.25).toInt()
                }


                Log.e("initData", "SUCCESS")

            }

    }

    private fun setChartViewWalk(view: View) {
        var walk_week = binding?.barchartWalkWeek
        if (walk_week != null) {
            setWeek(walk_week)
        }
    }

    private fun setChartViewWater(view: View) {
        var water_week = binding?.barchartWaterWeek
        if (water_week != null) {
            setWeek(water_week)
        }

    }

    private fun setChartViewStrech(view: View) {

        var strech_week = binding?.barchartStrechWeek
        if (strech_week != null) {
            setWeek(strech_week)
        }

    }

    private fun initBarDataSetWater(barDataSet: BarDataSet) {
        barDataSet.color = Color.parseColor("#2B7FEE")
        barDataSet.formSize = 3f
        barDataSet.setDrawValues(false)//막대값

    }

    private fun initBarDataSetWalk(barDataSet: BarDataSet) {
        barDataSet.color = Color.parseColor("#FF1862")
        barDataSet.formSize = 3f
        barDataSet.setDrawValues(false)//막대값

    }

    private fun initBarDataSetStrech(barDataSet: BarDataSet) {
        barDataSet.color = Color.parseColor("#FFCE16")
        barDataSet.formSize = 3f
        barDataSet.setDrawValues(false)//막대값

    }

    private fun setWeek(barChart: BarChart) {
        initBarChart(barChart)

        barChart.setScaleEnabled(false)
        val valueList = ArrayList<Double>()
        val entries: ArrayList<BarEntry> = ArrayList()
        val title = ""

        valueList.add(600.1)
        valueList.add(200.1)
        valueList.add(250.1)
        valueList.add(400.1)
        valueList.add(150.1)
        valueList.add(350.1)
        valueList.add(80.1)


        for (i in 0 until valueList.size) {
            val barEntry = BarEntry(i + 1.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, title)
        val data = BarData(barDataSet)
        data.barWidth = 0.15f
        barChart.data = data
        barChart.invalidate()

        if (isPageOpenWater) {
            initBarDataSetWater(barDataSet)
        } else if (isPageOpenWalk) {
            initBarDataSetWalk(barDataSet)
        } else if (isPageOpenStrech) {
            initBarDataSetStrech(barDataSet)
        }
    }

    private fun initBarChart(barChart: BarChart) {
        barChart.setTouchEnabled(false)

        barChart.setDrawGridBackground(false)//회색배경
        barChart.setDrawBarShadow(true)//그림자
        barChart.setDrawBorders(false)//경계

        val description = Description()
        description.setEnabled(false)
        barChart.setDescription(description)//설명라벨제거

        val xAxis: XAxis = barChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f//간격설정
        xAxis.textColor = Color.BLACK
        //xAxis.textSize=12f
        xAxis.setDrawAxisLine(false)//X축 숨기기
        xAxis.setDrawGridLines(false)

        val leftAxis: YAxis = barChart.getAxisLeft()
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawGridLines(false)
        leftAxis.textColor = Color.WHITE

        val rightAxis: YAxis = barChart.getAxisRight()
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
        rightAxis.textColor = Color.WHITE

        val legend: Legend = barChart.getLegend()
        legend.isEnabled = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
