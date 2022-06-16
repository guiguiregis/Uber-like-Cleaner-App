package com.wolfpackdigital.kliner.partner.core.main.more.profile

import android.net.Uri
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.BankAccount
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.FILENAME
import com.wolfpackdigital.kliner.partner.shared.useCases.GeneralOption
import com.wolfpackdigital.kliner.partner.shared.useCases.GetBankAccountUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCountriesUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetOptionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.ItemCountry
import com.wolfpackdigital.kliner.partner.shared.useCases.PhotoTypes
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.NavigationKeys.ARG_PHONE_NUMBER
import com.wolfpackdigital.kliner.partner.shared.utils.NavigationKeys.ARG_PREFIX
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.appVersion
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.autoSelectWorkTypeByServiceTypeConditioning
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.daysLeftFrom
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.filterCheckedItems
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getServiceTypeConditioning
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.onOptionsClicked
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("TooManyFunctions")
class ProfileDetailsViewModel(
    private val uploadUserPhotoUseCase: UploadUserPhotoUseCase,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val getBankAccountUseCase: GetBankAccountUseCase,
    private val getCleanerProfileUseCase: GetCleanerProfileUseCase,
    private val updateCleanerProfileUseCase: UpdateCleanerProfileUseCase,
    private val getOptionsUseCase: GetOptionsUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val appVersionField get() = appVersion

    private val _cleanerData = MutableLiveData<CleanerProfile>(cleanerProfile)
    val cleanerData: LiveData<CleanerProfile> = _cleanerData

    private val allDocumentsPresentMediator = MediatorLiveData<Boolean>()

    val profileImageUrl = MutableLiveData<String>()
    val rating = _cleanerData.map { it.rating?.toString() ?: "" }
    val firstName = _cleanerData.map { it.firstName }
    val lastName = _cleanerData.map { it.lastName }
    val email = _cleanerData.map { it.partnerAttributes?.email }
    val address = _cleanerData.map { getDisplayAddress(it) }
    val birthDate = _cleanerData.map { it.birthDate }

    val bankAccount = MutableLiveData<BankAccount>()
    var countryCode = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    private val _typesOfService = MutableLiveData<List<GeneralOption>>()
    val typesOfService: LiveData<List<GeneralOption>>
        get() = _typesOfService
    private val _typesOfWork = MutableLiveData<List<GeneralOption>>()
    val typesOfWork: LiveData<List<GeneralOption>>
        get() = _typesOfWork

    private var updateTypesJob: Job? = null
    val selectedWorkTypes = _typesOfWork.map {
        it?.filterCheckedItems()
    }
    val selectedServiceTypes = _typesOfService.map {
        if (cleanerProfile?.isIndependent() == true) {
            (it?.getServiceTypeConditioning() ?: mutableListOf())
                .autoSelectWorkTypeByServiceTypeConditioning(
                    _typesOfWork
                )
        }
        it?.filterCheckedItems()
    }

    val companyVisible = _cleanerData.map { it.isEmployer() || it.isIndependent() }
    val documentsVisible = _cleanerData.map { it.isEmployer() || it.isIndependent() }

    val daysLeftToUploadCertificate by lazy {
        contractSignedAt?.daysLeftFrom(Constants.MAX_DAYS_LEFT_TO_UPLOAD_CERTIFICATE)?.let {
            lastKnownLeftDaysToUploadCertificate = it
            it
        } ?: run {
            lastKnownLeftDaysToUploadCertificate
        }
    }
    private val documentsObserver = Observer<Any> {
        val novaCertificatePresent =
            !cleanerData.value?.novaCertificate?.url.isNullOrEmpty()
        val insuranceCertificatePresent =
            !cleanerData.value?.insuranceCertificate?.url.isNullOrEmpty()
        allDocumentsPresentMediator.value =
            novaCertificatePresent && insuranceCertificatePresent
    }

    init {
        _cleanerData.value = cleanerProfile
        allDocumentsPresentMediator.addSource(_cleanerData, documentsObserver)
        profileImageUrl.value = _cleanerData.value?.photoUrl
    }

    // Actions

    fun fetchData() {
        performApiCall {
            fetchProfileData()
            fetchOptions()

            when (val result = getCountriesUseCase.executeNow(Unit)) {
                is Result.Success -> {
                    result.data.firstOrNull {
                        _cleanerData.value?.partnerAttributes?.phoneNumber?.contains(
                            it.countryCode
                        ) ?: false
                    }?.let { fillPhoneDetails(it) }
                }
                is Result.Error ->
                    _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }

            when (val result = getBankAccountUseCase.executeNow(Unit)) {
                is Result.Success -> {
                    bankAccount.value = result.data
                }
            }
        }
    }

    private suspend fun fetchProfileData() {
        val id = cleanerProfile?.id ?: return
        when (val result = getCleanerProfileUseCase.executeNow(id)) {
            is Result.Success -> {
                cleanerProfile = result.data
                _cleanerData.value = result.data
            }
            is Result.Error -> {
                _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    private suspend fun fetchOptions() {
        when (val result = getOptionsUseCase.executeNow(Unit)) {
            is Result.Success -> {
                _typesOfWork.value =
                    result.data.optionsWorkType.map {
                        it.copy(
                            onClick = { item -> onOptionsClicked(item, _typesOfWork) },
                            checked = _cleanerData.value?.workTypeIDS?.contains(it.id) ?: false,
                            enabled = _cleanerData.value?.isIndependent() == true
                        )
                    }
                _typesOfService.value =
                    result.data.optionsServiceType.map {
                        it.copy(
                            onClick = { item -> onOptionsClicked(item, _typesOfService) },
                            checked = _cleanerData.value?.serviceTypeIDS?.contains(it.id)
                                ?: false,
                            enabled = _cleanerData.value?.isIndependent() == true
                        )
                    }
                gender.value =
                    result.data.optionsGender.firstOrNull {
                        it.id == cleanerProfile?.genderID
                    }?.name ?: ""
            }
            is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
        }
    }

    private fun fillPhoneDetails(itemCountry: ItemCountry) {
        phoneNumber.value = cleanerProfile?.partnerAttributes?.phoneNumber?.removePrefix(
            itemCountry.countryCode
        )
        countryCode.value = "${itemCountry.flag} ${itemCountry.countryCode}"
    }

    fun updateTypes() {
        updateTypesJob?.cancel()
        updateTypesJob = newUpdateTypesJob()
    }

    private fun newUpdateTypesJob() = viewModelScope.launch {
        withContext(Dispatchers.Main) {
            delay(Constants.UPDATE_DEBOUNCE)
            val profile = _cleanerData.value ?: return@withContext

            val serviceTypes = selectedServiceTypes.value ?: listOf()
            val workTypes = selectedWorkTypes.value ?: listOf()

            _baseCmd.value = when {
                profile.isEmployer() && serviceTypes.isEmpty() -> BaseCommand.ShowToastById(R.string.error_types)
                profile.isIndependent() && (serviceTypes.isEmpty() || workTypes.isEmpty()) ->
                    BaseCommand.ShowToastById(R.string.error_types)
                workTypes.sorted() == profile.workTypeIDS?.sorted() &&
                        serviceTypes.sorted() == profile.serviceTypeIDS?.sorted() -> return@withContext
                else -> when (val result =
                    updateCleanerProfileUseCase.executeNow(
                        profile.copy(
                            workTypeIDS = workTypes,
                            serviceTypeIDS = serviceTypes
                        )
                    )) {
                    is Result.Success -> {
                        _cleanerData.value = result.data
                        BaseCommand.ShowToastById(R.string.options_updated)
                    }
                    is Result.Error -> {
                        _cleanerData.value = profile
                        BaseCommand.ShowToast(result.error)
                    }
                }
            }
            fetchOptions()
        }
    }

    private fun getDisplayAddress(cleanerProfile: CleanerProfile?) =
        if (cleanerProfile?.isEmployee() == true) {
            var displayAddress = ""
            cleanerProfile.activityZonesAttributes?.firstOrNull { it.default }?.let {
                displayAddress += it.streetLine
            }
            cleanerProfile.activityZonesAttributes?.filterNot { it.default }?.map { it.city }
                ?.joinToString(", ")?.let {
                    if (displayAddress.isNotEmpty())
                        displayAddress += "\n"
                    displayAddress += it
                }
            displayAddress
        } else
            cleanerProfile?.activityZonesAttributes?.map { it.city }?.joinToString(", ") ?: ""

    fun onEditPhoneNumber() {
        val prefix = countryCode.value?.substring(countryCode.value?.indexOf("+") ?: 0) ?: ""
        _baseCmd.value = BaseCommand.PerformNavById(
            R.id.phoneConfirmationFragment,
            bundleOf(
                ARG_PREFIX to (prefix),
                ARG_PHONE_NUMBER to (phoneNumber.value ?: "")
            )
        )
    }

    fun onEditActivityArea() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToActivityAreaFragment()
        )
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

    fun onUploadNovaCertificate() {
        _cmd.value = Command.UploadNovaCertificate
    }

    fun onDownloadNovaCertificate() {
        _cmd.value = cleanerProfile?.novaCertificate?.url?.toUri()?.let { uri ->
            Command.DownloadFile(uri)
        }
    }

    fun onUploadInsuranceCertificate() {
        _cmd.value = Command.UploadInsuranceCertificate
    }

    fun onDownloadInsuranceCertificate() {
        _cmd.value = cleanerProfile?.insuranceCertificate?.url?.toUri()?.let { uri ->
            Command.DownloadFile(uri)
        }
    }

    fun onDownloadPartnershipContract() {
        _cmd.value = cleanerProfile?.partnershipContract?.url?.toUri()?.let { uri ->
            Command.DownloadFile(uri)
        }
    }

    fun onOpenDataProtection() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToGdprFragment()
        )
    }

    fun onLogout() {
        _cmd.value = Command.ShowLogoutDialog
    }

    fun onDeleteAccount() {
        _cmd.value = Command.ShowDeleteAccountDialog
    }

    fun showCompanyDetails() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToCompanyProfileFragment()
        )
    }

    fun addBankAccount() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToAddBankAccountFragment(
                bankAccount = bankAccount.value
            )
        )
    }

    fun openAuthActivity() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToAuthActivity2()
        )
    }

    fun pickImage() {
        _cmd.value = Command.OpenImagePicker
    }

    fun uploadProfilePhoto(uri: Uri) {
        performApiCall {
            when (val response = uploadUserPhotoUseCase.executeNow(
                UploadUserPhotoRequest(PhotoTypes.PROFILE_PHOTO, mapOf(FILENAME to uri))
            )) {
                is Result.Success -> {
                    profileImageUrl.value = uri.toString()
                    cleanerProfile = cleanerProfile?.copy(photoUrl = uri.toString())
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
            }
        }
    }

    // Command

    sealed class Command {
        object OpenImagePicker : Command()
        object ShowLogoutDialog : Command()
        object ShowDeleteAccountDialog : Command()
        object UploadNovaCertificate : Command()
        object UploadInsuranceCertificate : Command()
        data class DownloadFile(val uri: Uri) : Command()
    }
}
