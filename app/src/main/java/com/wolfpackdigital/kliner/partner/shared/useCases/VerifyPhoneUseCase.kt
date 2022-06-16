package com.wolfpackdigital.kliner.partner.shared.useCases

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.models.CleanerStatus
import com.wolfpackdigital.kliner.partner.data.models.Token
import com.wolfpackdigital.kliner.partner.data.repo.AuthRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError
import kotlinx.parcelize.Parcelize

class VerifyPhoneUseCase(private val repo: AuthRepoI) :
    BaseUseCase<VerifyPhoneRequest, VerifyPhoneResult>() {
    override suspend fun run(params: VerifyPhoneRequest) = try {
        Result.Success(repo.verifyCode(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
enum class OnboardingStatus {
    @SerializedName("new")
    NEW,

    @SerializedName("partial")
    PARTIAL,

    @SerializedName("complete")
    COMPLETE
}

@Keep
data class VerifyPhoneRequest(
    @SerializedName("phone_number")
    val phoneNumber: String = "",
    @SerializedName("code")
    val code: String = ""
)

@Keep
data class VerifyPhoneResult(
    val id: Int? = null,
    @SerializedName("phone_number")
    val phoneNumber: String = "",
    val code: String = "",
    @SerializedName("onboarding_status")
    val onboardingStatus: OnboardingStatus,
    @SerializedName("cleaning_company_id")
    val companyID: Int?,
    @SerializedName("cleaner_profile_id")
    val cleanerID: Int?,
    @SerializedName("cleaner_profile_kind")
    val cleanerKind: Kind?,
    @SerializedName("cleaner_profile_status")
    val cleanerStatus: CleanerStatus?,
    val tokens: Token,
    @SerializedName("contract_signed_at")
    val contractSignedAt: String? = null
)

@Parcelize
@Keep
data class UserRegisterFlowData(
    val onboardingStatus: OnboardingStatus,
    val kind: Kind
) : Parcelable
