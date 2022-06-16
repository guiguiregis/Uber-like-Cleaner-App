package com.wolfpackdigital.kliner.partner.core.main.confirmations.geolocation

import android.Manifest
import android.annotation.SuppressLint
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wolfpackdigital.kliner.partner.EnableGeolocationBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.MainActivity
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.navController
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnableGeolocationFragment :
    BaseFragment<EnableGeolocationBinding, EnableGeolocationViewModel>(
        R.layout.fr_enable_geolocation
    ) {
    override val viewModel by viewModel<EnableGeolocationViewModel>()

    override fun setupViews() {
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is EnableGeolocationViewModel.Command.RequestLocationPermissions -> {
                    requestGeolocationPermissions()
                }
                EnableGeolocationViewModel.Command.GoToDashboard ->
                    navController?.navigate(
                        EnableGeolocationFragmentDirections.actionEnableGeolocationFragmentOnboardingToMainActivity(),
                        MainActivity.getMainActivityExtras()
                    )
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun requestGeolocationPermissions() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        viewModel.onGeolocationAccepted()
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                    }
                    else -> {
                        snackBar(getString(R.string.location_permissions_required))
                    }
                }
            }
    }
}
