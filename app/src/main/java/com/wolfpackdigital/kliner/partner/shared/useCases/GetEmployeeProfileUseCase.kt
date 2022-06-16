package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class GetEmployeeProfileUseCase(
    private val repo: MainRepoI
) : BaseUseCase<Int, CleanerProfile>(), PersistenceService {
    override suspend fun run(params: Int) = try {
        Result.Success(repo.getEmployeeProfile(companyID ?: -1, params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
