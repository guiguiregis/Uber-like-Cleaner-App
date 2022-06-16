package com.wolfpackdigital.kliner.partner.shared.customs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior

class StatefulHideOnScrollBehaviour<T : View> constructor(
    context: Context,
    attributeSet: AttributeSet
) : HideBottomViewOnScrollBehavior<T>(context, attributeSet) {

    var enabled = true

    override fun slideDown(child: T) {
        if (enabled)
            super.slideDown(child)
    }

    override fun slideUp(child: T) {
        if (enabled)
            super.slideUp(child)
    }
}
