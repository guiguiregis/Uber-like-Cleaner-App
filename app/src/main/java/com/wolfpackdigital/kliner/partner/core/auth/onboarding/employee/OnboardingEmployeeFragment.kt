package com.wolfpackdigital.kliner.partner.core.auth.onboarding.employee

import androidx.navigation.fragment.navArgs
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.wolfpackdigital.kliner.partner.OnboardingEmployeeBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.adapters.CheckboxAdapter
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.requestCameraPermissions
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class OnboardingEmployeeFragment :
    BaseFragment<OnboardingEmployeeBinding, OnboardingEmployeeViewModel>(R.layout.fr_onboarding_employee) {

    // Properties

    private val args by navArgs<OnboardingEmployeeFragmentArgs>()

    override val viewModel by viewModel<OnboardingEmployeeViewModel> {
        parametersOf(args.cleanerID)
    }

    private val typesOfWorkAdapter by lazy { CheckboxAdapter() }

    // Lifecycle

    override fun setupViews() {
        binding?.rvTypesOfWork?.adapter = typesOfWorkAdapter
        viewModel.fetchCleanerProfile()
        setupObservers()
    }

    // Setup

    private fun setupObservers() {
        viewModel.typesOfWork.observe(viewLifecycleOwner, typesOfWorkAdapter::submitList)
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is OnboardingEmployeeViewModel.Command.OpenImagePicker -> requestCameraPermissions(
                    onGranted = {
                        RxImagePicker.with(childFragmentManager).requestImage(Sources.CHOOSER)
                            .subscribe { uri ->
                                viewModel.profileImageUri.value = uri
                            }
                    },
                    onNotGranted = { snackBar(getString(R.string.camera_permissions_required)) }
                )
            }
        }
    }
}
