package com.wolfpackdigital.kliner.partner.core.main.more.profile.gdpr

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent

class GdprViewModel : BaseViewModel() {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    // Actions

    fun onOpenTermsOfService() {
        _cmd.value = Command.OpenTermsOfService(R.string.terms_of_service_url)
    }

    fun onOpenPrivacyPolicy() {
        _cmd.value = Command.OpenPrivacyPolicy(R.string.privacy_policy_url)
    }

    // Command

    sealed class Command {
        data class OpenTermsOfService(@StringRes val url: Int) : Command()
        data class OpenPrivacyPolicy(@StringRes val url: Int) : Command()
    }
}
