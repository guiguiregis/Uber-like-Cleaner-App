package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class GetOptionsUseCase(
    private val mainRepoI: MainRepoI
) : BaseUseCase<Unit, OptionsResponse>() {
    override suspend fun run(params: Unit) = try {
        Result.Success(mainRepoI.getCompanyOptions())
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class OptionsResponse(
    @SerializedName("Options::Gender")
    val optionsGender: List<GeneralOption>,
    @SerializedName("Options::ServiceType")
    val optionsServiceType: List<GeneralOption>,
    @SerializedName("Options::WorkType")
    val optionsWorkType: List<GeneralOption>,
    @SerializedName("Options::AcceptedPayment")
    val optionsAcceptedPayment: List<GeneralOption>
)

@Keep
data class GeneralOption(
    val id: Int,
    val name: String,
    val type: String,
    @SerializedName("selected")
    val checked: Boolean = false,
    val enabled: Boolean = true,
    @SerializedName("work_types")
    val workTypes: List<GeneralOption>?,
    @Transient
    val onClick: (GeneralOption) -> Unit = {}
) {
    fun onClickOption() = onClick(this)
}
