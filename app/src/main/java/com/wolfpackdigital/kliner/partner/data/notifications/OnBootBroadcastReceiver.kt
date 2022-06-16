package com.wolfpackdigital.kliner.partner.data.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class OnBootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Intent("com.wolfpackdigital.kliner.partner.data.notifications.KlinerMessagingService").apply {
                    setClass(context ?: return, KlinerMessagingService::class.java)
                    context.startService(this)
                }
            }
            else -> {
            }
        }
    }
}
