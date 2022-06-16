package com.wolfpackdigital.kliner.partner.core.auth.registration.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressItem
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressType
import com.wolfpackdigital.kliner.partner.shared.useCases.CleaningCompany
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateCompanyUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCleaningCompanyUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.HeadQuarterAttributes
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchAddressUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.SearchRequest
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val SIREN_LEN = 9

class CompanyDetailsViewModel(
    private val createCompanyUseCase: CreateCompanyUseCase,
    private val getCleaningCompanyUseCase: GetCleaningCompanyUseCase,
    private val searchAddressUseCase: SearchAddressUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val companyName = MutableLiveData<String>()
    val errorCompanyName = MutableLiveData<Int>()
    val companySiren = MutableLiveData<String>()
    val errorCompanySiren = MutableLiveData<Int>()
    val address = MutableLiveData<AddressItem>()

    private val _suggestions = MutableLiveData<List<AddressItem>>()
    val suggestions: LiveData<List<AddressItem>> = _suggestions
    val companyAddress = MutableLiveData<String>().apply {
        observeForever {
            searchAddressJob?.cancel()
            if (canEditCompany.value == true) {
                searchAddressJob = newSearchJob(it)
            }
        }
    }
    private var searchAddressJob: Job? = null

    val canEditCompany = MutableLiveData(true)

    fun refreshCompanyData() {
        companyID?.let {
            performApiCall {
                when (val result = getCleaningCompanyUseCase.executeNow(it)) {
                    is Result.Success -> fillCompanyData(
                        result.data
                    )
                    else -> {
                    }
                }
            }
        }
    }

    private fun fillCompanyData(company: CleaningCompany) {
        canEditCompany.value = false

        companyName.value = company.name
        companyAddress.value = company.headquarterAttrs?.streetLineOne
        companySiren.value = company.siren

        validateCompany(company) {}
    }

    private fun newSearchJob(query: String) = viewModelScope.launch {
        if (query == address.value?.streetLine)
            return@launch
        withContext(Dispatchers.IO) {
            delay(Constants.SEARCH_DEBOUNCE)
            when (val result =
                searchAddressUseCase.executeNow(SearchRequest(query, AddressType.ADDRESS, null))) {
                is Result.Success -> _suggestions.postValue(result.data)
                else -> _suggestions.postValue(listOf())
            }
        }
    }

    // Actions

    fun addressSelected(position: Int) {
        searchAddressJob?.cancel()
        address.value = _suggestions.value?.get(position) ?: return
    }

    fun onSirenInfo() {
        _cmd.value = Command.ShowSirenDialog
    }

    fun closeSirenDialog() {
        _cmd.value = Command.CloseSirenDialog
    }

    fun onNext() {
        val company = CleaningCompany(
            companyName.value,
            companySiren.value,
            HeadQuarterAttributes(companyAddress.value)
        )

        validateCompany(company) {
            if (canEditCompany.value == false)
                _cmd.value = Command.GoNext
            else
                when (val response = createCompanyUseCase.executeNow(company)) {
                    is Result.Success -> {
                        companyID = response.data.id
                        _cmd.value = Command.GoNext
                    }
                    is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
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

        if (companyAddress.value.isNullOrEmpty()) {
            error = true
            _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.error_company_address)
        }

        errorCompanySiren.value =
            if (company.siren.isNullOrEmpty() || company.siren.length != SIREN_LEN) {
                error = true
                R.string.error_company_siren
            } else
                null

        if (!error)
            performApiCall { onCompanyValid() }
    }

    override fun onCleared() {
        super.onCleared()
        searchAddressJob?.cancel()
    }

    // Command

    sealed class Command {
        object GoNext : Command()
        object ShowSirenDialog : Command()
        object CloseSirenDialog : Command()
    }
}
