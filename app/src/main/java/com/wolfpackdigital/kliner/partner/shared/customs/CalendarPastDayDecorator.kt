package com.wolfpackdigital.kliner.partner.shared.customs

import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CalendarPastDayDecorator(private val color: Int) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day.isBefore(CalendarDay.today())
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(color))
    }
}
