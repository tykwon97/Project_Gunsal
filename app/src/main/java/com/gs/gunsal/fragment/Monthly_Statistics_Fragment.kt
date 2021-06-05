package com.gs.gunsal.fragment

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gs.gunsal.EventDecorator
import com.gs.gunsal.MainActivity
import com.gs.gunsal.OneDayDecorator
import com.gs.gunsal.R
import com.gs.gunsal.databinding.FragmentMonthlyStatisticsBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class Monthly_Statistics_Fragment : Fragment() {

    lateinit var binding: FragmentMonthlyStatisticsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMonthlyStatisticsBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    var isPageOpen = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val result = arrayOf("2021,04,01", "2021,04,10", "2021,04,18", "2021,04,20")
        ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor())
        val translatedown = AnimationUtils.loadAnimation(getContext(), R.anim.translate_down)
        val translateup = AnimationUtils.loadAnimation(getContext(), R.anim.translate_up)

        binding!!.apply {
            monthlyTextColor.bringToFront()
            monthly.state().edit()
                .setMinimumDate(CalendarDay.from(2021, 0, 1))
                .setMaximumDate(CalendarDay.from(2021,6,24))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()
            monthly.setOnDateChangedListener { widget, date, selected ->
                //클릭한 날짜 받아오기
                todayTranslateUpDateWalk.text =
                    date.year.toString() + "." + date.month.toString() + "." + date.day.toString()
                if (!isPageOpen) {
                    isPageOpen = true
                    (activity as MainActivity).tabbargone() //탭바사라지게게
                    monthly.visibility = View.GONE
                    slideViewer.visibility = View.VISIBLE
                    todayBlackBackground.visibility = View.VISIBLE
                    monthlyTextColor.visibility = View.GONE
                    slideViewer.startAnimation(translateup)
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
    }

    inner class ApiSimulator internal constructor(var Time_Result: Array<String>) :
        AsyncTask<Void?, Void?, List<CalendarDay>>() {
        override fun doInBackground(vararg params: Void?): List<CalendarDay> {
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
                val day: CalendarDay = CalendarDay.from(calendar)
                val time = Time_Result[i].split(",").toTypedArray()
                val year = time[0].toInt()
                val month = time[1].toInt()
                val dayy = time[2].toInt()
                dates.add(day)
                calendar.set(year, month - 1, dayy)
            }
            return dates
        }

        override fun onPostExecute(calendarDays: List<CalendarDay>) {
            super.onPostExecute(calendarDays)
            if (isCancelled) {
                return
            }
            binding.monthly.addDecorator(
                EventDecorator(
                    ContextCompat.getColor(context!!, R.color.select_color),
                    calendarDays,
                    context!!
                )
            )
        }

    }
}