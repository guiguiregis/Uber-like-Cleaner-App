package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class UpdatePhoneNumberUseCase(
    private val repo: MainRepoI
) : BaseUseCase<String, Unit>() {
    override suspend fun run(params: String) = try {
        Result.Success(repo.updatePhoneNumber(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class UpdatePhoneNumberRequest(
    @SerializedName("phone_number")
    val phoneNumber: String
)
