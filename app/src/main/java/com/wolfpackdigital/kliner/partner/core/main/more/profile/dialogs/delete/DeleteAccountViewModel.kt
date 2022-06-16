package com.wolfpackdigital.kliner.partner.core.main.more.profile.dialogs.delete

import androidx.lifecycle.LiveData
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.DeleteAccountUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class DeleteAccountViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase
) : BaseViewModel(), PersistenceService {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    fun onDeleteAccount() {
        performApiCall {
            when (val result = deleteAccountUseCase.executeNow(Unit)) {
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
