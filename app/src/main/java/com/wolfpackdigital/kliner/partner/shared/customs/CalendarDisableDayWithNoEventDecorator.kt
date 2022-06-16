package com.wolfpackdigital.kliner.partner.shared.customs

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CalendarDisableDayWithNoEventDecorator(var dates: List<CalendarDay>? = null) :
    DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates?.let { !it.contains(day) } ?: false
    }

    override fun decorate(view: DayViewFacade) {
        view.setDaysDisabled(true)
    }
}
