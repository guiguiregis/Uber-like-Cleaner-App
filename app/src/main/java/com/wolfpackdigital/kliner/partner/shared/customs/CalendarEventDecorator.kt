package com.wolfpackdigital.kliner.partner.shared.customs

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.wolfpackdigital.kliner.partner.shared.utils.Constants.DOT_RADIUS

class CalendarEventDecorator(
    private val color: Int? = null,
    var dates: List<CalendarDay>? = null
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates?.contains(day) ?: false
    }

    override fun decorate(view: DayViewFacade) {
        color?.let { view.addSpan(DotSpan(DOT_RADIUS, it)) }
    }
}
