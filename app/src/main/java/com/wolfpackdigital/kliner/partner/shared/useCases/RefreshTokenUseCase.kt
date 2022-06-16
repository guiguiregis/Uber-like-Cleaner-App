package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.models.Token
import com.wolfpackdigital.kliner.partner.data.repo.AuthRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

private const val GRANT_TYPE = "refresh_token"

class RefreshTokenUseCase(private val repo: AuthRepoI) :
    BaseUseCase<String, Token>() {
    override suspend fun run(params: String): Result<Token> = try {
        val request = RefreshTokenRequest(GRANT_TYPE, params)
        Result.Success(repo.refreshToken(request))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class RefreshTokenRequest(
    @SerializedName("grant_type")
    val grantType: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)
