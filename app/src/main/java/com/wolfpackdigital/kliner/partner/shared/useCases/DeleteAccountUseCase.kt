package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.AuthRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class DeleteAccountUseCase(private val repo: AuthRepoI) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) = try {
        Result.Success(repo.logout())
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
