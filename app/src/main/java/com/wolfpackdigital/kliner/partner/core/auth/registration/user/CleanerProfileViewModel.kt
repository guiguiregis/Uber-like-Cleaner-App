package com.wolfpackdigital.kliner.partner.core.auth.registration.user

import android.net.Uri
import android.util.Patterns
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.FILENAME
import com.wolfpackdigital.kliner.partner.shared.useCases.GeneralOption
import com.wolfpackdigital.kliner.partner.shared.useCases.GetCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetOptionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.Kind
import com.wolfpackdigital.kliner.partner.shared.useCases.PartnerAttributes
import com.wolfpackdigital.kliner.partner.shared.useCases.PhotoTypes
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class CleanerProfileViewModel(
    private val kind: Kind,
    private val createCleanerProfileUseCase: CreateCleanerProfileUseCase,
    private val uploadUserPhotoUseCase: UploadUserPhotoUseCase,
    private val getCleanerProfileUseCase: GetCleanerProfileUseCase,
    private val getOptionsUseCase: GetOptionsUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val lastName = MutableLiveData<String>()
    val errorLastName = MutableLiveData<Int>(null)
    val firstName = MutableLiveData<String>()
    val errorFirstName = MutableLiveData<Int>(null)
    val email = MutableLiveData<String>()
    val errorEmail = MutableLiveData<Int>()
    val birthDate = MutableLiveData<String>()
    val errorBirthDate = MutableLiveData<Int>(null)
    val gender = MutableLiveData<String>()
    val profileImageUri = MutableLiveData<Uri>()
    val profileImageUrl = profileImageUri.map { it?.toString() }

    val canEditProfile = MutableLiveData(true)

    val genderList = MutableLiveData<List<GeneralOption>>()

    fun refreshProfileData() {
        performApiCall {
            genderList.value ?: run {
                when (val result = getOptionsUseCase.executeNow(Unit)) {
                    is Result.Success -> {
                        genderList.value = result.data.optionsGender
                    }
                    is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
                }
            }
            cleanerProfile?.id?.let {
                when (val result = getCleanerProfileUseCase.executeNow(it)) {
                    is Result.Success -> {
                        fillProfileData(result.data)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun fillProfileData(profile: CleanerProfile) {
        canEditProfile.value = false
        cleanerProfile = profile

        lastName.value = profile.lastName
        firstName.value = profile.firstName
        email.value = profile.partnerAttributes?.email
        birthDate.value = profile.birthDate
        gender.value = genderList.value?.firstOrNull { it.id == profile.genderID }?.name
        profileImageUri.value = profile.photoUrl?.toUri()

        validateProfile(profile) {}
    }

    // Actions

    fun onNext() {
        val genderID = genderList.value?.firstOrNull { it.name == gender.value }?.id

        val profile = CleanerProfile(
            firstName = firstName.value,
            lastName = lastName.value,
            birthDate = birthDate.value,
            genderID = genderID,
            kind = kind,
            partnerAttributes = PartnerAttributes(email = email.value)
        )
        validateProfile(profile) {
            if (canEditProfile.value == false)
                _cmd.value = Command.GoNext
            else
                when (val response = createCleanerProfileUseCase.executeNow(profile)) {
                    is Result.Success -> {
                        cleanerProfile = response.data
                        uploadProfilePhoto()
                    }
                    is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
                }
        }
    }

    private suspend fun uploadProfilePhoto() {
        val uri = profileImageUri.value ?: return
        when (val response = uploadUserPhotoUseCase.executeNow(
            UploadUserPhotoRequest(PhotoTypes.PROFILE_PHOTO, mapOf(FILENAME to uri))
        )) {
            is Result.Success -> _cmd.value = Command.GoNext
            is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
        }
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

        errorEmail.value = if (profile.partnerAttributes?.email.isNullOrEmpty()) {
            error = true
            R.string.error_company_email
        } else if (
            !Patterns.EMAIL_ADDRESS.matcher(profile.partnerAttributes?.email ?: "").matches()
        ) {
            error = true
            R.string.error_company_email_invalid
        } else
            null

        errorBirthDate.value = if (profile.birthDate.isNullOrEmpty()) {
            error = true
            R.string.error_birth_date
        } else
            null

        if (profileImageUri.value == null) {
            _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.error_profile_photo)
            return
        }

        if (!error)
            performApiCall { onProfileValid() }
    }

    fun openDatePicker() {
        _cmd.value = Command.OpenDatePicker
    }

    fun pickImage() {
        if (canEditProfile.value == true)
            _cmd.value = Command.OpenImagePicker
        else
            _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.photo_not_editable)
    }

    // Command

    sealed class Command {
        object OpenImagePicker : Command()
        object OpenDatePicker : Command()
        object GoNext : Command()
    }
}
