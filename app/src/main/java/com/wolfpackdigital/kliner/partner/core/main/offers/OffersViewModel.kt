@file:Suppress("TooManyFunctions")

package com.wolfpackdigital.kliner.partner.core.main.offers

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.AcceptOfferUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.ConfirmMissionRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.ConfirmMissionUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetBankAccountUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetOffersUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetUnconfirmedMissionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.Mission
import com.wolfpackdigital.kliner.partner.shared.useCases.RefuseOfferUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.IntentProvider
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.minusAssign
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class OffersViewModel(
    private val getUnconfirmedMissionsUseCase: GetUnconfirmedMissionsUseCase,
    private val getOffersUseCase: GetOffersUseCase,
    private val acceptOfferUseCase: AcceptOfferUseCase,
    private val refuseOfferUseCase: RefuseOfferUseCase,
    private val confirmMissionUseCase: ConfirmMissionUseCase,
    private val getBankAccountUseCase: GetBankAccountUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _offers = MutableLiveData<List<Mission>>()
    val offers: LiveData<List<Mission>> = _offers

    val isRefreshing = MutableLiveData<Boolean>()
    private val hasBankAccount = MutableLiveData(false)

    // Actions

    fun fetchAllData() {
        performApiCall {
            fetchBankAccount()
            fetchOffers()
        }
    }

    private suspend fun fetchBankAccount() {
        when (getBankAccountUseCase.executeNow(Unit)) {
            is Result.Success -> hasBankAccount.value = true
            else -> hasBankAccount.value = false
        }
    }

    private suspend fun fetchOffers() {
        when (val result =
            if (cleanerProfile?.isEmployee() == true)
                getUnconfirmedMissionsUseCase.executeNow(Unit)
            else getOffersUseCase.executeNow(Unit)) {
            is Result.Success -> {
                isRefreshing.value = false
                _offers.value = result.data.map {
                    it.copy(
                        onLocationClicked = { item -> openOfferLocation(item) },
                        onAcceptClicked = { item -> acceptOfferClicked(item) },
                        onRefuseClicked = { item -> refuseOfferClicked(item) }
                    )
                }
            }
            else -> {
                isRefreshing.value = false
                _offers.value = listOf()
            }
        }
    }

    private fun dismissOffer(offerId: Int) {
        offers.value?.firstOrNull { it.id == offerId }?.let {
            _offers -= it
        }
    }

    private fun acceptOffer(id: Int) {
        performApiCall {
            when (val result = acceptOfferUseCase.executeNow(id)) {
                is Result.Success -> handleAcceptedOffer(result.data.id, result.data.isRecurrent)
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    private fun refuseOffer(id: Int) {
        performApiCall {
            when (val result = refuseOfferUseCase.executeNow(id)) {
                is Result.Success -> dismissOffer(id)
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    private fun confirmMission(id: Int, confirm: Boolean) {
        performApiCall {
            when (val result =
                confirmMissionUseCase.executeNow(ConfirmMissionRequest(id, confirm))) {
                is Result.Success -> dismissOffer(id)
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    private fun handleAcceptedOffer(id: Int?, isRecurrent: Boolean) {
        dismissOffer(id ?: return)
        if (cleanerProfile?.isEmployer() == true)
            _baseCmd.value = BaseCommand.PerformNavAction(
                OffersFragmentDirections
                    .actionOffersFragmentToAssignEmployeeBottomSheetDialog(id, isRecurrent)
            )
    }

    private fun openOfferLocation(offer: Mission) {
        offer.address?.let { address ->
            _cmd.value = Command.LaunchIntent(
                IntentProvider.getMapsIntent(address.latitude, address.longitude)
            )
        } ?: run {
            _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.error_no_customer_address)
        }
    }

    private fun acceptOfferClicked(offer: Mission) {
        if (cleanerProfile?.isEmployee() == false && hasBankAccount.value == false) {
            _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.offer_add_iban_error)
            return
        }
        offer.id?.let {
            if (cleanerProfile?.isEmployee() == true) confirmMission(it, true) else acceptOffer(it)
        } ?: run { _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.offer_not_found) }
    }

    private fun refuseOfferClicked(offer: Mission) {
        if (cleanerProfile?.isEmployee() == true)
            offer.id?.let { confirmMission(it, false) } ?: run {
                _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.offer_not_found)
            }
        else
            offer.id?.let { refuseOffer(it) } ?: run {
                _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.offer_not_found)
            }
    }

    // Command

    sealed class Command {
        data class LaunchIntent(val intent: Intent) : Command()
    }
}
