package com.wolfpackdigital.kliner.partner.shared.customs

import android.graphics.Canvas
import android.graphics.Paint
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class TwoDotsSpan(
    val radius: Float,
    private val firstColor: Int,
    private val secondColor: Int
) : DotSpan() {

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        charSequence: CharSequence,
        start: Int,
        end: Int,
        lineNum: Int
    ) {
        val oldColor = paint.color
        drawFirstCircle(paint, canvas, left, right, bottom)
        drawSecondCircle(paint, canvas, left, right, bottom)
        paint.color = oldColor
    }

    private fun drawFirstCircle(
        paint: Paint,
        canvas: Canvas,
        left: Int,
        right: Int,
        bottom: Int
    ) {
        if (firstColor != 0) {
            paint.color = firstColor
        }
        canvas.drawCircle(
            (left + right).div(2f) - radius - radius.div(2),
            bottom + radius,
            radius,
            paint
        )
    }

    private fun drawSecondCircle(
        paint: Paint,
        canvas: Canvas,
        left: Int,
        right: Int,
        bottom: Int
    ) {
        if (secondColor != 0) {
            paint.color = secondColor
        }
        canvas.drawCircle(
            (left + right).div(2f) + radius + radius.div(2),
            bottom + radius,
            radius,
            paint
        )
    }
}
