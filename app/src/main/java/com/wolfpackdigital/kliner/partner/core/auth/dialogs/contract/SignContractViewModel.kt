package com.wolfpackdigital.kliner.partner.core.auth.dialogs.contract

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolfpackdigital.kliner.partner.BuildConfig
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.SignContractUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toInstant
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import java.time.LocalDateTime

class SignContractViewModel(
    private val signContractUseCase: SignContractUseCase
) : BaseViewModel(), PersistenceService {
    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val termsConditionsAccepted = MutableLiveData(false)

    fun onViewContract() {
        _cmd.value = Command.ShowContract(
            "${BuildConfig.BASE_URL}contracts/partnership.pdf",
            token?.accessToken
        )
    }

    fun onSignContract() {
        performApiCall {
            val formattedTime = LocalDateTime.now().toInstant().toString()
            when (val result = signContractUseCase.executeNow(formattedTime)) {
                is Result.Success -> {
                    contractSignedAt = formattedTime
                    close()
                    _cmd.value = Command.ContractSigned
                }
                is Result.Error -> _cmd.value = Command.ShowSnackBar(result.error)
            }
        }
    }

    fun close() {
        _cmd.value = Command.CloseDialog
    }

    sealed class Command {
        data class ShowContract(val url: String, val accessToken: String?) : Command()
        object CloseDialog : Command()
        object ContractSigned : Command()
        data class ShowSnackBar(val message: String) : Command()
    }
}
