package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class ConfirmMissionUseCase(
    private val repo: MainRepoI
) : BaseUseCase<ConfirmMissionRequest, Mission>() {
    override suspend fun run(params: ConfirmMissionRequest) = try {
        Result.Success(repo.confirmMission(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class ConfirmMissionRequest(
    val missionId: Int,
    val confirm: Boolean
)

@Keep
data class ConfirmMissionBody(
    val confirm: Boolean
)
