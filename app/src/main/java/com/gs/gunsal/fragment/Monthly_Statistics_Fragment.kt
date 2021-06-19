package com.gs.gunsal.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gs.gunsal.*
import com.gs.gunsal.dataClass.*
import com.gs.gunsal.databinding.FragmentMonthlyStatisticsBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.math.floor

class Monthly_Statistics_Fragment(val userId: String) : Fragment() {

    var binding: FragmentMonthlyStatisticsBinding? = null


    //lateinit var isColor = arrayListOf(1, 2, 3, 1)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMonthlyStatisticsBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    var isPageOpen = false

    /*
    * 기본 아이디어
    * 1. 현재 달: 일단 현재 날짜를 가져온 뒤 그 날짜 숫자 만큼 for문을 돌아 1일 까지의 데이터를 가져옴
    * 2. 이전 달: 현재 날짜에 해당하는 달의 이전(-n)달의 끝 날을 구한 뒤 그 날짜 숫자 만큼 for문을 돌아 1일까지의 데이터를 가져옴*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseRepository.getTotalMonthData(userId, FirebaseRepository.getCurrentDate())
        FirebaseRepository.totalMonthListener = object:FirebaseRepository.OnTotalMonthListener{
            override fun onTotalMonthCaught(ratingArray: ArrayList<Rating>) {
                val dates = ArrayList<String>()
                val ratings = ArrayList<Int>()
                for(rating in ratingArray){
                    dates.add(rating.date)
                    ratings.add(rating.badGoodPerfect)
                }
                Log.d("Monthly:onViewCreated(dates)", dates.toString())
                Log.d("Monthly:onViewCreated(ratings)", ratings.toString())
                ApiSimulator(dates, ratings).executeOnExecutor(Executors.newSingleThreadExecutor())
            }
        }

        val translatedown = AnimationUtils.loadAnimation(getContext(), R.anim.translate_down)
        val translateup = AnimationUtils.loadAnimation(getContext(), R.anim.translate_up)
        binding!!.apply {
            val today = FirebaseRepository.getCurrentDate().split("-")
            val year = today[0].toInt()
            val month = today[1].toInt()
            val day = today[2].toInt()
            val cal = Calendar.getInstance()
            cal.set(year, month, day)

            monthlyTextColor.bringToFront()
            monthly.state().edit()
                .setMinimumDate(CalendarDay.from(2021, 0, 1))
                .setMaximumDate(CalendarDay.from(year, month - 1, day))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()
            monthly.setOnDateChangedListener { widget, date, selected ->
                //클릭한 날짜 받아오기
                todayTranslateUpDateWalk.text =
                    date.year.toString() + "." + (date.month + 1).toString() + "." + date.day.toString()
                if (!isPageOpen) {
                    isPageOpen = true
                    (activity as MainActivity).tabbargone() //탭바사라지게게
                    monthly.visibility = View.GONE
                    slideViewer.visibility = View.VISIBLE
                    todayBlackBackground.visibility = View.VISIBLE
                    monthlyTextColor.visibility = View.GONE
                    slideViewer.startAnimation(translateup)
                    var today =
                        date.year.toString() + "-" + date.month.toString() + "-" + date.day.toString()

                    if (date.month + 1 < 10) {
                        if (date.day < 10)
                            today =
                                date.year.toString() + "-0" + (date.month + 1).toString() + "-0" + date.day.toString()
                        else
                            today =
                                date.year.toString() + "-0" + (date.month + 1).toString() + "-" + date.day.toString()
                    } else {
                        if (date.day < 10)
                            today =
                                date.year.toString() + "-" + (date.month + 1).toString() + "-0" + date.day.toString()
                        else
                            today =
                                date.year.toString() + "-" + (date.month + 1).toString() + "-" + date.day.toString()
                    }


                    FirebaseRepository.getTotalData(userId, today)


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
                                val allwater: Float =
                                    (waterData.quantity.toFloat() / 1000.0).toFloat()
                                todayWaterNumber.text = (floor(allwater * 100) / 100).toString()
                                //2리터 기준,2000ml
                                if (waterData.quantity.toFloat() >= 2000.0) {
                                    todayWaterBarColor.width = 657
                                } else {
                                    todayWaterBarColor.width =
                                        ((waterData.quantity.toFloat() / 2000) * 656.25).toInt()
                                }

                                //걸음수
                                todayWalkNumber.text = walkData.step_count.toString()
                                //10000보 기준
                                if (walkData.step_count.toInt() >= 10000) {
                                    todayWalkBarColor.width = 657
                                } else {
                                    todayWalkBarColor.width =
                                        ((walkData.step_count.toFloat() / 10000) * 656.25).toInt()

                                }

                                //스트레칭 시간
                                val minutes = stretchData.time / 60
                                val sec = (stretchData.time % 60) / 60.0
                                val secResult = Math.round(sec * 10) / 10f
                                val result = minutes + secResult
                                todayStrechNumber.text = "$result"
                                if(result.toFloat()>=15.0){
                                    binding!!.todayStrechBarColor.width=657
                                }else{
                                    binding!!.todayStrechBarColor.width=((result.toFloat()/15.0)*656.25).toInt()
                                }

                                val walkkcal = (walkData.step_count.toFloat()*33)/1000.0.toFloat() //1보당 33cal
                                val strechkcal = result*3.toFloat()//1분당 3kcal
                                val sumkcal = Math.round((walkkcal+strechkcal)*10)/10f
                                binding!!.todayKcalNumber.text= sumkcal.toString()
                            }
                        }
                    }
                }
                barchartAccept.setOnClickListener {
                    if (isPageOpen) {
                        slideViewer.visibility = View.GONE
                        todayBlackBackground.visibility = View.GONE
                        //slideViewer.startAnimation(translatedown)
                        (activity as MainActivity).tabbarvisible()
                        isPageOpen = false
                        monthly.visibility = View.VISIBLE
                        monthlyTextColor.visibility = View.VISIBLE
                    }
                }

            }

            monthly.setOnMonthChangedListener { widget, date ->
                val calendar = Calendar.getInstance()
                calendar.set(date.year, date.month - 1, 1)
                val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                var lastDay = ""
                if(date.month < 9)
                    lastDay = "${date.year}-0${date.month + 1}-$lastDayOfMonth"
                else
                    lastDay = "${date.year}-${date.month + 1}-$lastDayOfMonth"
                FirebaseRepository.getTotalMonthData(userId, lastDay)
                FirebaseRepository.totalMonthListener = object:FirebaseRepository.OnTotalMonthListener{
                    override fun onTotalMonthCaught(ratingArray: ArrayList<Rating>) {
                        val dates = ArrayList<String>()
                        val ratings = ArrayList<Int>()
                        for(rating in ratingArray){
                            dates.add(rating.date)
                            ratings.add(rating.badGoodPerfect)
                        }
                        Log.d("Monthly:onViewCreated(dates)", dates.toString())
                        Log.d("Monthly:onViewCreated(ratings)", ratings.toString())
                        ApiSimulator(dates, ratings).executeOnExecutor(Executors.newSingleThreadExecutor())
                    }
                }
            }
        }
    }

    inner class ApiSimulator internal constructor(var Time_Result: ArrayList<String>, var ratings: ArrayList<Int>) :
        AsyncTask<Void?, Void?, List<CalendarDay>>() {

        override fun doInBackground(vararg params: Void?): List<CalendarDay> {
            Log.d("doInBackground", "doInBackground")
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val calendar: Calendar = Calendar.getInstance()
            val dates: ArrayList<CalendarDay> = ArrayList()


            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for (i in Time_Result.indices) {

                val time = Time_Result[i].split("-").toTypedArray()
                val year = time[0].toInt()  //2021
                val month = time[1].toInt() // 6
                val dayy = time[2].toInt()  // 19
                Log.d("Monthly:doInBackground(year.user_month.dayy)", "$year.${month + 1}.$dayy")
                //Log.i("sad", day.toString())

                calendar.set(year, month - 1, dayy)
                val day: CalendarDay = CalendarDay.from(calendar)
                dates.add(day)
            }

            return dates
        }

        override fun onPostExecute(calendarDays: List<CalendarDay>) {
            super.onPostExecute(calendarDays)
            if (isCancelled) {
                return
            }
            Log.i("Monthly:onPostExecute(calendarDays)", calendarDays.toString())
            Log.i("Monthly:onPostExecute(calendarDays_Size)", calendarDays.size.toString())
            Log.i("Monthly:ratingsSize", ratings.size.toString())
            var i = 0
            for (temp in calendarDays) {
                var color: Int
                when (ratings[i++]) {
                    1 -> {
                        Log.d("getColor", "1")
                        color = ContextCompat.getColor(context!!, R.color.walk_color)
                        binding!!.monthly.addDecorator(
                            EventDecorator(temp, context!!, color)
                        )
                    }
                    2 -> {
                        Log.d("getColor", "2")
                        color = ContextCompat.getColor(context!!, R.color.stretching_color)
                        binding!!.monthly.addDecorator(
                            EventDecorator(temp, context!!, color)
                        )
                    }
                    3 -> {
                        Log.d("getColor", "3")
                        color = ContextCompat.getColor(context!!, R.color.select_color)
                        binding!!.monthly.addDecorator(
                            EventDecorator(temp, context!!, color)
                        )
                    }
                }
            }
//            var color: Int
//            val temp = calendarDays[0]
//            when (ratings[i++]) {
//                1 -> {
//                    Log.d("getColor", "1")
//                    color = ContextCompat.getColor(context!!, R.color.walk_color)
//                    binding!!.monthly.addDecorator(
//                        EventDecorator(
//                            temp,
//                            context!!, color
//                        )
//                    )
//                }
//                2 -> {
//                    Log.d("getColor", "2")
//                    color = ContextCompat.getColor(context!!, R.color.stretching_color)
//                    binding!!.monthly.addDecorator(
//                        EventDecorator(
//                            temp,
//                            context!!, color
//                        )
//                    )
//                }
//                3 -> {
//                    Log.d("getColor", "3")
//                    color = ContextCompat.getColor(context!!, R.color.select_color)
//                    binding!!.monthly.addDecorator(
//                        EventDecorator(
//                            temp,
//                            context!!, color
//                        )
//                    )
//                }
//            }



        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}