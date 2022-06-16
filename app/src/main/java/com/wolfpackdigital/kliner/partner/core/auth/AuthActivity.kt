package com.wolfpackdigital.kliner.partner.core.auth

import android.animation.LayoutTransition
import android.util.Log
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.wolfpackdigital.kliner.partner.AuthActBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.data.notifications.NotificationModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : BaseActivity<AuthActBinding, AuthActivityViewModel>(R.layout.activity_auth) {
    override val viewModel by viewModel<AuthActivityViewModel>()

    override fun setupViews() {
        (binding.clRoot as? ViewGroup)?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
        setupObservers()
    }

    private fun setupObservers() {
        binding.authHostFragment.post {
            findNavController(R.id.auth_host_fragment)
                .addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.welcomeFragment -> loadTranslucentStatusBar()
                        R.id.phoneConfirmationFragment -> loadColoredStatusBar()
                        else -> loadColoredStatusBar()
                    }
                }
        }
    }

    override fun parseNotificationFromIntent(notification: NotificationModel?) {
        Log.d("FBNOTIF", "auth notification parsed: $notification")
    }
}
