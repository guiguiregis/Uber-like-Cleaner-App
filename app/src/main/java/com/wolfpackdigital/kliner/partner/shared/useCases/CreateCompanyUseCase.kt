package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class CreateCompanyUseCase(private val repo: MainRepoI) :
    BaseUseCase<CleaningCompany, CleaningCompany>() {
    override suspend fun run(params: CleaningCompany) = try {
        Result.Success(repo.createCleaningCompany(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class CreateCleaningCompanyRequest(
    @SerializedName("cleaning_company")
    val cleaningCompany: CleaningCompany
)

@Keep
data class CleaningCompany(
    val name: String?,
    @SerializedName("identification_number")
    val siren: String?,
    @SerializedName("headquarter_attributes")
    val headquarterAttrs: HeadQuarterAttributes?,
    val id: Int? = null
)

@Keep
data class PartnerAttributes(
    val email: String? = null,
    @SerializedName("phone_number")
    val phoneNumber: String? = null
)

@Keep
data class HeadQuarterAttributes(
    @SerializedName("street_line_one")
    val streetLineOne: String?
)
