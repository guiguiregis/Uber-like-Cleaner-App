package com.wolfpackdigital.kliner.partner.core.main.more.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.GetActivityRecordsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCompanyActivityUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.TransactionItem
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class ActivityViewModel(
    getCompanyActivityUseCase: GetCompanyActivityUseCase,
    getActivityRecordsUseCase: GetActivityRecordsUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _companyActivity = MutableLiveData<List<TransactionItem>>()
    val companyActivity: LiveData<List<TransactionItem>> = _companyActivity

    private val _missionsCompleted = MutableLiveData<Number>()
    val missionsCompleted: LiveData<Number> = _missionsCompleted

    private val _totalRevenue = MutableLiveData<Number>()
    val totalRevenue: LiveData<Number> = _totalRevenue

    private val _totalHours = MutableLiveData<Number>()
    val totalHours: LiveData<Number> = _totalHours

    val isEmployee = cleanerProfile?.isEmployee() ?: true

    init {
        performApiCall {
            if (!isEmployee)
                when (val result = getCompanyActivityUseCase.executeNow(Unit)) {
                    is Result.Success -> _companyActivity.value = result.data?.map {
                        it.copy(onClick = { item -> onItemClicked(item) })
                    }
                    is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
                }
            when (val result = getActivityRecordsUseCase.executeNow(Unit)) {
                is Result.Success -> {
                    _missionsCompleted.value = result.data.totalMissions
                    _totalRevenue.value = result.data.totalRevenue
                    _totalHours.value = result.data.totalDuration
                }
            }
        }
    }

    // Actions

    private fun onItemClicked(item: TransactionItem) {
        // TODO
    }

    // Command

    sealed class Command
}
