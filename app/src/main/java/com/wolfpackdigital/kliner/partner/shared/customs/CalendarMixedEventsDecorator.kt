package com.wolfpackdigital.kliner.partner.shared.customs

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.wolfpackdigital.kliner.partner.shared.utils.Constants.DOT_RADIUS

class CalendarMixedEventsDecorator(
    private val firstColor: Int? = null,
    private val secondColor: Int? = null,
    var dates: List<CalendarDay>? = null
) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates?.contains(day) ?: false
    }

    override fun decorate(view: DayViewFacade) {
        if (firstColor != null && secondColor != null) {
            view.addSpan(TwoDotsSpan(DOT_RADIUS, firstColor, secondColor))
        }
    }
}
