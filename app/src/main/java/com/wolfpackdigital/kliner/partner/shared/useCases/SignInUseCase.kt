package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.AuthRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class SignInUseCase(private val repo: AuthRepoI) : BaseUseCase<SignInRequest, Unit>() {
    override suspend fun run(params: SignInRequest) = try {
        Result.Success(repo.signIn(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class SignInRequest(
    @SerializedName("phone_number")
    val phoneNumber: String = ""
)
