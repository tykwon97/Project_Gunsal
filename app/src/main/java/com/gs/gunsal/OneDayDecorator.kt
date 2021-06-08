package com.gs.gunsal

import android.graphics.Color
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

class OneDayDecorator(day: CalendarDay) : DayViewDecorator {

    var date: CalendarDay = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return date != null && day.equals(date)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(StyleSpan(Typeface.BOLD));
        view.addSpan(RelativeSizeSpan(1.4f));
        view.addSpan(ForegroundColorSpan(Color.GREEN));
    }
    fun setDate(date: Date?) {
        this.date = CalendarDay.from(date)
    }
}