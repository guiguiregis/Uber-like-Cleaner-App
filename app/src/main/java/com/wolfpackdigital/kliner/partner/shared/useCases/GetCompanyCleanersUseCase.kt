package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class GetCompanyCleanersUseCase(
    private val repo: MainRepoI
) : BaseUseCase<CompanyCleanersRequest, List<CleanerProfile>>(), PersistenceService {
    override suspend fun run(params: CompanyCleanersRequest) = try {
        Result.Success(repo.getCompanyCleaners(params.companyID, params.missionID))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

data class CompanyCleanersRequest(
    val companyID: Int,
    val missionID: Int? = null
)
