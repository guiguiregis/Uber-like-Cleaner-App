package com.wolfpackdigital.kliner.partner.core.main.more.profile.company.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressItem
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressType
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchAddressUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateCleaningCompanyUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.Constants.SEARCH_DEBOUNCE
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompanyAddressViewModel(
    private val updateCleaningCompanyUseCase: UpdateCleaningCompanyUseCase,
    private val searchAddressUseCase: SearchAddressUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _cleanerData = MutableLiveData<CleanerProfile>(cleanerProfile)
    val cleanerData: LiveData<CleanerProfile> = _cleanerData
    private var searchAddressesJob: Job? = null
    private val _suggestions = MutableLiveData<List<AddressItem>>()
    val suggestions: LiveData<List<AddressItem>> = _suggestions
    val addressMediator = MediatorLiveData<Boolean>()
    private val addressChangeObserver = Observer<String> {
        addressMediator.value =
            companyAddress.value != cleanerData.value?.cleaningCompany?.headquarterAttrs?.streetLineOne &&
                    companyAddress.value?.isNotEmpty() ?: false
    }
    private var isAutoCompletedAddress = true
    val address = MutableLiveData<AddressItem>()
    val companyAddress = MutableLiveData<String>().apply {
        observeForever {
            if (isAutoCompletedAddress && it != cleanerData.value?.cleaningCompany?.headquarterAttrs?.streetLineOne) {
                isAutoCompletedAddress = false
            }
            searchAddressesJob?.cancel()
            if (!isAutoCompletedAddress)
                searchAddressesJob = newSearchJob(it)
        }
    }

    private fun newSearchJob(query: String) = viewModelScope.launch {
        if (query == address.value?.streetLine)
            return@launch
        withContext(Dispatchers.IO) {
            delay(SEARCH_DEBOUNCE)
            when (val result =
                searchAddressUseCase.executeNow(SearchRequest(query, AddressType.ADDRESS))) {
                is Result.Success -> _suggestions.postValue(result.data)
                else -> _suggestions.postValue(listOf())
            }
        }
    }

// Actions

    init {
        addressMediator.addSource(companyAddress, addressChangeObserver)
        _cleanerData.value = cleanerProfile
        companyAddress.value = cleanerData.value?.cleaningCompany?.headquarterAttrs?.streetLineOne
    }

    fun addressSelected(position: Int) {
        searchAddressesJob?.cancel()
        address.value = _suggestions.value?.get(position) ?: return
    }

    fun onAddAddressClicked() {
        performApiCall {
            val newCompanyProfile = cleanerData.value?.cleaningCompany?.copy(
                headquarterAttrs = cleanerData.value?.cleaningCompany?.headquarterAttrs?.copy(
                    streetLineOne = companyAddress.value
                )
            ) ?: return@performApiCall
            when (val result = updateCleaningCompanyUseCase.executeNow(newCompanyProfile)) {
                is Result.Success -> {
                    _baseCmd.value = BaseCommand.GoBack
                    cleanerProfile = cleanerProfile?.copy(cleaningCompany = newCompanyProfile)
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchAddressesJob?.cancel()
    }

// Command

    sealed class Command
}
