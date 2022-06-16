package com.wolfpackdigital.kliner.partner.core.auth.registration.verification

import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.UserVerificationBinding
import com.wolfpackdigital.kliner.partner.core.auth.AuthActivity
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.contract.SignContractBottomSheetDialog
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.contract.SignContractViewModel
import com.wolfpackdigital.kliner.partner.core.main.MainActivity
import com.wolfpackdigital.kliner.partner.data.models.CleanerStatus
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.navController
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserVerificationFragment :
    BaseFragment<UserVerificationBinding, UserVerificationViewModel>(R.layout.fr_user_verification) {

    // Properties

    override val viewModel by viewModel<UserVerificationViewModel>()
    private val dialogViewModel by viewModel<SignContractViewModel>()
    private val dialog by lazy { SignContractBottomSheetDialog() }

    // Lifecycle

    override fun setupViews() {
        viewModel.fetchCleanerStatus()
        setupObservers()
    }

    // Setup

    private fun setupObservers() {
        viewModel.screenState.observe(viewLifecycleOwner) {
            when (it) {
                CleanerStatus.ACTIVE ->
                    (activity as? AuthActivity)?.loadColoredStatusBar(R.color.colorAccent)
                else -> (activity as? AuthActivity)?.loadBlackStatusBar()
            }
        }
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                UserVerificationViewModel.Command.ShowContractDialog ->
                    dialog.show(
                        childFragmentManager,
                        SignContractBottomSheetDialog::class.simpleName
                    )
                UserVerificationViewModel.Command.GoToDashboard ->
                    navController?.navigate(
                        UserVerificationFragmentDirections.actionUserVerificationFragmentToMainActivity(),
                        MainActivity.getMainActivityExtras()
                    )
            }
        }
        dialogViewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                SignContractViewModel.Command.ContractSigned -> viewModel.checkNotificationsStatus()
            }
        }
    }
}
