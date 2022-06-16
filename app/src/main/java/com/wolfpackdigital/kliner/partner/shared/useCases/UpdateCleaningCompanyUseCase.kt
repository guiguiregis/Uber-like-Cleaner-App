package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class UpdateCleaningCompanyUseCase(
    private val repo: MainRepoI
) : BaseUseCase<CleaningCompany, CleaningCompany>() {
    override suspend fun run(params: CleaningCompany) = try {
        Result.Success(repo.updateCleaningCompany(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
