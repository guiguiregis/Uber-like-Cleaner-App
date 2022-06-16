package com.wolfpackdigital.kliner.partner.core.auth.registration.idcard

import androidx.lifecycle.ViewModelStoreOwner
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.wolfpackdigital.kliner.partner.IdentityCardBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.country.SelectCountryBottomSheetDialog
import com.wolfpackdigital.kliner.partner.core.auth.dialogs.country.SelectCountryViewModel
import com.wolfpackdigital.kliner.partner.core.auth.registration.RegistrationViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.requestCameraPermissions
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import io.reactivex.disposables.Disposable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class IdentityCardFragment :
    BaseFragment<IdentityCardBinding, IdentityCardViewModel>(R.layout.fr_identity_card) {

    // Properties

    override val viewModel by viewModel<IdentityCardViewModel>()
    private val parentViewModel by
    sharedViewModel<RegistrationViewModel>(from = { parentFragment as ViewModelStoreOwner })

    private val dialogViewModel by viewModel<SelectCountryViewModel>()
    private val countryDialog by lazy { SelectCountryBottomSheetDialog() }
    private val picker by lazy { RxImagePicker.with(childFragmentManager) }

    // Lifecycle

    override fun setupViews() {
        binding?.dialogViewModel = dialogViewModel
        dialogViewModel.loadCountries()
        setupObservers()
    }

    // Setup

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                IdentityCardViewModel.Command.OpenCountryPicker -> countryDialog.show(
                    childFragmentManager,
                    SelectCountryBottomSheetDialog::class.simpleName
                )
                IdentityCardViewModel.Command.ChangeIdCardFrontSide -> pickImage {
                    picker.requestImage(Sources.CHOOSER).subscribe { uri ->
                        viewModel.idCardFrontSideUri.value = uri
                    }
                }
                IdentityCardViewModel.Command.ChangeIdCardBackSide -> pickImage {
                    picker.requestImage(Sources.CHOOSER).subscribe { uri ->
                        viewModel.idCardBackSideUri.value = uri
                    }
                }
                IdentityCardViewModel.Command.ChangePassport -> pickImage {
                    picker.requestImage(Sources.CHOOSER).subscribe { uri ->
                        viewModel.passportUri.value = uri
                    }
                }
                IdentityCardViewModel.Command.ChangeResidencePermit -> pickImage {
                    picker.requestImage(Sources.CHOOSER).subscribe { uri ->
                        viewModel.residencePermitUri.value = uri
                    }
                }
                IdentityCardViewModel.Command.GoNext ->
                    parentViewModel.goToNextPage()
            }
        }
        dialogViewModel.selectedItem.observe(viewLifecycleOwner) {
            viewModel.country.value = it?.name
        }
    }

    private fun pickImage(function: () -> Disposable) {
        requestCameraPermissions(
            onGranted = { function.invoke() },
            onNotGranted = { snackBar(getString(R.string.camera_permissions_required)) }
        )
    }

    companion object {
        fun newInstance() = IdentityCardFragment()
    }
}
