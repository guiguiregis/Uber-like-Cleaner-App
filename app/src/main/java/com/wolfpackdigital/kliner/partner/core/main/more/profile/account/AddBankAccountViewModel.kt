package com.wolfpackdigital.kliner.partner.core.main.more.profile.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.BankAccount
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateBankAccountUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateBankAccountUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import nl.garvelink.iban.CountryCodes
import nl.garvelink.iban.IBAN

private const val COUNTRY_CODE_LENGTH = 2

class AddBankAccountViewModel(
    bankAccount: BankAccount?,
    private val createBankAccountUseCase: CreateBankAccountUseCase,
    private val updateBankAccountUseCase: UpdateBankAccountUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val existingBankAccount = MutableLiveData<BankAccount>()

    val bankAccountTitle = MutableLiveData(R.string.add_bank_account)
    val accountHolderName = MutableLiveData<String>()
    val errorAccountHolderName = MutableLiveData<Int>(null)
    val iban = MutableLiveData<String>()
    val ibanValidation = iban.map {
        if (it.contains(" ")) {
            iban.value = it.replace(" ", "")
        }
        it.length == CountryCodes.getLengthForCountryCode(getCountryCode(it)) && isSepaCompliant(it)
    }

    val errorIban = MutableLiveData<Int>(null)

    // Actions

    init {
        existingBankAccount.value = bankAccount
        bankAccount?.let {
            bankAccountTitle.value = R.string.edit_bank_account
            accountHolderName.value = it.accountHolderName
        }
    }

    @Suppress("ComplexMethod")
    private fun validateBankAccount(account: BankAccount, onAccountValid: suspend () -> Unit) {
        var error = false
        errorAccountHolderName.value = if (account.accountHolderName.isNullOrEmpty()) {
            error = true
            R.string.error_account_holder_name
        } else
            null
        errorIban.value = if (account.iban.isNullOrEmpty() || ibanValidation.value != true) {
            error = true
            R.string.iban_sepa_error
        } else
            null
        if (!error)
            performApiCall { onAccountValid() }
    }

    private fun getCountryCode(iban: String?) = iban?.let {
        if (iban.length >= COUNTRY_CODE_LENGTH) iban.substring(0, COUNTRY_CODE_LENGTH) else ""
    } ?: ""

    private fun isSepaCompliant(input: String?): Boolean = try {
        val iban = IBAN.valueOf(input)
        iban.isSEPA
    } catch (exception: IllegalArgumentException) {
        false
    }

    fun onNext() {
        val account = BankAccount(
            accountHolderName = accountHolderName.value,
            iban = iban.value
        )
        if (existingBankAccount.value == null) {
            validateBankAccount(account) {
                when (val response = createBankAccountUseCase.executeNow(account)) {
                    is Result.Success -> {
                        _baseCmd.value = BaseCommand.GoBack
                    }
                    is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
                }
            }
        } else {
            validateBankAccount(account) {
                when (val response = updateBankAccountUseCase.executeNow(account)) {
                    is Result.Success -> {
                        _baseCmd.value = BaseCommand.GoBack
                    }
                    is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
                }
            }
        }
    }

    // Command

    sealed class Command
}
