package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class CancelMissionUseCase(
    private val repo: MainRepoI
) : BaseUseCase<Int, Mission>() {
    override suspend fun run(params: Int) = try {
        Result.Success(repo.cancelMission(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
