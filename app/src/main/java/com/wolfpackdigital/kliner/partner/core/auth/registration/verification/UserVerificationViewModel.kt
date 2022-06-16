package com.wolfpackdigital.kliner.partner.core.auth.registration.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.data.models.CleanerStatus
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class UserVerificationViewModel(
    private val getCleanerStatusUseCase: GetCleanerProfileUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val profile = MutableLiveData<CleanerProfile>(cleanerProfile)

    val screenState = profile.map { it.status }

    val titleText = screenState.map {
        when (it) {
            CleanerStatus.ACTIVE -> R.string.welcome
            else -> R.string.checking_in_progress
        }
    }

    val descriptionText = screenState.map {
        when (it) {
            CleanerStatus.ACTIVE -> R.string.user_verified_description
            else -> R.string.user_verification_description
        }
    }

    val textColor = screenState.map {
        when (it) {
            CleanerStatus.ACTIVE -> R.color.textColor
            else -> android.R.color.white
        }
    }

    val topBgColor = screenState.map {
        when (it) {
            CleanerStatus.ACTIVE -> R.color.colorAccent
            else -> R.color.bgBlack
        }
    }

    val bottomBgColor = screenState.map {
        when (it) {
            CleanerStatus.ACTIVE -> android.R.color.white
            else -> R.color.bgBlackSecondary
        }
    }

    // Actions

    fun onContinue() {
        if (contractSignedAt != null)
            checkNotificationsStatus()
        else
            _cmd.value = Command.ShowContractDialog
    }

    fun fetchCleanerStatus() {
        performApiCall {
            when (val result = getCleanerStatusUseCase.executeNow(cleanerProfile?.id ?: -1)) {
                is Result.Success -> {
                    profile.value = result.data
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    fun checkNotificationsStatus() {
        if (showNotificationSetting) {
            _baseCmd.value = BaseCommand.PerformNavAction(
                UserVerificationFragmentDirections.actionUserVerificationFragmentToEnableNotificationsFragment()
            )
        } else {
            checkGeolocationStatus()
        }
    }

    private fun checkGeolocationStatus() {
        if (showGeolocationSetting) {
            _baseCmd.value = BaseCommand.PerformNavAction(
                UserVerificationFragmentDirections.actionUserVerificationFragmentToEnableGeolocationFragmentOnboarding()
            )
        } else {
            if (cleanerProfile?.isEmployer() == true)
                _baseCmd.value = BaseCommand.PerformNavAction(
                    UserVerificationFragmentDirections.actionUserVerificationFragmentToInviteEmployeeFragment(
                        true
                    )
                )
            else
                _cmd.value = Command.GoToDashboard
        }
    }

    // Command

    sealed class Command {
        object ShowContractDialog : Command()
        object GoToDashboard : Command()
    }
}
