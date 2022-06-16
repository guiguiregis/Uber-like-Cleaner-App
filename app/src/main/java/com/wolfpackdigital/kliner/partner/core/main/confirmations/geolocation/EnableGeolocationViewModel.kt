package com.wolfpackdigital.kliner.partner.core.main.confirmations.geolocation

import androidx.lifecycle.LiveData
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class EnableGeolocationViewModel : BaseViewModel(),
    PersistenceService {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    fun onAcceptGeolocation() {
        _cmd.value = Command.RequestLocationPermissions
    }

    fun onGeolocationAccepted() {
        showGeolocationSetting = false
        if (cleanerProfile?.isEmployer() == true)
            _baseCmd.value = BaseCommand.PerformNavAction(
                EnableGeolocationFragmentDirections
                    .actionEnableGeolocationFragmentOnboardingToInviteEmployeeFragment(
                        true
                    )
            )
        else
            _cmd.value = Command.GoToDashboard
    }

    sealed class Command {
        object RequestLocationPermissions : Command()
        object GoToDashboard : Command()
    }
}
