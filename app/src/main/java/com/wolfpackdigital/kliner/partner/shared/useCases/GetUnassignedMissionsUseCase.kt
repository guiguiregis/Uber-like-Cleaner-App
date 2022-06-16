package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class GetUnassignedMissionsUseCase(
    private val repo: MainRepoI
) : BaseUseCase<Unit, List<Mission>>() {
    override suspend fun run(params: Unit) = try {
        Result.Success(repo.getUnassignedMissions())
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
