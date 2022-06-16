package com.wolfpackdigital.kliner.partner.shared.customs

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class TopAndBottomDividerItemDecoration(private val divider: Drawable) : ItemDecoration() {
    override fun onDraw(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerLeft = parent.paddingLeft
            val dividerRight = parent.width - parent.paddingRight
            var dividerTop: Int
            var dividerBottom: Int
            if (parent.getChildAdapterPosition(child) == 0) {
                dividerTop = child.top - params.topMargin
                dividerBottom = dividerTop + divider.intrinsicHeight
                drawCanvas(dividerLeft, dividerTop, dividerRight, dividerBottom, canvas)
            }
            dividerTop = child.bottom + params.bottomMargin
            dividerBottom = dividerTop + divider.intrinsicHeight
            drawCanvas(dividerLeft, dividerTop, dividerRight, dividerBottom, canvas)
        }
    }

    private fun drawCanvas(
        dividerLeft: Int,
        dividerTop: Int,
        dividerRight: Int,
        dividerBottom: Int,
        canvas: Canvas
    ) {
        divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
        divider.draw(canvas)
    }
}
