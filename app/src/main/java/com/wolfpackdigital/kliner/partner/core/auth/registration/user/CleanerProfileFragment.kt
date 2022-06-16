package com.wolfpackdigital.kliner.partner.core.auth.registration.user

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.wolfpackdigital.kliner.partner.CleanerProfileBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.auth.registration.RegistrationViewModel
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.useCases.Kind
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.extraNotNull
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.requestCameraPermissions
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.snackBar
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toInstant
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toLocalDateTimeOrNull
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CleanerProfileFragment :
    BaseFragment<CleanerProfileBinding, CleanerProfileViewModel>(R.layout.fr_cleaner_profile) {

    // Properties
    private val userKind by extraNotNull<Kind>(USER_KIND)
    override val viewModel by viewModel<CleanerProfileViewModel> {
        parametersOf(userKind)
    }
    private val parentViewModel by
    sharedViewModel<RegistrationViewModel>(from = { parentFragment as ViewModelStoreOwner })

    private val todayCalendar: LocalDateTime
        get() = LocalDateTime.now()

    private val initialSelectedDate by lazy {
        todayCalendar
            .minus(Constants.REGISTER_MINIMUM_YEARS, ChronoUnit.YEARS)
            .minus(1L, ChronoUnit.DAYS)
            .toInstant().toEpochMilli()
    }

    private val datePicker by lazy {
        MaterialDatePicker.Builder
            .datePicker()
            .setTitleText(getString(R.string.datepicker_title))
            .setTheme(R.style.DatePickerStyleOverlay)
            .setSelection(initialSelectedDate)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setOpenAt(initialSelectedDate)
                    .setValidator(DateValidatorPointBackward.before(initialSelectedDate))
                    .build()
            )
            .build().apply {
                addOnPositiveButtonClickListener {
                    val selected = Instant.ofEpochMilli(it).toLocalDateTimeOrNull()
                    viewModel.birthDate.value = selected.toLocalDate().toString()
                }
            }
    }

// Lifecycle

    override fun setupViews() {
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshProfileData()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is CleanerProfileViewModel.Command.OpenImagePicker -> requestCameraPermissions(
                    onGranted = {
                        RxImagePicker.with(childFragmentManager)
                            .requestImage(Sources.CHOOSER)
                            .subscribe { uri ->
                                viewModel.profileImageUri.value = uri
                            }
                    },
                    onNotGranted = { snackBar(getString(R.string.camera_permissions_required)) }
                )
                is CleanerProfileViewModel.Command.OpenDatePicker -> {
                    if (!datePicker.isAdded)
                        datePicker.show(childFragmentManager, MaterialDatePicker::class.simpleName)
                }
                is CleanerProfileViewModel.Command.GoNext -> parentViewModel.goToNextPage()
            }
        }
    }

    companion object {
        private const val USER_KIND = "user_kind"

        fun newInstance(kind: Kind) = CleanerProfileFragment().apply {
            arguments = bundleOf(USER_KIND to kind)
        }
    }
}
