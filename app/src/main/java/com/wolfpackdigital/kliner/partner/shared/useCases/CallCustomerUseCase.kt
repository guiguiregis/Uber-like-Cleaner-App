package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class CallCustomerUseCase(private val repo: MainRepoI) : BaseUseCase<Int, Any>() {
    override suspend fun run(params: Int) = try {
        Result.Success(repo.callCustomer(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}