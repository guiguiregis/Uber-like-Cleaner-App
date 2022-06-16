package com.wolfpackdigital.kliner.partner.core.auth.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent

class RegistrationViewModel(
    private val totalCount: Int
) : BaseViewModel() {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _currentPageString = MutableLiveData<String>()
    val currentPageString: LiveData<String> = _currentPageString

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int> = _currentPage

    fun onPageSelected(position: Int) {
        _currentPage.value = position
        _currentPageString.value = "${position + 1}/$totalCount"
    }

    fun goToNextPage() {
        _currentPage.value = ((currentPage.value ?: return) + 1) % totalCount
    }

    fun goToPreviousPage() {
        _currentPage.value = (currentPage.value ?: return) - 1
    }

    // Actions

    // Command

    sealed class Command
}
