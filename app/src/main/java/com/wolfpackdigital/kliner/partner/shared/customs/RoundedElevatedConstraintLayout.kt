package com.wolfpackdigital.kliner.partner.shared.customs

import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter

class RoundedElevatedConstraintLayout @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                val rect = Rect(0, 0, view?.width ?: 0, view?.height ?: 0)
                outline?.setRoundRect(rect, rect.width() / 2f)
            }
        }
        clipToOutline = true
    }
}

/**
 * Radius values will create a different shape, according
 * to following values:
 *     <0  : CIRCLE
 *      0  : SQUARE
 *     >0  : ROUNDED RECT with specified radius
 */
@BindingAdapter("klCornerRadius")
fun RoundedElevatedConstraintLayout.setCornerRadius(radius: Float?) {
    val finalRadius = radius ?: 0f
    outlineProvider = when {
        finalRadius < 0f -> object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                val rect = Rect(0, 0, view?.width ?: 0, view?.height ?: 0)
                outline?.setOval(rect)
            }
        }
        finalRadius == 0f -> object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                val rect = Rect(0, 0, view?.width ?: 0, view?.height ?: 0)
                outline?.setRect(rect)
            }
        }
        finalRadius > 0f -> object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                val rect = Rect(0, 0, view?.width ?: 0, view?.height ?: 0)
                outline?.setRoundRect(rect, finalRadius)
            }
        }
        else -> return
    }
    clipToOutline = true
}
