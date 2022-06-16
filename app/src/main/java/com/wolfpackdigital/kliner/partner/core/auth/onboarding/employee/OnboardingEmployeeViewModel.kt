package com.wolfpackdigital.kliner.partner.core.auth.onboarding.employee

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.wolfpackdigital.kliner.partner.core.main.MainActivity
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.FILENAME
import com.wolfpackdigital.kliner.partner.shared.useCases.GeneralOption
import com.wolfpackdigital.kliner.partner.shared.useCases.GetEmployeeProfileUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetOptionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.PhotoTypes
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.UploadUserPhotoUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class OnboardingEmployeeViewModel(
    private val id: Int,
    private val uploadUserPhotoUseCase: UploadUserPhotoUseCase,
    private val getEmployeeProfileUseCase: GetEmployeeProfileUseCase,
    private val getOptionsUseCase: GetOptionsUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    private val _typesOfWork = MutableLiveData<List<GeneralOption>>()
    val typesOfWork: LiveData<List<GeneralOption>>
        get() = _typesOfWork

    val cmd: LiveData<Command> = _cmd

    val profile = MutableLiveData<CleanerProfile>()
    val address = profile.map {
        it.activityZonesAttributes?.firstOrNull()?.streetLine ?: ""
    }

    val profileImageUri = MutableLiveData<Uri>()
    val profileImageUrl = profileImageUri.map { it.toString() }

    // Actions

    fun onNext() {
        performApiCall {
            val uri = profileImageUri.value ?: return@performApiCall
            when (val response = uploadUserPhotoUseCase.executeNow(
                UploadUserPhotoRequest(PhotoTypes.PROFILE_PHOTO, mapOf(FILENAME to uri))
            )) {
                is Result.Success -> {
                    _baseCmd.value = BaseCommand.PerformNavAction(
                        OnboardingEmployeeFragmentDirections.actionOnboardingEmployeeFragmentToMainActivity(),
                        MainActivity.getMainActivityExtras()
                    )
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
            }
        }
    }

    fun pickImage() {
        _cmd.value = Command.OpenImagePicker
    }

    fun fetchCleanerProfile() {
        performApiCall {
            when (val result = getEmployeeProfileUseCase.executeNow(id)) {
                is Result.Success -> {
                    profile.value = result.data
                    getOptions()
                }
                is Result.Error ->
                    _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    private fun getOptions() {
        performApiCall {
            when (val result = getOptionsUseCase.executeNow(Unit)) {
                is Result.Success -> {
                    _typesOfWork.value = result.data.optionsWorkType.map {
                        it.copy(
                            onClick = {},
                            checked = profile.value?.workTypeIDS?.contains(it.id) ?: false,
                            enabled = false
                        )
                    }
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowSnackbar(result.error)
            }
        }
    }

    // Command

    sealed class Command {
        object OpenImagePicker : Command()
    }
}
