package com.wolfpackdigital.kliner.partner.core.auth.welcome

import androidx.lifecycle.LiveData
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent

class WelcomeViewModel : BaseViewModel() {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    // Actions

    fun openPhoneConfirmation() {
        _baseCmd.value = BaseCommand.PerformNavById(R.id.phoneConfirmationFragment)
    }

    // Command

    sealed class Command
}
