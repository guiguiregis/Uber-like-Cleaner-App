package com.wolfpackdigital.kliner.partner.core.main.confirmations.notifications

import androidx.lifecycle.LiveData
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.EnableNotificationsUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class EnableNotificationsViewModel(
    private val enableNotificationsUseCase: EnableNotificationsUseCase
) : BaseViewModel(), PersistenceService {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    fun onConfirmNotifications(enabled: Boolean) {
        performApiCall {
            when (val result = enableNotificationsUseCase.executeNow(enabled)) {
                is Result.Success -> {
                    showNotificationSetting = false
                    _baseCmd.value = BaseCommand.PerformNavAction(
                        EnableNotificationsFragmentDirections
                            .actionEnableNotificationsFragmentToEnableGeolocationFragmentOnboarding()
                    )
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    sealed class Command
}
