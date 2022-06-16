package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class UpdateEmployeeProfileUseCase(
    private val repo: MainRepoI
) : BaseUseCase<CleanerProfile, CleanerProfile>() {
    override suspend fun run(params: CleanerProfile) = try {
        Result.Success(repo.updateEmployeeProfile(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
