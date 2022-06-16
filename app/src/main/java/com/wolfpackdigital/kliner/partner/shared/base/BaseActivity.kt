package com.wolfpackdigital.kliner.partner.shared.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.wolfpackdigital.kliner.partner.BR
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.data.notifications.NotificationModel
import com.wolfpackdigital.kliner.partner.shared.noNetwork.NoNetworkDialog
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.views.LoadingDialog

abstract class BaseActivity<BINDING : ViewDataBinding, VIEW_MODEL : ViewModel>
constructor(@LayoutRes private val layoutResource: Int) : AppCompatActivity() {

    protected val binding by activityBinding<BINDING>(
        layoutResource
    )
    protected abstract val viewModel: VIEW_MODEL

    var loadingDialog: LoadingDialog? = null
    var networkDialog: NoNetworkDialog? = null

    /**
     * This receiver handles push notifications while the app was active
     */
    private val newFCMNotificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("FBNOTIF", "broadcast received")
            getNotificationFromIntent(intent).let {
                Log.d("FBNOTIF", "broadcast data: $it")
                parseNotificationFromIntent(it)
            }
        }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.also {
            it.lifecycleOwner = this
            it.setVariable(BR.viewModel, viewModel)
        }
        loadingDialog = LoadingDialog(this)
        networkDialog = NoNetworkDialog.getInstance()
        registerReceiver(newFCMNotificationReceiver, IntentFilter(Constants.FCM_NEW_MESSAGE))
        parseNotificationFromIntent(getNotificationFromIntent())
        setupViews()
    }

    @CallSuper
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseNotificationFromIntent(getNotificationFromIntent(intent))
    }

    @CallSuper
    override fun onDestroy() {
        unregisterReceiver(newFCMNotificationReceiver)
        loadingDialog = null
        networkDialog = null
        super.onDestroy()
    }

    fun loadColoredStatusBar(color: Int = android.R.color.white) {
        window?.statusBarColor = ContextCompat.getColor(baseContext, color)
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun loadBlackStatusBar() {
        window?.statusBarColor = ContextCompat.getColor(baseContext, R.color.bgBlack)
        window?.decorView?.systemUiVisibility = 0
    }

    fun loadTranslucentStatusBar() {
        window?.statusBarColor = ContextCompat.getColor(baseContext, android.R.color.transparent)
        window?.decorView?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    private fun getNotificationFromIntent(newIntent: Intent? = null) = (newIntent ?: intent)
        ?.extras?.getParcelable<NotificationModel>(Constants.FCM_NOTIFICATION_MODEL)

    abstract fun setupViews()
    abstract fun parseNotificationFromIntent(notification: NotificationModel? = null)
}
