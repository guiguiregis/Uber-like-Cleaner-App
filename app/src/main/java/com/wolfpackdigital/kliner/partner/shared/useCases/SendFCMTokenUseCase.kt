package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.notifications.KlinerMessagingService
import com.wolfpackdigital.kliner.partner.data.repo.AuthRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class SendFCMTokenUseCase(
    private val repo: AuthRepoI
) : BaseUseCase<Unit, FCMTokenResult>() {
    override suspend fun run(params: Unit) = try {
        val token = KlinerMessagingService.getDeviceToken() ?: ""
        val request = FCMTokenRequest(FCMToken(token = token))
        Result.Success(repo.sendFCMToken(request))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class FCMTokenRequest(
    val device: FCMToken
)

@Keep
data class FCMToken(
    val os: String? = "android",
    @SerializedName("app_type")
    val appType: String? = "partner",
    val token: String? = null,
    val enabled: Boolean = true
)

@Keep
data class FCMTokenResult(
    val id: Int,
    val os: String,
    val token: String
)
