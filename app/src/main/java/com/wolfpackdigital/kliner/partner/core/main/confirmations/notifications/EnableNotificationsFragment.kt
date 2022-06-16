package com.wolfpackdigital.kliner.partner.core.main.confirmations.notifications

import com.wolfpackdigital.kliner.partner.EnableNotificationsBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnableNotificationsFragment :
    BaseFragment<EnableNotificationsBinding, EnableNotificationsViewModel>(
        R.layout.fr_enable_notifications
    ) {
    override val viewModel by viewModel<EnableNotificationsViewModel>()

    override fun setupViews() {
    }
}
