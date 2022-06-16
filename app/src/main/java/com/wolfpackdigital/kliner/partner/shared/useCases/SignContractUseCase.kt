package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class SignContractUseCase(private val repo: MainRepoI) : BaseUseCase<String, Unit>() {
    override suspend fun run(params: String) = try {
        Result.Success(repo.signContract(ContractData("partnership", params)))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class SignContractRequest(
    val contract: ContractData
)

@Keep
data class ContractData(
    @SerializedName("contract_type")
    val contractType: String? = null,
    @SerializedName("contract_signed_at")
    val contractSignedAt: String? = null
)
