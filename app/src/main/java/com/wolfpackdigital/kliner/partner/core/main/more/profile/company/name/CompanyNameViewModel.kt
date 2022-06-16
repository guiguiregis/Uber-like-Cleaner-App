package com.wolfpackdigital.kliner.partner.core.main.more.profile.company.name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.CleaningCompany
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateCleaningCompanyUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class CompanyNameViewModel(
    private val updateCleaningCompanyUseCase: UpdateCleaningCompanyUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _cleanerData = MutableLiveData<CleanerProfile>(cleanerProfile)
    val cleanerData: LiveData<CleanerProfile> = _cleanerData
    val nameMediator = MediatorLiveData<Boolean>()
    private val nameChangeObserver = Observer<String> {
        nameMediator.value =
            companyName.value != cleanerData.value?.cleaningCompany?.name &&
                    companyName.value?.isNotEmpty() ?: false
    }
    val companyName = MutableLiveData<String>()
    val errorCompanyName = MutableLiveData<Int>()

// Actions

    init {
        nameMediator.addSource(companyName, nameChangeObserver)
        _cleanerData.value = cleanerProfile
        companyName.value = cleanerData.value?.cleaningCompany?.name
    }

    fun onAddNameClicked() {
        val newCompanyProfile = cleanerData.value?.cleaningCompany?.copy(
            name = companyName.value
        ) ?: return
        validateCompany(newCompanyProfile) {
            when (val result = updateCleaningCompanyUseCase.executeNow(newCompanyProfile)) {
                is Result.Success -> {
                    _baseCmd.value = BaseCommand.GoBack
                    cleanerProfile = cleanerProfile?.copy(cleaningCompany = newCompanyProfile)
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    @Suppress("ComplexMethod")
    private fun validateCompany(company: CleaningCompany, onCompanyValid: suspend () -> Unit) {
        var error = false
        errorCompanyName.value = if (company.name.isNullOrEmpty()) {
            error = true
            R.string.error_company_name
        } else
            null
        if (!error)
            performApiCall { onCompanyValid() }
    }

// Command

    sealed class Command
}
