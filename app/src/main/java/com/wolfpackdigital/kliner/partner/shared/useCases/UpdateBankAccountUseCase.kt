package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class UpdateBankAccountUseCase(
    private val repo: MainRepoI
) : BaseUseCase<BankAccount, BankAccount>() {
    override suspend fun run(params: BankAccount) = try {
        Result.Success(repo.updateBankAccount(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
