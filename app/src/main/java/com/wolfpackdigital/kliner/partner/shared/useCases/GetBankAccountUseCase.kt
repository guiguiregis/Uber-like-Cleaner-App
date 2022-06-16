package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class GetBankAccountUseCase(
    private val repo: MainRepoI
) : BaseUseCase<Unit, BankAccount>() {
    override suspend fun run(params: Unit) = try {
        Result.Success(repo.getBankAccount())
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
