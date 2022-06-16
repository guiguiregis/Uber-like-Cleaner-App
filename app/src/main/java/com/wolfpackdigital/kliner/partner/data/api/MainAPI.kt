package com.wolfpackdigital.kliner.partner.data.api

import com.wolfpackdigital.kliner.partner.shared.useCases.ActivityRecordsResponse
import com.wolfpackdigital.kliner.partner.shared.useCases.AddressItem
import com.wolfpackdigital.kliner.partner.shared.useCases.AssignEmployeeRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.BankAccount
import com.wolfpackdigital.kliner.partner.shared.useCases.BankAccountRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.useCases.CleaningCompany
import com.wolfpackdigital.kliner.partner.shared.useCases.ConfirmMissionBody
import com.wolfpackdigital.kliner.partner.shared.useCases.ConfirmPhoneNumberRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateCleanerProfileRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateCleaningCompanyRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.CreateEmployeeProfileRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.Mission
import com.wolfpackdigital.kliner.partner.shared.useCases.OptionsResponse
import com.wolfpackdigital.kliner.partner.shared.useCases.SignContractRequest
import com.wolfpackdigital.kliner.partner.shared.useCases.TextResponse
import com.wolfpackdigital.kliner.partner.shared.useCases.TextType
import com.wolfpackdigital.kliner.partner.shared.useCases.TransactionItem
import com.wolfpackdigital.kliner.partner.shared.useCases.UpdatePhoneNumberRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

@Suppress("TooManyFunctions")
interface MainAPI {

    @POST("partners/cleaner_profiles")
    suspend fun createCleanerProfile(@Body request: CreateCleanerProfileRequest): CleanerProfile

    @GET("partners/cleaner_profiles/{id}")
    suspend fun getCleanerProfile(@Path("id") id: Int): CleanerProfile

    @PATCH("partners/cleaner_profiles/{id}")
    suspend fun updateCleanerProfile(
        @Path("id") cleanerID: Int,
        @Body request: CreateCleanerProfileRequest
    ): CleanerProfile

    @Multipart
    @POST("partners/photo_uploads/profile_photo")
    suspend fun uploadCleanerProfilePhoto(
        @Part photo: MultipartBody.Part
    )

    @POST("partners/photo_uploads/identity_card")
    suspend fun uploadCleanerIDCardPhoto(
        @Body body: MultipartBody
    )

    @Multipart
    @POST("partners/photo_uploads/passport")
    suspend fun uploadCleanerPassportPhoto(
        @Part photo: MultipartBody.Part
    )

    @Multipart
    @POST("partners/photo_uploads/residence_permit")
    suspend fun uploadCleanerResidencePermitPhoto(
        @Part photo: MultipartBody.Part
    )

    @Multipart
    @POST("partners/photo_uploads/nova_certificate ")
    suspend fun uploadCleanerNovaCertificate(
        @Part photo: MultipartBody.Part
    )

    @Multipart
    @POST("partners/photo_uploads/insurance_certificate")
    suspend fun uploadCleanerInsuranceCertificate(
        @Part photo: MultipartBody.Part
    )

    @GET("backoffice/addresses")
    suspend fun searchAddress(@Query("q") query: String): List<AddressItem>

    @POST("partners/cleaning_companies")
    suspend fun createCleaningCompany(@Body request: CreateCleaningCompanyRequest): CleaningCompany

    @GET("partners/cleaning_companies/{id}")
    suspend fun getCleaningCompanyDetails(@Path("id") id: Int): CleaningCompany

    @PATCH("partners/cleaning_companies/{id}")
    suspend fun updateCleaningCompany(
        @Path("id") companyID: Int,
        @Body request: CreateCleaningCompanyRequest
    ): CleaningCompany

    @GET("options")
    suspend fun getCompanyOptions(): OptionsResponse

    @GET("texts/{text}")
    suspend fun getText(@Path("text") textType: TextType): TextResponse

    @POST("contracts")
    suspend fun signContract(@Body signContractRequest: SignContractRequest)

    @GET("partners/cleaning_companies/{id}/employees")
    suspend fun getCompanyCleaners(
        @Path("id") companyID: Int,
        @Query("mission_id") missionID: Int?
    ): List<CleanerProfile>

    @GET("partners/cleaning_companies/{companyID}/employees/{employeeID}")
    suspend fun getEmployeeProfile(
        @Path("companyID") companyID: Int,
        @Path("employeeID") employeeID: Int
    ): CleanerProfile

    @POST("partners/cleaning_companies/{companyID}/employees")
    suspend fun createEmployeeProfile(
        @Path("companyID") companyID: Int,
        @Body request: CreateEmployeeProfileRequest
    ): CleanerProfile

    @PATCH("partners/cleaning_companies/{companyID}/employees/{employeeID}")
    suspend fun updateEmployeeProfile(
        @Path("companyID") companyID: Int,
        @Path("employeeID") employeeID: Int,
        @Body profile: CreateEmployeeProfileRequest
    ): CleanerProfile

    @DELETE("partners/cleaning_companies/{companyID}/employees/{employeeID}")
    suspend fun deleteEmployee(
        @Path("companyID") companyID: Int,
        @Path("employeeID") employeeID: Int
    )

    @GET("partners/missions")
    suspend fun getMissions(
        @Query("filter[by_month]") month: Int?,
        @Query("filter[by_year]") year: Int?
    ): List<Mission>

    @GET("partners/missions/unconfirmed")
    suspend fun getUnconfirmedMissions(): List<Mission>

    @GET("partners/missions/on_assignment")
    suspend fun getUnassignedMissions(): List<Mission>

    @GET("partners/missions/{id}")
    suspend fun getMissionById(@Path("id") id: Int): Mission

    @PATCH("partners/missions/{id}")
    suspend fun assignEmployeeToMission(
        @Path("id") id: Int,
        @Body body: AssignEmployeeRequest
    ): Mission

    @POST("partners/missions/{id}/confirm")
    suspend fun confirmMission(
        @Path("id") id: Int,
        @Body body: ConfirmMissionBody
    ): Mission

    @POST("partners/missions/{id}/start")
    suspend fun startMission(@Path("id") id: Int): Mission

    @POST("partners/missions/{id}/finish")
    suspend fun finishMission(@Path("id") id: Int): Mission

    @POST("partners/missions/{id}/cancel")
    suspend fun cancelMission(@Path("id") id: Int): Mission

    @POST("partners/missions/{id}/call")
    suspend fun callCustomer(@Path("id") id: Int)

    @GET("partners/offers")
    suspend fun getOffers(): List<Mission>

    @POST("partners/offers/{id}/accept")
    suspend fun acceptOffer(@Path("id") id: Int): Mission

    @POST("partners/offers/{id}/refuse")
    suspend fun refuseOffer(@Path("id") id: Int)

    @GET("partners/bank_accounts")
    suspend fun getBankAccount(): BankAccount

    @POST("partners/bank_accounts")
    suspend fun createBankAccount(
        @Body request: BankAccountRequest
    ): BankAccount

    @PATCH("partners/bank_accounts")
    suspend fun updateBankAccount(
        @Body request: BankAccountRequest
    ): BankAccount

    @POST("partners/notifications")
    suspend fun toggleNotificationsSettings(enabled: Boolean)

    @PATCH("phone_numbers")
    suspend fun updatePhoneNumber(@Body request: UpdatePhoneNumberRequest)

    @POST("phone_numbers/confirm")
    suspend fun confirmPhoneNumber(@Body request: ConfirmPhoneNumberRequest)

    @GET("partners/activity_records")
    suspend fun getActivityRecords(): ActivityRecordsResponse

    @GET("partners/payments")
    suspend fun getCompanyActivity(): List<TransactionItem>
}
