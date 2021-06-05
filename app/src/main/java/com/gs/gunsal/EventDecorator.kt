package com.gs.gunsal

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.*
import kotlin.collections.HashSet


class EventDecorator(color: Int, dates: Collection<CalendarDay>, context: Context) :
    DayViewDecorator {
    var drawable: Drawable? = null
    var color = 0
    var dates: HashSet<CalendarDay>? = null


    init {
        this.drawable = drawable
        this.color = color
        this.dates = HashSet(dates)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates!!.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(15F, color))
    }


}