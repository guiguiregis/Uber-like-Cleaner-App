package com.wolfpackdigital.kliner.partner.core.auth.splash

import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.SplashScreenBinding
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashScreenFragment :
    BaseFragment<SplashScreenBinding, SplashScreenViewModel>(R.layout.fr_splash_screen) {
    override val viewModel by viewModel<SplashScreenViewModel>()

    override fun setupViews() {
        viewModel.goToOnboardingScreen()
    }
}
