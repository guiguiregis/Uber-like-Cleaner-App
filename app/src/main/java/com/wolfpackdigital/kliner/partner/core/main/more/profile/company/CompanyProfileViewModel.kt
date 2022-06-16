package com.wolfpackdigital.kliner.partner.core.main.more.profile.company

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class CompanyProfileViewModel : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _cleanerData = MutableLiveData<CleanerProfile>(cleanerProfile)
    val cleanerData: LiveData<CleanerProfile> = _cleanerData
    val phoneNumber = MutableLiveData<String>()
    var name = cleanerData.map { it.cleaningCompany?.name }
    val siren = MutableLiveData<String>()
    val address = cleanerData.map { it.cleaningCompany?.headquarterAttrs?.streetLineOne }
    private val profileImageUri = MutableLiveData<Uri>()
    val profileImageUrl = profileImageUri.map { it?.toString() }

    // Actions

    init {
        phoneNumber.value = cleanerData.value?.partnerAttributes?.phoneNumber
        siren.value = cleanerData.value?.cleaningCompany?.siren
        profileImageUri.value = cleanerProfile?.photoUrl?.toUri()
    }

    fun initCleanerProfile() {
        _cleanerData.value = cleanerProfile
    }

    fun onEditAddress() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            CompanyProfileFragmentDirections.actionCompanyProfileFragmentToCompanyAddressFragment()
        )
    }

    fun onEditCompanyName() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            CompanyProfileFragmentDirections.actionCompanyProfileFragmentToCompanyNameFragment()
        )
    }

    // Command

    sealed class Command
}
