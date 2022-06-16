@file:Suppress("TooManyFunctions")

package com.wolfpackdigital.kliner.partner.data.repo

import com.wolfpackdigital.kliner.partner.data.api.MainAPI
import com.wolfpackdigital.kliner.partner.shared.useCases.ActivityRecordsResponse
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressItem
import com.wolfpackdigital.kliner.partner.shared.useCases.AssignEmployeeRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.BankAccount
import com.wolfpackdigital.kliner.partner.shared.useCases.BankAccountRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.CleaningCompany
import com.wolfpackdigital.kliner.partner.shared.useCases.ConfirmMissionBody
import com.wolfpackdigital.kliner.partner.shared.useCases.ConfirmMissionRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.ConfirmPhoneNumberRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.ContractData
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateCleanerProfileRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateCleaningCompanyRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateEmployeeProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateEmployeeProfileRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.Mission
import com.wolfpackdigital.kliner.partner.shared.useCases.MissionBody
import com.wolfpackdigital.kliner.partner.shared.useCases.OptionsResponse
import com.wolfpackdigital.kliner.partner.shared.useCases.PhotoTypes
import com.wolfpackdigital.kliner.partner.shared.useCases.SignContractRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.TextResponse
import com.wolfpackdigital.kliner.partner.shared.useCases.TextType
import com.wolfpackdigital.kliner.partner.shared.useCases.TransactionItem
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdateMissionRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdatePhoneNumberRequest
import java.io.InvalidObjectException
import okhttp3.MultipartBody
import org.koin.core.KoinComponent
import org.koin.core.inject

private const val IDENTITY_CARD_ID = "identity_card[%s]"
private const val IDENTITY_CARD_NAME = "id_card_%s"

interface MainRepoI {
    suspend fun searchAddress(query: String): List<AddressItem>
    suspend fun createCleanerProfile(profile: CleanerProfile): CleanerProfile
    suspend fun updateCleanerProfile(profile: CleanerProfile): CleanerProfile
    suspend fun getCleanerProfile(id: Int): CleanerProfile
    suspend fun uploadPhoto(type: PhotoTypes, image: MultipartBody.Part?)
    suspend fun uploadPhotos(type: PhotoTypes, images: Map<String, MultipartBody.Part>)
    suspend fun createCleaningCompany(company: CleaningCompany): CleaningCompany
    suspend fun getCompanyOptions(): OptionsResponse
    suspend fun getText(textType: TextType): TextResponse
    suspend fun getCleaningCompanyDetails(id: Int): CleaningCompany
    suspend fun signContract(contractData: ContractData)
    suspend fun getCompanyCleaners(companyID: Int, missionID: Int?): List<CleanerProfile>
    suspend fun getEmployeeProfile(companyID: Int, employeeID: Int): CleanerProfile
    suspend fun createEmployeeProfile(profile: CreateEmployeeProfile): CleanerProfile
    suspend fun updateEmployeeProfile(profile: CleanerProfile): CleanerProfile
    suspend fun deleteEmployeeProfile(companyID: Int, employeeID: Int)
    suspend fun updateCleaningCompany(cleaningCompany: CleaningCompany): CleaningCompany
    suspend fun getMissions(month: Int?, year: Int?): List<Mission>
    suspend fun getUnconfirmedMissions(): List<Mission>
    suspend fun getUnassignedMissions(): List<Mission>
    suspend fun getMissionById(id: Int): Mission
    suspend fun assignEmployeeToMission(updateMissionRequest: UpdateMissionRequest): Mission
    suspend fun confirmMission(confirmMissionRequest: ConfirmMissionRequest): Mission
    suspend fun startMission(id: Int): Mission
    suspend fun finishMission(id: Int): Mission
    suspend fun cancelMission(id: Int): Mission
    suspend fun callCustomer(id: Int)
    suspend fun getOffers(): List<Mission>
    suspend fun acceptOffer(id: Int): Mission
    suspend fun refuseOffer(id: Int)
    suspend fun createBankAccount(bankAccount: BankAccount): BankAccount
    suspend fun updateBankAccount(bankAccount: BankAccount): BankAccount
    suspend fun getBankAccount(): BankAccount
    suspend fun toggleNotificationSettings(enabled: Boolean)
    suspend fun updatePhoneNumber(phoneNumber: String)
    suspend fun confirmPhoneNumber(code: String)
    suspend fun getActivityRecords(): ActivityRecordsResponse
    suspend fun getCompanyActivity(): List<TransactionItem>
}

class MainRepo : KoinComponent, MainRepoI {
    private val api by inject<MainAPI>()

    override suspend fun searchAddress(query: String) =
        api.searchAddress(query)

    override suspend fun createCleanerProfile(profile: CleanerProfile) =
        api.createCleanerProfile(CreateCleanerProfileRequest(profile))

    override suspend fun updateCleanerProfile(profile: CleanerProfile) =
        api.updateCleanerProfile(profile.id ?: -1, CreateCleanerProfileRequest(profile))

    override suspend fun getCleanerProfile(id: Int) =
        api.getCleanerProfile(id)

    override suspend fun uploadPhoto(
        type: PhotoTypes,
        image: MultipartBody.Part?
    ) = if (image == null) {
        throw InvalidObjectException("Invalid photo")
    } else {
        when (type) {
            PhotoTypes.PROFILE_PHOTO ->
                api.uploadCleanerProfilePhoto(image)
            PhotoTypes.PASSPORT ->
                api.uploadCleanerPassportPhoto(image)
            PhotoTypes.RESIDENCE_PERMIT ->
                api.uploadCleanerResidencePermitPhoto(image)
            PhotoTypes.NOVA_CERTIFICATE ->
                api.uploadCleanerNovaCertificate(image)
            PhotoTypes.INSURANCE_CERTIFICATE ->
                api.uploadCleanerInsuranceCertificate(image)
            else -> throw InvalidObjectException("Invalid photo type")
        }
    }

    override suspend fun uploadPhotos(
        type: PhotoTypes,
        images: Map<String, MultipartBody.Part>
    ) = when (type) {
        PhotoTypes.ID_CARD -> {
            val requestBodyBuilder =
                MultipartBody.Builder().setType(MultipartBody.FORM)
            for (image in images) {
                requestBodyBuilder.addFormDataPart(
                    IDENTITY_CARD_ID.format(image.key),
                    IDENTITY_CARD_NAME.format(image.key),
                    image.value.body
                )
            }
            api.uploadCleanerIDCardPhoto(requestBodyBuilder.build())
        }
        else -> throw InvalidObjectException("Invalid photo type")
    }

    override suspend fun createCleaningCompany(company: CleaningCompany) =
        api.createCleaningCompany(CreateCleaningCompanyRequest(company))

    override suspend fun updateCleaningCompany(cleaningCompany: CleaningCompany) =
        api.updateCleaningCompany(
            cleaningCompany.id ?: -1,
            CreateCleaningCompanyRequest(cleaningCompany)
        )

    override suspend fun getCompanyOptions() = api.getCompanyOptions()

    override suspend fun getCleaningCompanyDetails(id: Int) = api.getCleaningCompanyDetails(id)

    override suspend fun getText(textType: TextType) = api.getText(textType)

    override suspend fun signContract(contractData: ContractData) =
        api.signContract(SignContractRequest(contractData))

    override suspend fun getCompanyCleaners(companyID: Int, missionID: Int?) =
        api.getCompanyCleaners(companyID, missionID)

    override suspend fun getEmployeeProfile(companyID: Int, employeeID: Int) =
        api.getEmployeeProfile(companyID, employeeID)

    override suspend fun createEmployeeProfile(profile: CreateEmployeeProfile) =
        api.createEmployeeProfile(
            profile.companyId,
            CreateEmployeeProfileRequest(profile.request.employee)
        )

    override suspend fun updateEmployeeProfile(profile: CleanerProfile) =
        api.updateEmployeeProfile(
            profile.cleaningCompany?.id ?: -1,
            profile.id ?: -1,
            CreateEmployeeProfileRequest(profile)
        )

    override suspend fun deleteEmployeeProfile(companyID: Int, employeeID: Int) =
        api.deleteEmployee(companyID, employeeID)

    override suspend fun getMissions(month: Int?, year: Int?) =
        api.getMissions(month, year)

    override suspend fun getUnconfirmedMissions() =
        api.getUnconfirmedMissions()

    override suspend fun getUnassignedMissions() =
        api.getUnassignedMissions()

    override suspend fun getMissionById(id: Int) =
        api.getMissionById(id)

    override suspend fun assignEmployeeToMission(updateMissionRequest: UpdateMissionRequest) =
        api.assignEmployeeToMission(
            updateMissionRequest.missionId,
            AssignEmployeeRequest(
                MissionBody(
                    updateMissionRequest.cleanerProfileId,
                    updateMissionRequest.assignmentScope
                )
            )
        )

    override suspend fun confirmMission(confirmMissionRequest: ConfirmMissionRequest) =
        api.confirmMission(
            confirmMissionRequest.missionId,
            ConfirmMissionBody(confirmMissionRequest.confirm)
        )

    override suspend fun startMission(id: Int) =
        api.startMission(id)

    override suspend fun finishMission(id: Int) =
        api.finishMission(id)

    override suspend fun cancelMission(id: Int) =
        api.cancelMission(id)

    override suspend fun callCustomer(id: Int) =
        api.callCustomer(id)

    override suspend fun getOffers() =
        api.getOffers()

    override suspend fun acceptOffer(id: Int) =
        api.acceptOffer(id)

    override suspend fun refuseOffer(id: Int) =
        api.refuseOffer(id)

    override suspend fun createBankAccount(bankAccount: BankAccount) =
        api.createBankAccount(BankAccountRequest(bankAccount))

    override suspend fun updateBankAccount(bankAccount: BankAccount) =
        api.updateBankAccount(BankAccountRequest(bankAccount))

    override suspend fun getBankAccount(): BankAccount =
        api.getBankAccount()

    override suspend fun toggleNotificationSettings(enabled: Boolean) =
        api.toggleNotificationsSettings(enabled)

    override suspend fun updatePhoneNumber(phoneNumber: String) =
        api.updatePhoneNumber(UpdatePhoneNumberRequest(phoneNumber))

    override suspend fun confirmPhoneNumber(code: String) =
        api.confirmPhoneNumber(ConfirmPhoneNumberRequest(code))

    override suspend fun getActivityRecords() =
        api.getActivityRecords()

    override suspend fun getCompanyActivity(): List<TransactionItem> =
        api.getCompanyActivity()
}
