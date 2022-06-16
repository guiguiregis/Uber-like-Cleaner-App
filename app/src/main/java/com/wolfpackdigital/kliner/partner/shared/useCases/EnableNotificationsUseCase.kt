package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.AuthRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class EnableNotificationsUseCase(
    private val repo: AuthRepoI
) : BaseUseCase<Boolean, FCMTokenResult>() {
    override suspend fun run(params: Boolean): Result<FCMTokenResult> = try {
        Result.Success(
            repo.toggleNotificationsStatus(
                FCMTokenRequest(FCMToken(enabled = params))
            )
        )
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
