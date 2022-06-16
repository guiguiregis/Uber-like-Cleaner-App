package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.AuthRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class ResendCodeUseCase(private val repo: AuthRepoI) : BaseUseCase<ResendCodeRequest, Unit>() {
    override suspend fun run(params: ResendCodeRequest) = try {
        Result.Success(repo.resendCode(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class ResendCodeRequest(
    @SerializedName("phone_number")
    val phoneNumber: String = ""
)
