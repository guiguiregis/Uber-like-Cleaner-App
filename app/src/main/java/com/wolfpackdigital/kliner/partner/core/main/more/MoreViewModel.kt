package com.wolfpackdigital.kliner.partner.core.main.more

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.GetMoreSectionItemsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.MoreSectionItem
import com.wolfpackdigital.kliner.partner.shared.useCases.Type
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class MoreViewModel(
    getMoreSectionItemsUseCase: GetMoreSectionItemsUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _moreSection = MutableLiveData<List<MoreSectionItem>>()
    val moreSection: LiveData<List<MoreSectionItem>> = _moreSection

    private val _cleanerData = MutableLiveData<CleanerProfile>(cleanerProfile)
    val cleanerData: LiveData<CleanerProfile> = _cleanerData

    init {
        performApiCall {
            when (val result = getMoreSectionItemsUseCase.executeNow(Unit)) {
                is Result.Success -> _moreSection.value = result.data.map {
                    it.copy(onClick = { item -> onItemClicked(item) })
                }
                else -> _moreSection.value = listOf()
            }
        }
    }

    fun initCleanerProfile() {
        _cleanerData.value = cleanerProfile
    }

    // Actions

    fun onOpenEmployeeProfile() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            MoreFragmentDirections.actionMoreFragmentToProfileDetailsFragment()
        )
    }

    private fun onItemClicked(item: MoreSectionItem) {
        when (item.type) {
            Type.Employees -> {
                _baseCmd.value = BaseCommand.PerformNavAction(
                    MoreFragmentDirections.actionMoreFragmentToShowEmployeesFragment()
                )
            }
            Type.UnassignedMissions -> {
                _baseCmd.value = BaseCommand.PerformNavAction(
                    MoreFragmentDirections.actionMoreFragmentToUnassignedMissionsFragment()
                )
            }
            Type.Activity -> {
                _baseCmd.value = BaseCommand.PerformNavAction(
                    MoreFragmentDirections.actionMoreFragmentToActivityFragment()
                )
            }
            Type.Help -> {
                _cmd.value = Command.ShowSupport(R.string.support_url)
            }
            Type.About -> {
                _cmd.value = Command.ShowAbout(R.string.about_url)
            }
        }
    }

    // Command

    sealed class Command {
        data class ShowSupport(@StringRes val url: Int) :
            MoreViewModel.Command()

        data class ShowAbout(@StringRes val url: Int) :
            MoreViewModel.Command()
    }
}
