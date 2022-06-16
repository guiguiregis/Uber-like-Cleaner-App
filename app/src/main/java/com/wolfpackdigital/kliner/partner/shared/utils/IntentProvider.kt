package com.wolfpackdigital.kliner.partner.shared.utils

import android.content.Intent
import androidx.core.net.toUri

object IntentProvider {
    fun getMapsIntent(latitude: String?, longitude: String?) = Intent().apply {
        action = Intent.ACTION_VIEW
        data = "geo:0,0?q=$latitude,$longitude".toUri()
    }

    fun getDialIntent(number: String) = Intent().apply {
        action = Intent.ACTION_DIAL
        data = "tel:$number".toUri()
    }
}
