package com.wolfpackdigital.kliner.partner.shared.customs

import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CalendarTodayDecorator(private val color: Int, private val drawable: Drawable) :
    DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == CalendarDay.today()
    }

    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(drawable)
        view.addSpan(ForegroundColorSpan(color))
    }
}
