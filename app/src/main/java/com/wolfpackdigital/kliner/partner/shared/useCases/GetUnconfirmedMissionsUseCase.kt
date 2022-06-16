package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class GetUnconfirmedMissionsUseCase(
    private val repo: MainRepoI
) : BaseUseCase<Unit, List<Mission>>() {
    override suspend fun run(params: Unit) = try {
        Result.Success(repo.getUnconfirmedMissions())
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
