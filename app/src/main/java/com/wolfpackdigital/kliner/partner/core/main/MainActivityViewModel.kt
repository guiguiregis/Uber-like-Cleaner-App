package com.wolfpackdigital.kliner.partner.core.main

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class MainActivityViewModel : ViewModel(), PersistenceService {

    private val _cmd = MutableLiveData<Command>()
    val cmd: LiveData<Command> = _cmd

    val selectedUnconfirmedMission = MutableLiveData<Int?>(null)

    fun toggleBottomNav(visible: Boolean) {
        _cmd.value = Command.BottomNavVisible(visible)
    }

    fun selectTab(@IdRes tab: Int) {
        _cmd.value = Command.SelectTab(tab)
    }

    sealed class Command {
        object SignOut : Command()
        data class SelectTab(@IdRes val pageId: Int) : Command()
        data class BottomNavVisible(val visible: Boolean) : Command()
    }
}
