package com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.AssignEmployeeUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.AssignmentScope
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateMissionRequest
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class ChangeMissionRecurrenceViewModel(
    private val missionId: Int,
    private val employeeId: Int,
    private val assignEmployeeUseCase: AssignEmployeeUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val thisMissionSelected = MutableLiveData(true)
    val allRecurrenceSelected = MutableLiveData(false)

    // Actions

    fun onCloseDialog() {
        _cmd.value = Command.CloseDialog
    }

    fun onThisMissionSelected() {
        thisMissionSelected.value = true
        allRecurrenceSelected.value = false
    }

    fun onAllRecurrenceSelected() {
        thisMissionSelected.value = false
        allRecurrenceSelected.value = true
    }

    fun onConfirmSelection() {
        val assignmentScope = when (allRecurrenceSelected.value) {
            true -> AssignmentScope.MULTIPLE
            else -> AssignmentScope.SINGLE
        }
        performApiCall {
            when (val result =
                assignEmployeeUseCase.executeNow(
                    UpdateMissionRequest(
                        missionId,
                        employeeId,
                        assignmentScope
                    )
                )) {
                is Result.Success -> _cmd.value = Command.RefreshMissionDetails
                is Result.Error -> _cmd.value = Command.ShowMessage(result.error)
            }
            onCloseDialog()
        }
    }

    // Command

    sealed class Command {
        class ShowMessage(val message: String) : Command()
        object CloseDialog : Command()
        object RefreshMissionDetails : Command()
    }
}
