package com.wolfpackdigital.kliner.partner.core.auth.phone

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.navigation.NavOptions
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.SignInRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.SignInUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdatePhoneNumberUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.NavigationKeys.ARG_IS_EDIT_MODE
import com.wolfpackdigital.kliner.partner.shared.utils.NavigationKeys.ARG_PHONE_NUMBER
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getExampleNumber
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getRegionCode
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getValidNumber

class PhoneConfirmationViewModel(
    prefix: String,
    number: String?,
    private val signInUseCase: SignInUseCase,
    private val updatePhoneNumberUseCase: UpdatePhoneNumberUseCase
) : BaseViewModel() {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val phoneUtil = PhoneNumberUtil.getInstance()

    val phonePrefix = MutableLiveData(prefix)
    val phoneNumber = MutableLiveData<String>(number)
    val phoneNumberHint = phonePrefix.map { prefix ->
        val hintPrefix = prefix ?: Constants.DEFAULT_PHONE_PREFIX
        hintPrefix.toIntOrNull()?.let {
            _cmd.value = Command.ChangeTextWatcher(phoneUtil.getRegionCode(it))
            phoneUtil.getExampleNumber(it) ?: ""
        } ?: ""
    }

    val description = MutableLiveData(R.string.phone_confirmation_description)
    val descriptionColor = MutableLiveData(R.color.textSecondaryColor)

    val isEditMode = MutableLiveData(!number.isNullOrEmpty())

    fun onContinue() {
        val number = phoneUtil.getValidNumber(phonePrefix.value, phoneNumber.value)
        number?.let {
            description.value = R.string.phone_confirmation_description
            descriptionColor.value = R.color.textSecondaryColor
            if (isEditMode.value == true) {
                updatePhoneNumber(number)
            } else {
                signIn(number)
            }
        } ?: run {
            description.value = R.string.phone_confirmation_error
            descriptionColor.value = R.color.textErrorColor
        }
    }

    private fun updatePhoneNumber(number: String) {
        performApiCall {
            when (val result = updatePhoneNumberUseCase.executeNow(number)) {
                is Result.Success -> {
                    confirmEditedPhoneNumber(number)
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    private fun confirmEditedPhoneNumber(number: String) {
        val options = NavOptions.Builder().setPopUpTo(R.id.profileDetailsFragment, false).build()
        _baseCmd.value = BaseCommand.PerformNavById(
            R.id.codeConfirmationFragment,
            bundleOf(
                ARG_PHONE_NUMBER to number,
                ARG_IS_EDIT_MODE to true
            ),
            options
        )
    }

    private fun signIn(number: String) {
        performApiCall {
            when (val response = signInUseCase.executeNow(SignInRequest(number))) {
                is Result.Success ->
                    _baseCmd.value = BaseCommand.PerformNavById(
                        R.id.codeConfirmationFragment,
                        bundleOf(
                            ARG_PHONE_NUMBER to number,
                            ARG_IS_EDIT_MODE to false
                        )
                    )
                is Result.Error ->
                    _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
            }
        }
    }

    fun openPrefixPicker() {
        _cmd.value = Command.OpenPrefixPicker
    }

    // Actions

    // Command

    sealed class Command {
        object OpenPrefixPicker : Command()
        class ChangeTextWatcher(val regionCode: String) : Command()
    }
}
