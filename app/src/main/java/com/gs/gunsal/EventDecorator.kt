package com.gs.gunsal

import android.content.Context
import android.util.Log
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan


class EventDecorator(dates: CalendarDay, context: Context, color: Int) :
    DayViewDecorator {
    var color = 0
    var dates: CalendarDay? = null

    init {
        this.color = color
        this.dates = dates
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        Log.i("dates", dates.toString())
        return day.equals(dates)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(15F, color))
        Log.i("data", view.toString())


    }


}