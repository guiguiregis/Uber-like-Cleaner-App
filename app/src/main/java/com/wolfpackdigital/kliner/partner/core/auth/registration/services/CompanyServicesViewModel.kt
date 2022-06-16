package com.wolfpackdigital.kliner.partner.core.auth.registration.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.auth.registration.RegistrationFragmentDirections
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressItem
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressType
import com.wolfpackdigital.kliner.partner.shared.useCases.GeneralOption
import com.wolfpackdigital.kliner.partner.shared.useCases.GetOptionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchAddressUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.Constants.SEARCH_DEBOUNCE
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.autoSelectWorkTypeByServiceTypeConditioning
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.filterCheckedItems
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getServiceTypeConditioning
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.minusAssign
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.onOptionsClicked
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.plusAssign
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompanyServicesViewModel(
    private val getOptionsUseCase: GetOptionsUseCase,
    private val searchAddressUseCase: SearchAddressUseCase,
    private val updateCleanerProfileUseCase: UpdateCleanerProfileUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _typesOfService = MutableLiveData<List<GeneralOption>>()
    val typesOfService: LiveData<List<GeneralOption>>
        get() = _typesOfService
    private val _typesOfWork = MutableLiveData<List<GeneralOption>>()
    val typesOfWork: LiveData<List<GeneralOption>>
        get() = _typesOfWork
    val selectedServiceTypes = _typesOfService.map {
        if (cleanerProfile?.isIndependent() == true) {
            (it?.getServiceTypeConditioning() ?: mutableListOf())
                .autoSelectWorkTypeByServiceTypeConditioning(
                    _typesOfWork
                )
        }
        it?.filterCheckedItems()
    }

    val selectedWorkTypes = _typesOfWork.map {
        it?.filterCheckedItems()
    }

    private val _cesuItems = MutableLiveData<List<GeneralOption>>()
    val cesuItems: LiveData<List<GeneralOption>>
        get() = _cesuItems

    val selectedCesu = _cesuItems.map {
        it?.filterCheckedItems()
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

    fun fetchOptions() {
        performApiCall {
            when (val result = getOptionsUseCase.executeNow(Unit)) {
                is Result.Success -> {
                    _typesOfService.value =
                        result.data.optionsServiceType.map {
                            it.copy(
                                onClick = { item -> onOptionsClicked(item, _typesOfService) },
                                checked = cleanerProfile?.serviceTypeIDS?.contains(it.id) ?: false,
                                enabled = true
                            )
                        }
                    _typesOfWork.value = if (cleanerProfile?.isEmployer() == true) null else
                        result.data.optionsWorkType.map {
                            it.copy(
                                onClick = { item -> onOptionsClicked(item, _typesOfWork) },
                                checked = cleanerProfile?.workTypeIDS?.contains(it.id) ?: false,
                                enabled = true
                            )
                        }
                    _cesuItems.value = result.data.optionsAcceptedPayment.map {
                        it.copy(
                            onClick = { item -> onOptionsClicked(item, _cesuItems) },
                            checked = cleanerProfile?.acceptedPaymentIDs?.contains(it.id) ?: false,
                            enabled = true
                        )
                    }
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    fun onNext() {
        performApiCall {
            val serviceTypes = selectedServiceTypes.value
            if (serviceTypes.isNullOrEmpty()) {
                _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.error_service_types)
                return@performApiCall
            }
            val workTypes = selectedWorkTypes.value
            if (workTypes?.isEmpty() == true && cleanerProfile?.isIndependent() == true) {
                _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.error_work_types)
                return@performApiCall
            }
            val paymentMethod = selectedCesu.value ?: listOf()
            val zones = selectedAddresses.value ?: listOf()
            if (zones.isEmpty() && cleanerProfile?.isIndependent() == true) {
                _baseCmd.value =
                    BaseCommand.ShowSnackbarById(R.string.error_add_activity_area_soloprenur)
                return@performApiCall
            }
            val newProfile = cleanerProfile?.copy(
                serviceTypeIDS = serviceTypes,
                workTypeIDS = workTypes,
                acceptedPaymentIDs = paymentMethod,
                activityZonesAttributes = zones
            ) ?: return@performApiCall

            when (val result = updateCleanerProfileUseCase.executeNow(newProfile)) {
                is Result.Success -> {
                    cleanerProfile = result.data
                    showNotificationSetting = true
                    showGeolocationSetting = true
                    _baseCmd.value = BaseCommand.PerformNavAction(
                        RegistrationFragmentDirections
                            .actionRegistrationFragmentToUserVerificationFragment()
                    )
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    fun addAddress(position: Int) {
        val address = _suggestions.value?.get(position)
        _suggestions.value = listOf()
        address ?: return
        if (_selectedAddresses.value?.find { it.placeID == address.placeID } != null)
            return
        _selectedAddresses += copyOf(address)
    }

    private fun copyOf(address: AddressItem) = address.copy(onDeleteCallback = {
        _selectedAddresses -= it
    })

    override fun onCleared() {
        super.onCleared()
        searchAddressesJob?.cancel()
    }

// Command

    sealed class Command
}
