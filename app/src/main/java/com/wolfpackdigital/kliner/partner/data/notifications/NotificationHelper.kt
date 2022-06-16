package com.wolfpackdigital.kliner.partner.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.auth.AuthActivity
import com.wolfpackdigital.kliner.partner.core.main.MainActivity
import com.wolfpackdigital.kliner.partner.shared.utils.Constants

object NotificationHelper {

    @Suppress("LongParameterList")
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(
        context: Context,
        importance: Int,
        showBadge: Boolean,
        channelId: String,
        name: String,
        description: String
    ) {
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        channel.setShowBadge(showBadge)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    fun createNotificationFromPush(context: Context, pushNotificationData: NotificationModel) {
        val notificationBuilder = buildNotificationFromPush(context, pushNotificationData)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.notify(
            pushNotificationData.body.hashCode(),
            notificationBuilder.build()
        )
    }

    private fun buildNotificationFromPush(
        context: Context,
        pushNotificationData: NotificationModel
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, Constants.PUSH_NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_systray_notifications)

            setContentTitle(pushNotificationData.title)
            setStyle(NotificationCompat.BigTextStyle().bigText(pushNotificationData.body))
            setAutoCancel(true)

            val notificationIntent = when (pushNotificationData.action) {
                PushNotificationAction.ACTIVATE_CLEANER_PROFILE ->
                    Intent(context, AuthActivity::class.java)
                else -> Intent(context, MainActivity::class.java)
            }.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

            notificationIntent.putExtra(Constants.FCM_NOTIFICATION_MODEL, pushNotificationData)

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(MainActivity::class.java)
            stackBuilder.addNextIntent(notificationIntent)

            val pendingIntent = stackBuilder.getPendingIntent(
                pushNotificationData.body.hashCode(),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            setContentIntent(pendingIntent)
        }
    }
}
