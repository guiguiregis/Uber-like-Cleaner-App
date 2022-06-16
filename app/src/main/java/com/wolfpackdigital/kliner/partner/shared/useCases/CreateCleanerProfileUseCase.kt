package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.models.CleanerStatus
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class CreateCleanerProfileUseCase(
    private val repo: MainRepoI
) : BaseUseCase<CleanerProfile, CleanerProfile>() {
    override suspend fun run(params: CleanerProfile) = try {
        Result.Success(repo.createCleanerProfile(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class CreateCleanerProfileRequest(
    @SerializedName("cleaner_profile")
    val cleanerProfile: CleanerProfile
)

@Keep
data class CleanerProfile(
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("birthday")
    val birthDate: String? = null,
    val kind: Kind? = null,
    val status: CleanerStatus? = CleanerStatus.INACTIVE,
    // This is the ID of the CLEANER profile
    val id: Int? = null,
    val country: String? = null,
    @SerializedName("photo_url")
    val photoUrl: String? = null,
    @SerializedName("identity_card_url")
    val idCardUrl: IdentityCard? = null,
    @SerializedName("passport_url")
    val passportUrl: String? = null,
    @SerializedName("residence_permit_url")
    val residencePermitUrl: String? = null,
    @SerializedName("nova_certificate")
    val novaCertificate: Certificate? = null,
    @SerializedName("insurance_certificate")
    val insuranceCertificate: Certificate? = null,
    @SerializedName("partnership_contract")
    val partnershipContract: Certificate? = null,
    @SerializedName("service_type_ids")
    val serviceTypeIDS: List<Int>? = null,
    @SerializedName("work_type_ids")
    val workTypeIDS: List<Int>? = null,
    @SerializedName("accepted_payment_ids")
    val acceptedPaymentIDs: List<Int>? = null,
    @SerializedName("activity_zones_attributes")
    val activityZonesAttributes: List<AddressItem>? = null,
    @SerializedName("gender_id")
    val genderID: Int? = null,
    @SerializedName("onboarding_status")
    val onboardingStatus: OnboardingStatus? = null,
    @SerializedName("partner_attributes")
    val partnerAttributes: PartnerAttributes? = null,
    @SerializedName("cleaning_company")
    val cleaningCompany: CleaningCompany? = null,
    val rating: Double? = null,
    @SerializedName("quality")
    val qualityRank: QualityRank? = null,
    @SerializedName("invited_by")
    val invitedBy: CleanerProfile? = null,

    @Transient
    val onClick: (CleanerProfile) -> Unit = {}
) {
    fun onSelect() = onClick(this)

    val fullName: String
        get() = "$firstName $lastName"

    fun isEmployer() = kind == Kind.EMPLOYER
    fun isIndependent() = kind == Kind.INDEPENDENT
    fun isEmployee() = kind == Kind.EMPLOYEE
}

@Keep
data class Certificate(
    val url: String? = null,
    @SerializedName("uploaded_at")
    val uploadedAt: String? = null
)

@Keep
enum class Kind {
    @SerializedName("employer")
    EMPLOYER,

    @SerializedName("independent")
    INDEPENDENT,

    @SerializedName("employee")
    EMPLOYEE;
}

@Keep
enum class Rank {
    @SerializedName("Gold")
    Gold,

    @SerializedName("Silver")
    Silver,

    @SerializedName("Bronze")
    Bronze,

    @SerializedName("unspecified")
    Unspecified
}

@Keep
data class QualityRank(
    @SerializedName("rank_name")
    val rankName: String? = null,
    @SerializedName("rank_key")
    val rankKey: Rank? = null
)

@Keep
data class IdentityCard(
    val front: String?,
    val back: String?
)
