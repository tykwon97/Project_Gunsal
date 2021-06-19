package com.gs.gunsal.fragment

import android.graphics.Color
import android.os.Bundle
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
import com.gs.gunsal.dataClass.*
import com.gs.gunsal.databinding.FragmentTodayViewerBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.floor
import kotlin.math.roundToInt


class Today_Viewer_Fragment(val userId: String) : Fragment() {
    var binding: FragmentTodayViewerBinding?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        binding = FragmentTodayViewerBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }
    var isPageOpenWater=false
    var isPageOpenWalk=false
    var isPageOpenStrech=false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        binding!!.apply{

            val translatedown = AnimationUtils.loadAnimation(getContext(), R.anim.translate_down)
            val translateup = AnimationUtils.loadAnimation(getContext(),R.anim.translate_up)
            todayWater.setOnClickListener{
                if(!isPageOpenStrech&&!isPageOpenWalk){
                    if(!isPageOpenWater){
                        isPageOpenWater=true
                        val dateString = FirebaseRepository.getCurrentDate() // 2020-01-01의 String 형식
                        val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE) // LocalDate형으로 파싱
                        FirebaseRepository.getWeekDrinkData(userId, date)
                        FirebaseRepository.waterWeekDataListener = object: FirebaseRepository.OnWaterWeekDataListener{
                            override fun onWaterWeekDataCaught(
                                weekWaterData: ArrayList<Int>,
                                dayOfWeek: Int
                            ) {
                                setChartViewWater(weekWaterData, dayOfWeek)
                                (activity as MainActivity).tabbargone() //탭바사라지게게
                                slideViewerWater.visibility=View.VISIBLE
                                todayBlackBackground.visibility=View.VISIBLE
                                slideViewerWater.startAnimation(translateup)
                            }
                        }
                    }
                }

            }
            barchartAcceptWater.setOnClickListener{
                if(isPageOpenWater){

                    slideViewerWater.visibility=View.GONE
                    todayBlackBackground.visibility=View.GONE
                    slideViewerWater.startAnimation(translatedown)
                    (activity as MainActivity).tabbarvisible()
                    isPageOpenWater=false
                }
            }



            todayWalk.setOnClickListener{
                if(!isPageOpenStrech&&!isPageOpenWater){
                    if(!isPageOpenWalk){
                        isPageOpenWalk=true
                        val dateString = FirebaseRepository.getCurrentDate() // 2020-01-01의 String 형식
                        val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE) // LocalDate형으로 파싱
                        FirebaseRepository.getWeekWalkData(userId, date)
                        FirebaseRepository.walkWeekDataListener = object: FirebaseRepository.OnWalkWeekDataListener{
                            override fun onWalkWeekDataCaught(
                                weekWalkData: ArrayList<Int>,
                                dayOfWeek: Int
                            ) {
                                setChartViewWalk(weekWalkData, dayOfWeek)
                                (activity as MainActivity).tabbargone() //탭바사라지게게
                                slideViewerWalk.visibility=View.VISIBLE
                                todayBlackBackground.visibility=View.VISIBLE
                                slideViewerWalk.startAnimation(translateup)
                            }

                        }

                    }
                }

            }
            barchartAcceptWalk.setOnClickListener{
                if(isPageOpenWalk){
                    slideViewerWalk.visibility=View.GONE
                    todayBlackBackground.visibility=View.GONE
                    slideViewerWalk.startAnimation(translatedown)
                    (activity as MainActivity).tabbarvisible()
                    isPageOpenWalk=false
                }
            }




            todayStrech.setOnClickListener{
                if(!isPageOpenWalk&&!isPageOpenWater){
                    if(!isPageOpenStrech){
                        isPageOpenStrech=true
                        val dateString = FirebaseRepository.getCurrentDate() // 2020-01-01의 String 형식
                        val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE) // LocalDate형으로 파싱
                        FirebaseRepository.getWeekStretchData(userId, date)
                        FirebaseRepository.stretchWeekDataListener = object: FirebaseRepository.OnStretchWeekDataListener{
                            override fun onStretchWeekDataCaught(
                                weekStretchData: ArrayList<Int>,
                                dayOfWeek: Int
                            ) {
                                setChartViewStretch(weekStretchData, dayOfWeek)
                                (activity as MainActivity).tabbargone() //탭바사라지게게
                                slideViewerStrech.visibility=View.VISIBLE
                                todayBlackBackground.visibility=View.VISIBLE
                                slideViewerStrech.startAnimation(translateup)
                            }
                        }

                    }
                }
            }
            barchartAcceptStrech.setOnClickListener{
                if(isPageOpenStrech){

                    slideViewerStrech.visibility=View.GONE
                    todayBlackBackground.visibility=View.GONE
                    slideViewerStrech.startAnimation(translatedown)
                    (activity as MainActivity).tabbarvisible()
                    isPageOpenStrech=false
                }
            }
        }


    }

    private fun initData() {
        val today = FirebaseRepository.getCurrentDate()
        FirebaseRepository.getTotalData(userId, today)
        //Log.e("today", today)
        FirebaseRepository.totalDataListener = object: FirebaseRepository.OnTotalDataListener{
            override fun onTotalDataCaught(
                userData: UserData,
                bodyData: BodyDataDetail,
                waterData: WaterDataDetail,
                walkData: WalkDataDetail,
                stretchData: StretchDataDetail
            ) {
                binding!!.apply {
                    //칼로리
                    todayKcalNumber.text = walkData.kcal_consumed.toString()

                    //수분섭취
                    val allwater:Float = (waterData.quantity.toFloat()/1000.0).toFloat()
                    todayWaterNumber.text = (floor(allwater*100)/100).toString()
                    todayWaterNumberDetail.text=(floor(allwater*100)/100).toString()
                    //2리터 기준,2000ml
                    if(waterData.quantity.toFloat()>=2000.0){
                        binding!!.todayWaterBarColor.width=657
                    }else{
                        binding!!.todayWaterBarColor.width=((waterData.quantity.toFloat()/2000)*656.25).toInt()
                    }

                    //걸음수
                    todayWalkNumber.text = walkData.step_count.toString()
                    todayWalkNumberDetail.text = walkData.step_count.toString()
                    //10000보 기준
                    if(walkData.step_count.toInt()>=10000){
                        binding!!.todayWalkBarColor.width=657
                    }else{
                        binding!!.todayWalkBarColor.width=((walkData.step_count.toFloat()/10000)*656.25).toInt()
                    }

                    //스트레칭 시간
                    val minutes = stretchData.time / 60
                    val sec  = (stretchData.time % 60) / 60.0
                    val secResult = Math.round(sec * 10) / 10f
                    val result = minutes + secResult
                    todayStrechNumber.text = "$result"
                }
            }
        }

    }
    private fun setChartViewWalk(weekWalkData: ArrayList<Int>, dayOfWeek: Int){
        var walk_week= binding?.barchartWalkWeek
        if (walk_week != null) {
            setWeek(walk_week, weekWalkData, dayOfWeek, 1)
        }
    }

    private fun setChartViewWater(weekWaterData: ArrayList<Int>, dayOfWeek: Int){
        var water_week= binding?.barchartWaterWeek
        if (water_week != null) {
            setWeek(water_week, weekWaterData, dayOfWeek, 2)
        }

    }

    private fun setChartViewStretch(weekStretchData: ArrayList<Int>, dayOfWeek: Int){

        var strech_week= binding?.barchartStrechWeek
        if (strech_week != null) {
            setWeek(strech_week, weekStretchData, dayOfWeek, 3)
        }

    }

    private fun initBarDataSetWater(barDataSet: BarDataSet){
        barDataSet.color=Color.parseColor("#2B7FEE")
        barDataSet.formSize=3f
        barDataSet.setDrawValues(false)//막대값

    }

    private fun initBarDataSetWalk(barDataSet: BarDataSet){
        barDataSet.color=Color.parseColor("#FF1862")
        barDataSet.formSize=3f
        barDataSet.setDrawValues(false)//막대값

    }

    private fun initBarDataSetStrech(barDataSet: BarDataSet){
        barDataSet.color=Color.parseColor("#FFCE16")
        barDataSet.formSize=3f
        barDataSet.setDrawValues(false)//막대값

    }

    private fun setWeek(barChart: BarChart, weekData: ArrayList<Int>, dayOfWeek: Int, type: Int){
        initBarChart(barChart)
        barChart.setScaleEnabled(false)
        val valueList=ArrayList<Int>()
        val entries:ArrayList<BarEntry> = ArrayList()
        val title=""
        var sum = 0

        for(i in 0 until 7) {
            valueList.add(weekData[i])
            sum += weekData[i]
        }
        when(type){
            1->{ // Walk
                binding!!.apply {
                    barchartBottomNameWalk.text = when(dayOfWeek){
                        1-> "화수목금토일월"
                        2-> "수목금토일월화"
                        3-> "목금토일월화수"
                        4-> "금토일월화수목"
                        5-> "토일월화수목금"
                        6-> "일월화수목금토"
                        7-> "월화수목금토일"
                        else-> "월화수목금토일"
                    }
                    todayWalkNumberDetail.text = (sum / 7).toString()
                }
            }
            2->{ // Drink
                binding!!.apply {
                    barchartBottomNameWater.text = when(dayOfWeek){
                        1-> "화수목금토일월"
                        2-> "수목금토일월화"
                        3-> "목금토일월화수"
                        4-> "금토일월화수목"
                        5-> "토일월화수목금"
                        6-> "일월화수목금토"
                        7-> "월화수목금토일"
                        else-> "월화수목금토일"
                    }
                    val drinkSum = (sum / 7).toDouble()
                    val drinkWeekAvg = (drinkSum).roundToInt() / 1000f
                    todayWaterNumberDetail.text = drinkWeekAvg.toString()
                }
            }
            3->{ // Stretch
                binding!!.apply {
                    barchartBottomNameStrech.text = when(dayOfWeek){
                        1-> "화수목금토일월"
                        2-> "수목금토일월화"
                        3-> "목금토일월화수"
                        4-> "금토일월화수목"
                        5-> "토일월화수목금"
                        6-> "일월화수목금토"
                        7-> "월화수목금토일"
                        else-> "월화수목금토일"
                    }
                    val time = sum / 7.0
                    val minutes = time.toInt() / 60
                    val sec  = (time % 60) / 60.0
                    val secResult = (sec * 10).roundToInt() / 100f
                    val result = minutes + secResult
                    todayStrechNumberDetail.text = "$result"
                    //todayStrechNumberDetail.text = (sum / 7).toString()
                }
            }
        }

        for(i in 0 until valueList.size){
            val barEntry=BarEntry((i+1).toFloat(),valueList[i].toFloat())
            entries.add(barEntry)
        }

        val barDataSet= BarDataSet(entries,title)
        val data= BarData(barDataSet)
        data.barWidth=0.15f
        barChart.data=data
        barChart.invalidate()

        if(isPageOpenWater){
            initBarDataSetWater(barDataSet)
        }else if(isPageOpenWalk){
            initBarDataSetWalk(barDataSet)
        }else if(isPageOpenStrech){
            initBarDataSetStrech(barDataSet)
        }
    }

    private fun initBarChart(barChart: BarChart){
        barChart.setTouchEnabled(false)

        barChart.setDrawGridBackground(false)//회색배경
        barChart.setDrawBarShadow(true)//그림자
        barChart.setDrawBorders(false)//경계

        val description = Description()
        description.setEnabled(false)
        barChart.setDescription(description)//설명라벨제거

        val xAxis:XAxis=barChart.getXAxis()
        xAxis.position=XAxis.XAxisPosition.BOTTOM
        xAxis.granularity=1f//간격설정
        xAxis.textColor= Color.BLACK
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

        val legend:Legend=barChart.getLegend()
        legend.isEnabled=false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}