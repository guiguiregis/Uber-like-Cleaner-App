package com.wolfpackdigital.kliner.partner.core.main.offers.assign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.AssignEmployeeUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.AssignmentScope
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.CompanyCleanersRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCompanyCleanersUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.OnboardingStatus
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateMissionRequest
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class AssignEmployeeViewModel(
    private val missionId: Int,
    private val isRecurrentMission: Boolean,
    private val isRecurrenceChange: Boolean,
    getCompanyCleanersUseCase: GetCompanyCleanersUseCase,
    private val assignEmployeeUseCase: AssignEmployeeUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _cleaners = MutableLiveData<List<CleanerProfile>>()
    val cleaners: LiveData<List<CleanerProfile>> = _cleaners

    private val selectedEmployee = MutableLiveData<CleanerProfile>()
    val isSelected = selectedEmployee.map { it != null }

    init {
        performApiCall {
            when (val result = getCompanyCleanersUseCase.executeNow(
                CompanyCleanersRequest(companyID ?: -1, missionId)
            )) {
                is Result.Success -> _cleaners.value =
                    result.data.filter { it.onboardingStatus == OnboardingStatus.COMPLETE }
                else -> _cleaners.value = listOf()
            }
        }
    }

    // Actions

    fun onEmployeeSelected(employee: CleanerProfile?) {
        selectedEmployee.value = employee
    }

    fun onConfirmEmployee() {
        selectedEmployee.value?.id?.let {
            if (isRecurrenceChange) goToRecurrenceChangeDialog(it) else assignEmployee(it)
        }
    }

    fun onOpenInviteEmployee() {
        _cmd.value = Command.OpenInviteEmployee
    }

    fun onCloseDialog() {
        _cmd.value = Command.CloseDialog
    }

    private fun assignEmployee(employeeId: Int) {
        val assignmentScope = if (isRecurrentMission)
            AssignmentScope.MULTIPLE else AssignmentScope.SINGLE
        performApiCall {
            when (val result =
                assignEmployeeUseCase.executeNow(
                    UpdateMissionRequest(missionId, employeeId, assignmentScope)
                )) {
                is Result.Success -> _cmd.value = Command.RefreshMissionDetails
                is Result.Error -> _cmd.value = Command.ShowMessage(result.error)
            }
            onCloseDialog()
        }
    }

    private fun goToRecurrenceChangeDialog(employeeId: Int) {
        onCloseDialog()
        _cmd.value = Command.OpenChangeRecurrence(missionId, employeeId)
    }

    // Command

    sealed class Command {
        object OpenInviteEmployee : Command()
        data class OpenChangeRecurrence(val missionId: Int, val employeeId: Int) : Command()
        data class ShowMessage(val message: String) : Command()
        object CloseDialog : Command()
        object RefreshMissionDetails : Command()
    }
}
