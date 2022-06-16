package com.wolfpackdigital.kliner.partner.core.main.dialogs

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.DeleteEmployeeUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GeneralOption
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCountriesUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetOptionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.ItemCountry
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateEmployeeProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.IntentProvider
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.filterCheckedItems
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.onOptionsClicked
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmployeeDetailsViewModel(
    private val id: Int,
    private val getCleanerProfileUseCase: GetCleanerProfileUseCase,
    private val updateEmployeeProfileUseCase: UpdateEmployeeProfileUseCase,
    private val getOptionsUseCase: GetOptionsUseCase,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase
) : BaseViewModel() {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _cleanerProfile = MutableLiveData<CleanerProfile>()
    val cleanerProfile: LiveData<CleanerProfile> = _cleanerProfile

    val firstName = _cleanerProfile.map {
        it.firstName
    }
    val lastName = _cleanerProfile.map {
        it.lastName
    }
    val email = _cleanerProfile.map {
        it.partnerAttributes?.email
    }
    val address = _cleanerProfile.map {
        it.activityZonesAttributes?.firstOrNull()?.streetLine ?: ""
    }
    val birthDate = _cleanerProfile.map {
        it.birthDate
    }

    val countryCode = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()

    val gender = MutableLiveData<String>()
    private val _typesOfWork = MutableLiveData<List<GeneralOption>>()
    val typesOfWork: LiveData<List<GeneralOption>>
        get() = _typesOfWork

    private var updateWorkTypesJob: Job? = null
    val selectedWorkTypes = _typesOfWork.map {
        it?.filterCheckedItems()
    }

    // Mock data
    @Suppress("MagicNumber")
    val workedHours = MutableLiveData(100)

    @Suppress("MagicNumber")
    val completedServices = MutableLiveData(50)

    init {
        performApiCall {
            when (val result = getCleanerProfileUseCase.executeNow(id)) {
                is Result.Success -> {
                    _cleanerProfile.value = result.data
                }
                else -> _cmd.value = Command.CloseEmployeeDetailsDialog
            }
            fetchOptions()
            when (val result = getCountriesUseCase.executeNow(Unit)) {
                is Result.Success -> {
                    result.data.firstOrNull {
                        _cleanerProfile.value?.partnerAttributes?.phoneNumber?.contains(
                            it.countryCode
                        ) ?: false
                    }?.let { fillPhoneDetails(it) }
                }
                is Result.Error ->
                    _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    private suspend fun fetchOptions() {
        when (val result = getOptionsUseCase.executeNow(Unit)) {
            is Result.Success -> {
                _typesOfWork.value = result.data.optionsWorkType.map {
                    it.copy(
                        onClick = { item -> onOptionsClicked(item, _typesOfWork) },
                        checked = _cleanerProfile.value?.workTypeIDS?.contains(it.id) ?: false,
                        enabled = true
                    )
                }
                gender.value = result.data.optionsGender.firstOrNull {
                    it.id == _cleanerProfile.value?.genderID
                }?.name ?: ""
            }
            else -> {
            }
        }
    }

    private fun fillPhoneDetails(country: ItemCountry) {
        phoneNumber.value = _cleanerProfile.value?.partnerAttributes?.phoneNumber?.removePrefix(
            country.countryCode
        )
        countryCode.value = "${country.flag} ${country.countryCode}"
    }

    fun updateWorkTypes(workTypes: List<Int>) {
        updateWorkTypesJob?.cancel()
        updateWorkTypesJob = newUpdateWorkTypesJob(workTypes)
    }

    private fun newUpdateWorkTypesJob(workTypes: List<Int>) = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            delay(Constants.UPDATE_DEBOUNCE)
            val profile = _cleanerProfile.value ?: return@withContext

            when {
                workTypes.isEmpty() -> {
                    _cmd.value = Command.ShowMessageID(R.string.error_work_types)
                }
                workTypes.sorted() == profile.workTypeIDS?.sorted() -> return@withContext
                else -> when (val result =
                    updateEmployeeProfileUseCase.executeNow(profile.copy(workTypeIDS = workTypes))) {
                    is Result.Success -> {
                        _cleanerProfile.value = result.data
                        _cmd.value = Command.ShowMessageID(R.string.options_updated)
                    }
                    is Result.Error -> {
                        _cleanerProfile.value = profile
                        _cmd.value = Command.ShowMessage(result.error)
                    }
                }
            }
            fetchOptions()
        }
    }

    fun onDeleteEmployee() {
        performApiCall {
            when (val result = deleteEmployeeUseCase.executeNow(id)) {
                is Result.Success -> {
                    _cmd.value = Command.RefreshEmployeeList
                    _cmd.value = Command.CloseDeleteEmployeeDialog
                    _cmd.value = Command.CloseEmployeeDetailsDialog
                }
                is Result.Error ->
                    _cmd.value = Command.ShowMessage(result.error)
            }
        }
    }

    fun onCallEmployee() {
        val phoneNumber = _cleanerProfile.value?.partnerAttributes?.phoneNumber ?: return
        _cmd.value = Command.LaunchIntent(IntentProvider.getDialIntent(phoneNumber))
    }

    fun onOpenMenu() {
        _cmd.value = Command.OpenMenu
    }

    fun close() {
        _cmd.value = Command.CloseEmployeeDetailsDialog
    }

    fun closeDeleteEmployee() {
        _cmd.value = Command.CloseDeleteEmployeeDialog
    }

    sealed class Command {
        object CloseEmployeeDetailsDialog : Command()
        object CloseDeleteEmployeeDialog : Command()
        data class LaunchIntent(val intent: Intent) : Command()
        object OpenMenu : Command()
        data class ShowMessage(val text: String) : Command()
        data class ShowMessageID(val res: Int) : Command()
        object RefreshEmployeeList : Command()
    }
}
