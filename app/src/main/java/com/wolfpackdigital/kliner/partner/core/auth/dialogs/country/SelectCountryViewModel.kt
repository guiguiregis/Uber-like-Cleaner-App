package com.wolfpackdigital.kliner.partner.core.auth.dialogs.country

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCountriesUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.ItemCountry
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import kotlinx.coroutines.launch

class SelectCountryViewModel(
    private val getCountriesUseCase: GetCountriesUseCase
) : BaseViewModel(), PersistenceService {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val searchQuery = MutableLiveData<String>()

    private val _countries = MutableLiveData<List<ItemCountry>>()
    val countries: LiveData<List<ItemCountry>> get() = _countries

    private val filterObserver: (Any) -> Unit = {
        val initialList = _countries.value ?: listOf()
        val query = searchQuery.value ?: ""
        filteredCountries.value = if (!query.isBlank())
            initialList.filter {
                it.name.contains(query, true) ||
                        it.countryCode.contains(query, true)
            }
        else
            initialList
    }

    val filteredCountries = MediatorLiveData<List<ItemCountry>>().apply {
        addSource(searchQuery, filterObserver)
        addSource(_countries, filterObserver)
    }

    val selectedItem = MutableLiveData<ItemCountry?>()
    val selectedItemString = selectedItem.map {
        it ?: return@map ""
        "${it.flag} ${it.name}"
    }

    val selectedCountryCode = selectedItem.map {
        it ?: return@map ""
        "${it.flag} ${it.countryCode}"
    }

    private val countrySelectedCallback: (ItemCountry) -> Unit = {
        val new = it.copy(locationSelected = !it.locationSelected)
        selectedItem.value = if (new.locationSelected) new else null
        close()
    }

    fun loadCountries(selectedCountryCode: String? = Constants.DEFAULT_PHONE_PREFIX) {
        if (_countries.value?.isNotEmpty() == true) return
        viewModelScope.launch {
            when (val result = getCountriesUseCase.executeNow(Unit)) {
                is Result.Success ->
                    _countries.value = result.data.map {
                        it.copy(onSelectCallback = countrySelectedCallback)
                    }.apply {
                        val filteredList = filter { it.countryCode == selectedCountryCode }
                        when (filteredList.size) {
                            1 -> filteredList[0].onSelect()
                            else -> {
                                filteredList.firstOrNull { it.name == cleanerProfile?.country }
                                    ?.onSelect() ?: filteredList.firstOrNull()?.onSelect()
                            }
                        }
                    }
                is Result.Error ->
                    _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    fun close() {
        _cmd.value = Command.CloseDialog
    }

    fun clearQuery() {
        searchQuery.value = ""
    }

    sealed class Command {
        object CloseDialog : Command()
    }
}
