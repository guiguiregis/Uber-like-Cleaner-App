package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class NotificationsSettingsUseCase(
    private val repo: MainRepoI
) : BaseUseCase<Boolean, Unit>() {
    override suspend fun run(params: Boolean) = try {
        Result.Success(repo.toggleNotificationSettings(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
