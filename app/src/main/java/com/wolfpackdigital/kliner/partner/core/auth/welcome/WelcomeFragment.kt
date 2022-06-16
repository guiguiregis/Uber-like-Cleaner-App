package com.wolfpackdigital.kliner.partner.core.auth.welcome

import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.WelcomeBinding
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeFragment : BaseFragment<WelcomeBinding, WelcomeViewModel>(R.layout.fr_welcome) {

    // Properties

    override val viewModel by viewModel<WelcomeViewModel>()

    // Lifecycle

    override fun setupViews() {
        setupObservers()
    }

    // Setup

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
        }
    }
}
