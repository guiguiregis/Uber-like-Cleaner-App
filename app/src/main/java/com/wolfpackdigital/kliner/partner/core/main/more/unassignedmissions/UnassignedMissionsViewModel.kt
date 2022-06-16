package com.wolfpackdigital.kliner.partner.core.main.more.unassignedmissions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.GetUnassignedMissionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.Mission
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent

class UnassignedMissionsViewModel(private val getUnassignedMissionsUseCase: GetUnassignedMissionsUseCase) :
    BaseViewModel() {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _unassignedMissions = MutableLiveData<List<Mission>>()
    val unassignedMissions: LiveData<List<Mission>> = _unassignedMissions

    fun fetchMissions() {
        performApiCall {
            when (val result = getUnassignedMissionsUseCase.executeNow(Unit)) {
                is Result.Success -> _unassignedMissions.value = result.data
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    val onMissionSelected: (Mission) -> Unit = {
        _baseCmd.value = it.id?.let { id ->
            BaseCommand.PerformNavAction(
                UnassignedMissionsFragmentDirections.actionUnassignedMissionsFragmentToNavMissionDetails(
                    id
                )
            )
        } ?: BaseCommand.ShowSnackbarById(R.string.error_no_mission_id)
    }

    // Actions

    // Command

    sealed class Command
}
