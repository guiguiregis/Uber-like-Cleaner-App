package com.wolfpackdigital.kliner.partner.core.auth.registration.idcard

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.FILENAME
import com.wolfpackdigital.kliner.partner.shared.useCases.ID_CARD_BACK
import com.wolfpackdigital.kliner.partner.shared.useCases.ID_CARD_FRONT
import com.wolfpackdigital.kliner.partner.shared.useCases.PhotoTypes
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateCleanerProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class IdentityCardViewModel(
    private val updateCleanerProfileUseCase: UpdateCleanerProfileUseCase,
    private val uploadUserPhotoUseCase: UploadUserPhotoUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    val country = MutableLiveData<String>()
    val errorCountry = MutableLiveData<Int>()

    val idCardFrontSideUri = MutableLiveData<Uri>(cleanerProfile?.idCardUrl?.front?.toUri())
    val idCardBackSideUri = MutableLiveData<Uri>(cleanerProfile?.idCardUrl?.back?.toUri())
    val passportUri = MutableLiveData<Uri>(cleanerProfile?.passportUrl?.toUri())
    val residencePermitUri = MutableLiveData<Uri>(cleanerProfile?.residencePermitUrl?.toUri())

    private val enabledCardObserver = Observer<Any> {
        val idFront = idCardFrontSideUri.value
        val idBack = idCardBackSideUri.value
        val passport = passportUri.value
        val permit = residencePermitUri.value

        enabledCard.value = when {
            (idFront != null || idBack != null) && passport == null && permit == null -> PhotoTypes.ID_CARD
            idFront == null && idBack == null && passport != null && permit == null -> PhotoTypes.PASSPORT
            idFront == null && idBack == null && passport == null && permit != null -> PhotoTypes.RESIDENCE_PERMIT
            else -> PhotoTypes.ALL
        }
    }
    private val enabledCard: MediatorLiveData<PhotoTypes> = MediatorLiveData<PhotoTypes>().apply {
        addSource(idCardFrontSideUri, enabledCardObserver)
        addSource(idCardBackSideUri, enabledCardObserver)
        addSource(passportUri, enabledCardObserver)
        addSource(residencePermitUri, enabledCardObserver)
    }

    val idCardEnabled = enabledCard.map {
        it in listOf(PhotoTypes.ID_CARD, PhotoTypes.ALL)
    }
    val passportEnabled = enabledCard.map {
        it in listOf(PhotoTypes.PASSPORT, PhotoTypes.ALL)
    }
    val residencePermitEnabled = enabledCard.map {
        it in listOf(PhotoTypes.RESIDENCE_PERMIT, PhotoTypes.ALL)
    }

    // Actions

    fun openCountryPicker() {
        _cmd.value = Command.OpenCountryPicker
    }

    fun changeIdCardFrontSide() {
        if (idCardFrontSideUri.value != null) {
            idCardFrontSideUri.value = null
            return
        }
        _cmd.value = Command.ChangeIdCardFrontSide
    }

    fun changeIdCardBackSide() {
        if (idCardBackSideUri.value != null) {
            idCardBackSideUri.value = null
            return
        }
        _cmd.value = Command.ChangeIdCardBackSide
    }

    fun changePassport() {
        if (passportUri.value != null) {
            passportUri.value = null
            return
        }
        _cmd.value = Command.ChangePassport
    }

    fun changeResidencePermit() {
        if (residencePermitUri.value != null) {
            residencePermitUri.value = null
            return
        }
        _cmd.value = Command.ChangeResidencePermit
    }

    @Suppress("ReturnCount", "ComplexMethod")
    fun onNext() {
        val selectedCountry = country.value
        if (selectedCountry.isNullOrBlank()) {
            errorCountry.value = R.string.error_country
            return
        } else
            errorCountry.value = null

        val request = when (enabledCard.value) {
            PhotoTypes.ID_CARD -> {
                val frontSideUri = idCardFrontSideUri.value
                val backSideUri = idCardBackSideUri.value
                if (frontSideUri == null || backSideUri == null) {
                    _baseCmd.value =
                        BaseCommand.ShowSnackbarById(R.string.error_upload_both_sides_of_id)
                    return
                }

                UploadUserPhotoRequest(
                    PhotoTypes.ID_CARD,
                    mapOf(
                        ID_CARD_FRONT to frontSideUri,
                        ID_CARD_BACK to backSideUri
                    )
                )
            }
            PhotoTypes.PASSPORT -> {
                val uri = passportUri.value ?: return
                UploadUserPhotoRequest(PhotoTypes.PASSPORT, mapOf(FILENAME to uri))
            }
            PhotoTypes.RESIDENCE_PERMIT -> {
                val uri = residencePermitUri.value ?: return
                UploadUserPhotoRequest(PhotoTypes.RESIDENCE_PERMIT, mapOf(FILENAME to uri))
            }
            PhotoTypes.ALL -> {
                _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.error_no_id_selected)
                return
            }
            else -> return
        }

        performApiCall {
            val profile = cleanerProfile?.copy(country = selectedCountry) ?: return@performApiCall
            when (val response = updateCleanerProfileUseCase.executeNow(profile)) {
                is Result.Success -> uploadPhoto(request)
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
            }
        }
    }

    private suspend fun uploadPhoto(request: UploadUserPhotoRequest) {
        when (val response = uploadUserPhotoUseCase.executeNow(request)) {
            is Result.Success -> _cmd.value = Command.GoNext
            is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
        }
    }
    // Command

    sealed class Command {
        object OpenCountryPicker : Command()
        object ChangeIdCardFrontSide : Command()
        object ChangeIdCardBackSide : Command()
        object ChangePassport : Command()
        object ChangeResidencePermit : Command()
        object GoNext : Command()
    }
}
