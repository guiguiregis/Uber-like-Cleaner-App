package com.wolfpackdigital.kliner.partner.core.auth.onboarding

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class OnboardingViewModel : BaseViewModel(), PersistenceService {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val btnContinueText = MutableLiveData<@StringRes Int>(R.string.onboarding_btn_continue)
    val skipVisible = MutableLiveData(true)

    fun pageSelected(page: Int) {
        when (page) {
            0, 1 -> {
                btnContinueText.value = R.string.onboarding_btn_continue
                skipVisible.value = true
            }
            2 -> {
                btnContinueText.value = R.string.onboarding_btn_start
                skipVisible.value = false
            }
            else -> {
                btnContinueText.value = R.string.onboarding_btn_continue
                skipVisible.value = true
            }
        }
    }

    fun nextPage() {
        _cmd.value = Command.NextPage
    }

    fun skipOnboarding() {
        _cmd.value = Command.SkipOnboarding
    }

    fun openWelcomeScreen() {
        showOnboarding = false
        _baseCmd.value =
            BaseCommand.PerformNavAction(OnboardingFragmentDirections.actionOnboardingFragmentToWelcomeFragment())
    }

    sealed class Command {
        object NextPage : Command()
        object SkipOnboarding : Command()
    }
}
