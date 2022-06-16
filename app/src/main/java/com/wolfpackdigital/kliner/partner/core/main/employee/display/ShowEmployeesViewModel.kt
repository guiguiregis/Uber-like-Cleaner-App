package com.wolfpackdigital.kliner.partner.core.main.employee.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.CompanyCleanersRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCompanyCleanersUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class ShowEmployeesViewModel(
    private val getCompanyCleanersUseCase: GetCompanyCleanersUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _cleaners = MutableLiveData<List<CleanerProfile>>()
    val cleaners: LiveData<List<CleanerProfile>> = _cleaners

    // Actions

    fun fetchCleaners() {
        performApiCall {
            when (val result = getCompanyCleanersUseCase.executeNow(
                CompanyCleanersRequest(companyID ?: -1, null)
            )) {
                is Result.Success -> _cleaners.value = result.data.map {
                    it.copy(onClick = { item -> onItemClicked(item) })
                }
                else -> _cleaners.value = listOf()
            }
        }
    }

    private fun onItemClicked(item: CleanerProfile) {
        item.id?.let { id ->
            _baseCmd.value = BaseCommand.PerformNavAction(
                ShowEmployeesFragmentDirections.actionShowEmployeesFragmentToEmployeeDetailsFragment(
                    id
                )
            )
        }
    }

    fun onOpenAddEmployeeFragment() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            ShowEmployeesFragmentDirections.actionShowEmployeesFragmentToAddEmployeeFragment()
        )
    }

    // Command

    sealed class Command
}
