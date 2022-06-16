package com.wolfpackdigital.kliner.partner.shared.utils.extensions

import android.view.View

// Extensions related to views (Views, TextViews, etc...)

fun View.hideAlpha() {
    this.alpha = 0f
}

fun View.showAlpha() {
    this.alpha = 1f
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.disable() {
    this.isEnabled = false
}

fun View.enable() {
    this.isEnabled = true
}
