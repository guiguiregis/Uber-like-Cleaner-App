package com.wolfpackdigital.kliner.partner.shared.customs

import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CalendarFutureDayWithNoEventDecorator(
    private val color: Int,
    var dates: List<CalendarDay>? = null
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day.isAfter(CalendarDay.today()) && dates?.let { !it.contains(day) } ?: false
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(color))
    }
}
