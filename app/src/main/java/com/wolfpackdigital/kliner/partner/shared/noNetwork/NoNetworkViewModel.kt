package com.wolfpackdigital.kliner.partner.shared.noNetwork

import androidx.lifecycle.LiveData
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.Variables

class NoNetworkViewModel : BaseViewModel() {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    // Actions

    fun onNetworkRetry() {
        if (Variables.isNetworkConnected) {
            _cmd.value = Command.CloseDialog
        }
    }

    // Command

    sealed class Command {
        object CloseDialog : Command()
    }
}
