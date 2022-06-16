package com.wolfpackdigital.kliner.partner.core.main.more.profile.activityarea

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressItem
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressType
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchAddressUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.equalItems
import com.wolfpackdigital.kliner.partner.shared.utils.Constants.SEARCH_DEBOUNCE
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.minusAssign
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.plusAssign
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val MINIMUM_NUMBER_OF_ZONES = 1

class ActivityAreaViewModel(
    private val updateCleanerProfileUseCase: UpdateCleanerProfileUseCase,
    private val searchAddressUseCase: SearchAddressUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _cleanerData = MutableLiveData<CleanerProfile>(cleanerProfile)
    val cleanerData: LiveData<CleanerProfile> = _cleanerData
    val addressMediator = MediatorLiveData<Boolean>()
    private val addressChangeObserver = Observer<List<AddressItem>> {
        addressMediator.value = !it.equalItems(cleanerData.value?.activityZonesAttributes)
    }

    private var searchAddressesJob: Job? = null
    private val _suggestions = MutableLiveData<List<AddressItem>>()
    val suggestions: LiveData<List<AddressItem>> = _suggestions

    private val _selectedAddresses = MutableLiveData<List<AddressItem>>()
    val selectedAddresses: LiveData<List<AddressItem>> = _selectedAddresses

    val rawZone = MutableLiveData<String>().apply {
        observeForever {
            searchAddressesJob?.cancel()
            searchAddressesJob = newSearchJob(it)
        }
    }

    private fun newSearchJob(query: String) = viewModelScope.launch {
        if (query == _selectedAddresses.value?.lastOrNull()?.streetLine)
            return@launch
        withContext(Dispatchers.IO) {
            delay(SEARCH_DEBOUNCE)
            when (val result =
                searchAddressUseCase.executeNow(SearchRequest(query, AddressType.REGIONS))) {
                is Result.Success -> _suggestions.postValue(result.data)
                else -> _suggestions.postValue(listOf())
            }
        }
    }

// Actions

    init {
        addressMediator.addSource(_selectedAddresses, addressChangeObserver)
        _cleanerData.value = cleanerProfile
        _selectedAddresses.value =
            cleanerProfile?.activityZonesAttributes?.map { addressItem ->
                addressItem.copy(
                    placeID = "",
                    onDeleteCallback = {
                        onDeleteAddress(it)
                    }
                )
            } ?: listOf()
    }

    fun onAddAddressClicked() {
        performApiCall {
            val newProfile = cleanerProfile?.copy(
                activityZonesAttributes = getUpdatedAddresses()
            ) ?: return@performApiCall

            when (val result = updateCleanerProfileUseCase.executeNow(newProfile)) {
                is Result.Success -> {
                    cleanerProfile = result.data
                    _baseCmd.value = BaseCommand.GoBack
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    private fun getUpdatedAddresses(): List<AddressItem> {
        val updatedActivityAreas = mutableListOf<AddressItem>()
        selectedAddresses.value?.asIterable()?.let { newAddresses ->
            updatedActivityAreas.addAll(newAddresses)

            val deletedAddresses = cleanerProfile?.activityZonesAttributes?.minus(newAddresses)
            deletedAddresses?.forEach { it.destroy = 1 }
            deletedAddresses?.let { updatedActivityAreas.addAll(it) }
        }
        return updatedActivityAreas
    }

    fun addAddress(position: Int) {
        val address = _suggestions.value?.get(position)
        _suggestions.value = listOf()
        address ?: return
        if (_selectedAddresses.value?.find {
                it.placeID == address.placeID ||
                        it.streetLine == address.streetLine
            } != null)
            return
        _selectedAddresses += copyOf(address)
    }

    private fun copyOf(address: AddressItem) = address.copy(onDeleteCallback = {
        onDeleteAddress(it)
    })

    private fun onDeleteAddress(it: AddressItem) {
        when {
            cleanerProfile?.isIndependent() == true ->
                if (_selectedAddresses.value?.size ?: 0 > MINIMUM_NUMBER_OF_ZONES)
                    _selectedAddresses -= it
                else
                    _baseCmd.value =
                        BaseCommand.ShowSnackbarById(
                            R.string.error_edit_activity_area_soloprenur
                        )
            else -> _selectedAddresses -= it
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchAddressesJob?.cancel()
    }
// Command

    sealed class Command
}
