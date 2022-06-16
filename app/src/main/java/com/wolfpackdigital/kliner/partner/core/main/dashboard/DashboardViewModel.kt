package com.wolfpackdigital.kliner.partner.core.main.dashboard

import android.net.Uri
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.CleaningCompany
import com.wolfpackdigital.kliner.partner.shared.useCases.CompanyCleanersRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.FILENAME
import com.wolfpackdigital.kliner.partner.shared.useCases.GetBankAccountUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCleaningCompanyUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCompanyCleanersUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetDashboardTipsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetMissionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.Mission
import com.wolfpackdigital.kliner.partner.shared.useCases.MissionsRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.PhotoTypes
import com.wolfpackdigital.kliner.partner.shared.useCases.Rank
import com.wolfpackdigital.kliner.partner.shared.useCases.TipItem
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.Constants.MAX_DAYS_LEFT_TO_UPLOAD_CERTIFICATE
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.daysLeftFrom
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.isSameDay
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.safeValueOf
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toInstant
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

@Suppress("TooManyFunctions")
class DashboardViewModel(
    private val getDashboardTipsUseCase: GetDashboardTipsUseCase,
    private val getCleanerProfileUseCase: GetCleanerProfileUseCase,
    private val getCompanyCleanersUseCase: GetCompanyCleanersUseCase,
    private val getMissionsUseCase: GetMissionsUseCase,
    private val uploadUserPhotoUseCase: UploadUserPhotoUseCase,
    private val getBankAccountUseCase: GetBankAccountUseCase,
    private val getCleaningCompanyUseCase: GetCleaningCompanyUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _tips = MutableLiveData<List<TipItem>>()
    val tips: LiveData<List<TipItem>> = _tips

    private val _cleanerData = MutableLiveData<CleanerProfile>(cleanerProfile)
    val cleanerData: LiveData<CleanerProfile> = _cleanerData

    private val _cleaningCompany = MutableLiveData<CleaningCompany>()
    val cleaningCompany: LiveData<CleaningCompany> = _cleaningCompany

    private val _cleaners = MutableLiveData<List<CleanerProfile>>()
    val cleaners: LiveData<List<CleanerProfile>> = _cleaners

    val hasBankAccount = MutableLiveData(false)
    val allDocumentsPresentMediator = MediatorLiveData<Boolean>()

    val daysLeftToUploadCertificate by lazy {
        contractSignedAt?.daysLeftFrom(MAX_DAYS_LEFT_TO_UPLOAD_CERTIFICATE)?.let {
            lastKnownLeftDaysToUploadCertificate = it
            it
        } ?: run {
            lastKnownLeftDaysToUploadCertificate
        }
    }

    private val documentsObserver = Observer<Any> {
        val bankAccountPresent = hasBankAccount.value ?: false
        val novaCertificatePresent =
            !cleanerData.value?.novaCertificate?.url.isNullOrEmpty()
        val insuranceCertificatePresent =
            !cleanerData.value?.insuranceCertificate?.url.isNullOrEmpty()
        allDocumentsPresentMediator.value =
            bankAccountPresent && novaCertificatePresent && insuranceCertificatePresent
    }

    // Cleaners FILTERING
    val rankFilter = MutableLiveData<String>()
    val ranks = listOf(
        R.string.gold,
        R.string.silver,
        R.string.bronze,
        R.string.unspecified
    )

    private val cleanersMediator = MediatorLiveData<List<CleanerProfile>>()

    private val filterObserver = Observer<String> {
        val rank = safeValueOf(it) ?: Rank.Unspecified
        cleanersMediator.value =
            _cleaners.value?.filter { c -> c.qualityRank?.rankKey == rank } ?: listOf()
    }
    private val cleanersObserver = Observer<List<CleanerProfile>> {
        val rank = safeValueOf(rankFilter.value) ?: Rank.Unspecified
        cleanersMediator.value = it?.filter { p -> p.qualityRank?.rankKey == rank } ?: listOf()
    }

    // Missions FILTERING
    private val _missions = MutableLiveData<List<Mission>>(listOf())

    val unassignedMissionsCount =
        _missions.map { it?.count { mission -> mission.isOnAssignment } ?: 0 }

    private val missionsFilter = MutableLiveData(MissionFilter.TODAY)

    private val missionFilterObserver = Observer<MissionFilter> {
        missionsMediator.value = when (it) {
            MissionFilter.MONTH -> _missions.value
            MissionFilter.TODAY -> _missions.value?.filter { m ->
                m.date?.toInstant()?.isSameDay() == true
            } ?: listOf()
            else -> _missions.value
        }?.filter { mission -> mission.isAssigned }
    }

    private val missionsObserver = Observer<List<Mission>> {
        missionsMediator.value = when (missionsFilter.value) {
            MissionFilter.MONTH -> it
            MissionFilter.TODAY -> it?.filter { m ->
                m.date?.toInstant()?.isSameDay() == true
            } ?: listOf()
            else -> it
        }?.filter { mission -> mission.isAssigned }
    }

    val missionsMediator = MediatorLiveData<List<Mission>>()

    val onMissionSelected: (Mission) -> Unit = onSelected@{
        if (cleanerProfile?.isEmployee() == true && it.confirmedAt.isNullOrEmpty()) {
            openOffers(it.id)
        } else {
            _baseCmd.value = BaseCommand.PerformNavAction(
                DashboardFragmentDirections.actionDashboardFragmentToMissionDetailsFragment(
                    it.id ?: return@onSelected
                )
            )
        }
    }

    init {
        cleanersMediator.addSource(rankFilter, filterObserver)
        cleanersMediator.addSource(_cleaners, cleanersObserver)

        missionsMediator.addSource(missionsFilter, missionFilterObserver)
        missionsMediator.addSource(_missions, missionsObserver)

        allDocumentsPresentMediator.addSource(_cleanerData, documentsObserver)
        allDocumentsPresentMediator.addSource(hasBankAccount, documentsObserver)
    }

    private val cleanerSelectedCallback: (CleanerProfile) -> Unit = {
        it.id?.let { id ->
            _baseCmd.value = BaseCommand.PerformNavAction(
                DashboardFragmentDirections.actionDashboardFragmentToEmployeeDetailsDialog(id)
            )
        }
    }

    @Suppress("ComplexMethod", "MagicNumber")
    fun fetchAllData() {
        performApiCall {
            fetchProfileData()

            fetchDashboardTips()

            fetchCompanyData()

            fetchCompanyCleaners()

            fetchMissions()

            fetchBankAccount()
        }
    }

    private suspend fun fetchDashboardTips() {
        when (val result = getDashboardTipsUseCase.executeNow(Unit)) {
            is Result.Success -> _tips.value = result.data
            else -> _tips.value = listOf()
        }
    }

    private suspend fun fetchProfileData() {
        val id = cleanerProfile?.id ?: return
        when (val result = getCleanerProfileUseCase.executeNow(id)) {
            is Result.Success -> {
                cleanerProfile = result.data
                _cleanerData.value = result.data
            }
            is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
        }
    }

    private suspend fun fetchCompanyData() {
        when (val result = getCleaningCompanyUseCase.executeNow(companyID ?: -1)) {
            is Result.Success -> _cleaningCompany.value = result.data
            else -> {
            }
        }
    }

    fun fetchCompanyCleaners() {
        performApiCall {
            when (val result = getCompanyCleanersUseCase.executeNow(
                CompanyCleanersRequest(companyID ?: -1, null)
            )) {
                is Result.Success -> {
                    val defaultRank =
                        result.data.mapNotNull { it.qualityRank?.rankKey }.firstOrNull()
                            ?: Rank.Unspecified
                    rankFilter.value = defaultRank.toString()
                    _cleaners.value = result.data.map { it.copy(onClick = cleanerSelectedCallback) }
                }
                else -> _cleaners.value = listOf()
            }
        }
    }

    private suspend fun fetchMissions() {
        when (val result = getMissionsUseCase.executeNow(MissionsRequest())) {
            is Result.Success -> _missions.value = result.data.map {
                it.copy(onClick = onMissionSelected)
            }
            else -> _missions.value = listOf()
        }
    }

    private suspend fun fetchBankAccount() {
        when (getBankAccountUseCase.executeNow(Unit)) {
            is Result.Success -> hasBankAccount.value = true
            else -> hasBankAccount.value = false
        }
    }

    fun uploadCertificate(uri: Uri?, type: PhotoTypes) {
        performApiCall {
            val request = uri?.let { UploadUserPhotoRequest(type, mapOf(FILENAME to uri)) }
                ?: return@performApiCall
            when (val response = uploadUserPhotoUseCase.executeNow(request)) {
                is Result.Success -> fetchProfileData()
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
            }
        }
    }

    // Actions

    fun onOpenProfileDetails() {
        _baseCmd.value =
            BaseCommand.PerformNavAction(DashboardFragmentDirections.actionDashboardFragmentToProfileDetailsFragment())
    }

    fun onOpenCompanyDetails() {
        _baseCmd.value =
            BaseCommand.PerformNavAction(DashboardFragmentDirections.actionDashboardFragmentToCompanyProfileFragment())
    }

    fun onUploadNovaCertificate() {
        _cmd.value = Command.UploadNovaCertificate
    }

    fun onUploadInsuranceCertificate() {
        _cmd.value = Command.UploadInsuranceCertificate
    }

    fun onBankAccount() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            DashboardFragmentDirections.actionDashboardFragmentToAddBankAccountFragment(null)
        )
    }

    fun onOpenInviteEmployee() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            DashboardFragmentDirections.actionDashboardFragmentToInviteEmployeeFragment(false)
        )
    }

    fun onOpenAddEmployee() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            DashboardFragmentDirections.actionDashboardFragmentToAddEmployeeFragment(false)
        )
    }

    fun onOpenUnassignedMissions() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            DashboardFragmentDirections.actionDashboardFragmentToUnassignedMissionsFragment()
        )
    }

    fun onFilterToday() {
        missionsFilter.value = MissionFilter.TODAY
    }

    fun onFilterMonth() {
        missionsFilter.value = MissionFilter.MONTH
    }

    fun onSearchServices() {
        openOffers(null)
    }

    private fun openOffers(offerId: Int? = null) {
        _cmd.value = Command.OpenOffers(offerId)
    }

    // Command

    sealed class Command {
        data class OpenOffers(val offerId: Int?) : Command()
        object UploadNovaCertificate : Command()
        object UploadInsuranceCertificate : Command()
    }
}

@Keep
enum class MissionFilter {
    TODAY,
    MONTH
}
