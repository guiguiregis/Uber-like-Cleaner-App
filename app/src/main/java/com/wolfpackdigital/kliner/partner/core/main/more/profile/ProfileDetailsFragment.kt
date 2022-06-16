package com.wolfpackdigital.kliner.partner.core.main.more.profile

import android.annotation.SuppressLint
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.wolfpackdigital.kliner.partner.ProfileDetailsBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.adapters.CheckboxAdapter
import com.wolfpackdigital.kliner.partner.core.main.more.profile.dialogs.delete.DeleteAccountBottomSheetDialog
import com.wolfpackdigital.kliner.partner.core.main.more.profile.dialogs.delete.DeleteAccountViewModel
import com.wolfpackdigital.kliner.partner.core.main.more.profile.dialogs.logout.LogoutBottomSheetDialog
import com.wolfpackdigital.kliner.partner.core.main.more.profile.dialogs.logout.LogoutViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.useCases.PhotoTypes
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.downloadFile
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.requestCameraPermissions
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import io.reactivex.disposables.Disposable
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileDetailsFragment :
    BaseFragment<ProfileDetailsBinding, ProfileDetailsViewModel>(R.layout.fr_profile_details) {

    // Properties

    override val viewModel by viewModel<ProfileDetailsViewModel>()

    private val logoutDialogViewModel by viewModel<LogoutViewModel>()
    private val logoutDialog by lazy { LogoutBottomSheetDialog() }

    private val deleteAccountViewModel by viewModel<DeleteAccountViewModel>()
    private val deleteAccountDialog by lazy { DeleteAccountBottomSheetDialog() }

    private val picker by lazy { RxImagePicker.with(childFragmentManager) }

    private val typesOfServiceAdapter by lazy { CheckboxAdapter() }
    private val typesOfWorkAdapter by lazy { CheckboxAdapter() }

    // Lifecycle

    override fun setupViews() {
        binding?.rvTypesOfService?.adapter = typesOfServiceAdapter
        binding?.rvTypesOfWork?.adapter = typesOfWorkAdapter
        viewModel.fetchData()
        setupObservers()
    }

    @Suppress("ComplexMethod")
    private fun setupObservers() {
        viewModel.typesOfService.observe(viewLifecycleOwner, typesOfServiceAdapter::submitList)
        viewModel.typesOfWork.observe(viewLifecycleOwner, typesOfWorkAdapter::submitList)
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                ProfileDetailsViewModel.Command.ShowLogoutDialog ->
                    logoutDialog.show(
                        childFragmentManager,
                        LogoutBottomSheetDialog::class.simpleName
                    )
                ProfileDetailsViewModel.Command.ShowDeleteAccountDialog ->
                    deleteAccountDialog.show(
                        childFragmentManager,
                        DeleteAccountBottomSheetDialog::class.simpleName
                    )
                ProfileDetailsViewModel.Command.OpenImagePicker -> pickImage {
                    picker.requestImage(Sources.CHOOSER).subscribe { uri ->
                        viewModel.uploadProfilePhoto(uri)
                    }
                }
                ProfileDetailsViewModel.Command.UploadNovaCertificate -> pickCertificateImage(
                    PhotoTypes.NOVA_CERTIFICATE
                )
                ProfileDetailsViewModel.Command.UploadInsuranceCertificate -> pickCertificateImage(
                    PhotoTypes.INSURANCE_CERTIFICATE
                )
                is ProfileDetailsViewModel.Command.DownloadFile ->
                    requireContext().downloadFile(it.uri)
            }
        }

        logoutDialogViewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                LogoutViewModel.Command.ReturnToLogin -> {
                    activity?.finish()
                    viewModel.openAuthActivity()
                }
            }
        }

        deleteAccountViewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                DeleteAccountViewModel.Command.ReturnToLogin -> {
                    activity?.finish()
                    viewModel.openAuthActivity()
                }
            }
        }

        viewModel.selectedWorkTypes.observe(viewLifecycleOwner) {
            viewModel.updateTypes()
        }
        viewModel.selectedServiceTypes.observe(viewLifecycleOwner) {
            viewModel.updateTypes()
        }
    }

    @SuppressLint("CheckResult")
    private fun pickCertificateImage(type: PhotoTypes) {
        pickImage {
            picker.requestImage(Sources.CHOOSER).subscribe { uri ->
                viewModel.uploadCertificate(uri, type)
            }
        }
    }

    private fun pickImage(function: () -> Disposable) {
        requestCameraPermissions(
            onGranted = { function.invoke() },
            onNotGranted = { snackBar(getString(R.string.camera_permissions_required)) }
        )
    }
}
