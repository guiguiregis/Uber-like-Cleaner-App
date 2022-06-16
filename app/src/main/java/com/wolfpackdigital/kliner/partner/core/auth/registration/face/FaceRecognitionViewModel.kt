package com.wolfpackdigital.kliner.partner.core.auth.registration.face

import androidx.lifecycle.LiveData
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent

class FaceRecognitionViewModel : BaseViewModel() {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    // Actions

    // Command

    sealed class Command
}
