package com.wolfpackdigital.kliner.partner.core.main.employee.add

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressItem
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressType
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateEmployeeProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateEmployeeProfileRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateEmployeeProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GeneralOption
import com.wolfpackdigital.kliner.partner.shared.useCases.GetOptionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.Kind
import com.wolfpackdigital.kliner.partner.shared.useCases.PartnerAttributes
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchAddressUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchRequest
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.Constants.SEARCH_DEBOUNCE
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.filterCheckedItems
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getExampleNumber
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getRegionCode
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getValidNumber
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.minusAssign
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.onOptionsClicked
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.plusAssign
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEmployeeViewModel(
    private val isOnboarding: Boolean = false,
    private val getOptionsUseCase: GetOptionsUseCase,
    private val createEmployeeProfileUseCase: CreateEmployeeProfileUseCase,
    private val searchAddressUseCase: SearchAddressUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val phoneUtil = PhoneNumberUtil.getInstance()

    val lastName = MutableLiveData<String>()
    val errorLastName = MutableLiveData<Int>(null)
    val firstName = MutableLiveData<String>()
    val errorFirstName = MutableLiveData<Int>(null)
    val address = MutableLiveData<AddressItem>()
    val gender = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val errorEmail = MutableLiveData<Int>(null)
    val employeeCompetence = MutableLiveData<String>()
    val errorPhoneNumber = MutableLiveData<Int>(null)

    val phonePrefix = MutableLiveData(Constants.DEFAULT_PHONE_PREFIX)
    val phoneNumber = MutableLiveData("")
    val phoneNumberHint = phonePrefix.map { prefix ->
        val hintPrefix = prefix ?: Constants.DEFAULT_PHONE_PREFIX
        hintPrefix.toIntOrNull()?.let {
            _cmd.value = Command.ChangeTextWatcher(phoneUtil.getRegionCode(it))
            phoneUtil.getExampleNumber(it)
        } ?: ""
    }

    val genderList = MutableLiveData<List<GeneralOption>>()

    private val _typesOfWork = MutableLiveData<List<GeneralOption>>()
    val typesOfWork: LiveData<List<GeneralOption>>
        get() = _typesOfWork

    val selectedWorkTypes = _typesOfWork.map {
        it?.filterCheckedItems()
    }

    val rawAddress = MutableLiveData<String>().apply {
        observeForever {
            searchAddressJob?.cancel()
            searchAddressJob = newSearchJob(
                _employeeAddressSuggestions,
                SearchRequest(it, AddressType.ADDRESS)
            )
        }
    }

    private var searchAddressJob: Job? = null
    private val _employeeAddressSuggestions = MutableLiveData<List<AddressItem>>()
    val employeeAddressSuggestions: LiveData<List<AddressItem>> = _employeeAddressSuggestions

    private val _activityAreasSuggestions = MutableLiveData<List<AddressItem>>()
    val activityAreasSuggestions: LiveData<List<AddressItem>> = _activityAreasSuggestions

    private val _selectedAddresses = MutableLiveData<List<AddressItem>>()
    val selectedAddresses: LiveData<List<AddressItem>> = _selectedAddresses

    val rawZone = MutableLiveData<String>().apply {
        observeForever {
            searchAddressJob?.cancel()
            searchAddressJob = newSearchJob(
                _activityAreasSuggestions,
                SearchRequest(it, AddressType.REGIONS)
            )
        }
    }

    fun getOptions() {
        performApiCall {
            when (val result = getOptionsUseCase.executeNow(Unit)) {
                is Result.Success -> {
                    genderList.value = result.data.optionsGender
                    _typesOfWork.value =
                        result.data.optionsWorkType.map {
                            it.copy(
                                onClick = { item -> onOptionsClicked(item, _typesOfWork) },
                                checked = false,
                                enabled = true
                            )
                        }
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    private fun newSearchJob(
        suggestionsList: MutableLiveData<List<AddressItem>>,
        searchRequest: SearchRequest
    ) = viewModelScope.launch {
        if (searchRequest.query == address.value?.streetLine)
            return@launch
        if (searchRequest.query == _selectedAddresses.value?.lastOrNull()?.streetLine)
            return@launch
        withContext(Dispatchers.IO) {
            delay(SEARCH_DEBOUNCE)
            when (val result =
                searchAddressUseCase.executeNow(searchRequest)) {
                is Result.Success -> suggestionsList.postValue(result.data)
                else -> suggestionsList.postValue(listOf())
            }
        }
    }

    // Actions

    fun addressSelected(position: Int) {
        searchAddressJob?.cancel()
        address.value = _employeeAddressSuggestions.value?.get(position) ?: return
    }

    fun addActivityArea(position: Int) {
        val address = _activityAreasSuggestions.value?.get(position)
        _activityAreasSuggestions.value = listOf()
        address ?: return
        if (_selectedAddresses.value?.find {
                it.placeID == address.placeID ||
                        it.streetLine == address.streetLine
            } != null)
            return
        _selectedAddresses += copyOf(address)
    }

    private fun copyOf(address: AddressItem) = address.copy(onDeleteCallback = {
        _selectedAddresses -= it
    })

    fun onInvite() {
        val genderID = genderList.value?.firstOrNull { it.name == gender.value }?.id
        val workTypes = selectedWorkTypes.value
        if (workTypes?.isEmpty() == true) {
            _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.error_work_types)
            return
        }
        val activityZones = mutableListOf<AddressItem>()
        address.value?.let {
            it.default = true
            activityZones.add(it)
        }
        selectedAddresses.value?.let {
            activityZones.addAll(it)
        }
        val number = phoneUtil.getValidNumber(phonePrefix.value, phoneNumber.value)
        val companyId = companyID ?: -1

        val employeeProfile = CleanerProfile(
            firstName = firstName.value,
            lastName = lastName.value,
            genderID = genderID,
            kind = Kind.EMPLOYEE,
            partnerAttributes = PartnerAttributes(
                phoneNumber = number,
                email = email.value
            ),
            activityZonesAttributes = activityZones,
            workTypeIDS = workTypes
        )

        val request =
            CreateEmployeeProfile(CreateEmployeeProfileRequest(employeeProfile), companyId)

        validateProfile(employeeProfile) {
            when (val response = createEmployeeProfileUseCase.executeNow(request)) {
                is Result.Success -> {
                    if (isOnboarding) {
                        _cmd.value = Command.GoToDashboard
                    } else
                        _baseCmd.value = BaseCommand.GoBack
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
            }
        }
    }

    fun openPrefixPicker() {
        _cmd.value = Command.OpenPrefixPicker
    }

    @Suppress("ComplexMethod")
    private fun validateProfile(profile: CleanerProfile, onProfileValid: suspend () -> Unit) {
        var error = false
        errorFirstName.value = if (profile.firstName.isNullOrEmpty()) {
            error = true
            R.string.error_first_name
        } else
            null

        errorLastName.value = if (profile.lastName.isNullOrEmpty()) {
            error = true
            R.string.error_last_name
        } else
            null

        errorPhoneNumber.value = if (profile.partnerAttributes?.phoneNumber.isNullOrEmpty()) {
            error = true
            R.string.phone_confirmation_error
        } else
            null

        errorEmail.value = if (profile.partnerAttributes?.email.isNullOrEmpty()) {
            error = true
            R.string.error_email
        } else if (
            !Patterns.EMAIL_ADDRESS.matcher(profile.partnerAttributes?.email ?: "").matches()
        ) {
            error = true
            R.string.error_company_email_invalid
        } else
            null

        address.value ?: run {
            error = true
            _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.error_address)
        }

        if (!error)
            performApiCall { onProfileValid() }
    }

    override fun onCleared() {
        super.onCleared()
        searchAddressJob?.cancel()
    }

    // Command

    sealed class Command {
        object OpenPrefixPicker : Command()
        class ChangeTextWatcher(val regionCode: String) : Command()
        object GoToDashboard : Command()
    }
}
