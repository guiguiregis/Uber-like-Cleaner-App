package com.wolfpackdigital.kliner.partner.core.main.employee.invite

import androidx.lifecycle.LiveData
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.MainActivity
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent

class InviteEmployeeViewModel(private val isOnboarding: Boolean = false) : BaseViewModel() {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    // Actions

    fun onNext() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            InviteEmployeeFragmentDirections.actionInviteEmployeeFragmentToAddEmployeeFragment(
                isOnboarding
            )
        )
    }

    fun onLater() {
        if (isOnboarding) {
            _baseCmd.value = BaseCommand.PerformNavById(
                R.id.mainActivity,
                extras = MainActivity.getMainActivityExtras()
            )
        } else
            _baseCmd.value = BaseCommand.GoBack
    }

    // Command

    sealed class Command
}
