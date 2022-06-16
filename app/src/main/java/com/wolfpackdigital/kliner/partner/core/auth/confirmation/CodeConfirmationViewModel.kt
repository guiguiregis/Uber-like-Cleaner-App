@file:Suppress("TooManyFunctions")

package com.wolfpackdigital.kliner.partner.core.auth.confirmation

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.MainActivity
import com.wolfpackdigital.kliner.partner.data.models.CleanerStatus
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.ConfirmPhoneNumberUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.Kind
import com.wolfpackdigital.kliner.partner.shared.useCases.OnboardingStatus
import com.wolfpackdigital.kliner.partner.shared.useCases.ResendCodeRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.ResendCodeUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SendFCMTokenUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UserRegisterFlowData
import com.wolfpackdigital.kliner.partner.shared.useCases.VerifyPhoneRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.VerifyPhoneResult
import com.wolfpackdigital.kliner.partner.shared.useCases.VerifyPhoneUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.NavigationKeys.ARG_CLEANER_ID
import com.wolfpackdigital.kliner.partner.shared.utils.NavigationKeys.ARG_USER_FLOW_DATA
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

const val PIN_CODE_LEN = 4

class CodeConfirmationViewModel(
    val phoneNumber: String?,
    val isEditMode: Boolean = false,
    private val verifyPhoneUseCase: VerifyPhoneUseCase,
    private val resendCodeUseCase: ResendCodeUseCase,
    private val sendFCMTokenUseCase: SendFCMTokenUseCase,
    private val confirmPhoneNumberUseCase: ConfirmPhoneNumberUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val letter1 = MutableLiveData<String>()
    val letter2 = MutableLiveData<String>()
    val letter3 = MutableLiveData<String>()
    val letter4 = MutableLiveData<String>()

    private val lettersObserver = Observer<String> {
        val code = "${letter1.value}${letter2.value}${letter3.value}${letter4.value}"
        finalCode.value = code
    }
    val finalCode = MediatorLiveData<String>()

    init {
        finalCode.addSource(letter1, lettersObserver)
        finalCode.addSource(letter2, lettersObserver)
        finalCode.addSource(letter3, lettersObserver)
        finalCode.addSource(letter4, lettersObserver)
    }

    fun verifyCode(code: String) {
        performApiCall {
            if (code.length != PIN_CODE_LEN)
                return@performApiCall
            if (isEditMode) confirmPhoneNumberEdit(code) else verifyPhoneNumberOnLogin(code)
        }
    }

    private suspend fun confirmPhoneNumberEdit(code: String) {
        when (confirmPhoneNumberUseCase.executeNow(code)) {
            is Result.Success -> _baseCmd.value = BaseCommand.GoBack
            is Result.Error -> handleCodeConfirmationError()
        }
    }

    private suspend fun verifyPhoneNumberOnLogin(code: String) {
        phoneNumber ?: return
        when (val response = verifyPhoneUseCase.executeNow(VerifyPhoneRequest(phoneNumber, code))) {
            is Result.Success -> handleSuccessfulLogin(response.data)
            is Result.Error -> handleCodeConfirmationError()
        }
    }

    private suspend fun handleSuccessfulLogin(result: VerifyPhoneResult) {
        token = result.tokens
        sendFCMTokenUseCase.executeNow(Unit)
        handleProfileState(
            result.onboardingStatus,
            result.companyID,
            result.cleanerID,
            result.cleanerKind,
            result.cleanerStatus,
            result.contractSignedAt
        )
    }

    private fun handleCodeConfirmationError() {
        clearCode()
        _cmd.value = Command.FocusFirstLetter
        _baseCmd.value = BaseCommand.ShowSnackbar("Invalid code")
    }

    private fun clearCode() {
        letter1.value = ""
        letter2.value = ""
        letter3.value = ""
        letter4.value = ""
    }

    @Suppress("LongParameterList", "LongMethod", "ComplexMethod")
    private fun handleProfileState(
        onboardingStatus: OnboardingStatus,
        companyID: Int?,
        cleanerID: Int?,
        cleanerKind: Kind?,
        cleanerStatus: CleanerStatus?,
        contractSignedAt: String?
    ) {
        this.companyID = companyID
        cleanerProfile = CleanerProfile(
            id = cleanerID,
            status = cleanerStatus,
            onboardingStatus = onboardingStatus
        )
        this.contractSignedAt = contractSignedAt
        when (onboardingStatus) {
            OnboardingStatus.NEW -> {
                cleanerProfile = null
                _cmd.value = Command.ShowWorkWithKlinerDialog
            }
            OnboardingStatus.PARTIAL -> handlePartialOnboardingState(cleanerKind, cleanerID)
            OnboardingStatus.COMPLETE ->
                handleCompleteOnboardingState(cleanerKind, cleanerStatus, contractSignedAt)
        }
    }

    private fun handlePartialOnboardingState(
        cleanerKind: Kind?,
        cleanerID: Int?
    ) {
        clearCode()
        when (cleanerKind) {
            Kind.EMPLOYEE -> {
                _baseCmd.value = BaseCommand.PerformNavById(
                    R.id.onboardingEmployeeFragment, bundleOf(
                        ARG_CLEANER_ID to cleanerID
                    )
                )
            }
            Kind.EMPLOYER,
            Kind.INDEPENDENT -> {
                _baseCmd.value = BaseCommand.PerformNavById(
                    R.id.registrationFragment,
                    bundleOf(
                        ARG_USER_FLOW_DATA to UserRegisterFlowData(
                            OnboardingStatus.PARTIAL,
                            cleanerKind
                        )
                    )
                )
            }
        }
    }

    private fun handleCompleteOnboardingState(
        cleanerKind: Kind?,
        cleanerStatus: CleanerStatus?,
        contractSignedAt: String?
    ) {
        _baseCmd.value = when (cleanerKind) {
            Kind.EMPLOYEE ->
                BaseCommand.PerformNavById(
                    R.id.mainActivity,
                    extras = MainActivity.getMainActivityExtras()
                )
            else ->
                when (cleanerStatus) {
                    CleanerStatus.ACTIVE -> {
                        if (contractSignedAt != null && !showNotificationSetting && !showGeolocationSetting)
                            BaseCommand.PerformNavById(
                                R.id.mainActivity,
                                extras = MainActivity.getMainActivityExtras()
                            )
                        else
                        // The user might be activated but
                        // he did not sign the contract
                            BaseCommand.PerformNavById(R.id.userVerificationFragment)
                    }
                    else -> BaseCommand.PerformNavById(R.id.userVerificationFragment)
                }
        }
    }

    fun resendCode() {
        performApiCall {
            phoneNumber ?: return@performApiCall
            when (resendCodeUseCase.executeNow(ResendCodeRequest(phoneNumber))) {
                is Result.Success -> _baseCmd.value =
                    BaseCommand.ShowSnackbar("You requested a new code")
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar("Error")
            }
        }
    }

    fun onEntrepreneur() {
        clearCode()
        _cmd.value = Command.HideDialog
        _baseCmd.value = BaseCommand.PerformNavById(
            R.id.registrationFragment,
            bundleOf(
                ARG_USER_FLOW_DATA to UserRegisterFlowData(
                    OnboardingStatus.NEW,
                    Kind.INDEPENDENT
                )
            )
        )
    }

    fun onSME() {
        clearCode()
        _cmd.value = Command.HideDialog
        _baseCmd.value = BaseCommand.PerformNavById(
            R.id.registrationFragment,
            bundleOf(
                ARG_USER_FLOW_DATA to UserRegisterFlowData(
                    OnboardingStatus.NEW,
                    Kind.EMPLOYER
                )
            )
        )
    }

    sealed class Command {
        object ShowWorkWithKlinerDialog : Command()
        object FocusFirstLetter : Command()
        object HideDialog : Command()
    }
}
