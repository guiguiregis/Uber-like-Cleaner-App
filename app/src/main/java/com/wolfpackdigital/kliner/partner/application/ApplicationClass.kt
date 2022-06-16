package com.wolfpackdigital.kliner.partner.application

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import com.orhanobut.hawk.Hawk
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.data.notifications.NotificationHelper
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.NetworkUtil
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@Suppress("unused")
class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        Hawk.init(applicationContext).build()
        startKoin {
            androidContext(applicationContext)
            modules(AppModules.modules)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationHelper.createNotificationChannel(
                this,
                NotificationManagerCompat.IMPORTANCE_HIGH,
                false,
                Constants.PUSH_NOTIFICATION_CHANNEL_ID,
                getString(R.string.push_notification_channel_name),
                getString(R.string.default_notification_channel_desc)
            )
        }
        NetworkUtil.startNetworkCallback(this)
    }

    override fun onTerminate() {
        NetworkUtil.stopNetworkCallback(this)
        super.onTerminate()
    }
}
