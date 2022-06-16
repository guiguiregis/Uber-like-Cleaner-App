package com.wolfpackdigital.kliner.partner.core.main.more.profile.dialogs.logout

import androidx.lifecycle.LiveData
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.LogoutUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class LogoutViewModel(
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel(), PersistenceService {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    fun onLogout() {
        performApiCall {
            when (val result = logoutUseCase.executeNow(Unit)) {
                is Result.Success -> {
                    deleteAllUserData()
                    _cmd.value = Command.ReturnToLogin
                }
                is Result.Error -> {
                    _cmd.value = Command.ShowSnackBar(result.error)
                }
            }
        }
    }

    fun close() {
        _cmd.value = Command.CloseDialog
    }

    sealed class Command {
        object CloseDialog : Command()
        object ReturnToLogin : Command()
        data class ShowSnackBar(val message: String) : Command()
    }
}
