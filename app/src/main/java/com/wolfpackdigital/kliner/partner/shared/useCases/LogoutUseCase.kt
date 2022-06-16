package com.wolfpackdigital.kliner.partner.shared.useCases

import com.wolfpackdigital.kliner.partner.data.repo.AuthRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class LogoutUseCase(private val repo: AuthRepoI) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) = try {
        val response = repo.logout()
        Result.Success(response)
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}
