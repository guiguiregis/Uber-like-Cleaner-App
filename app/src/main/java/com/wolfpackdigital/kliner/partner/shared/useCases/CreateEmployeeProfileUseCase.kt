package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class CreateEmployeeProfileUseCase(
    private val repo: MainRepoI
) : BaseUseCase<CreateEmployeeProfile, CleanerProfile>() {
    override suspend fun run(params: CreateEmployeeProfile) = try {
        Result.Success(repo.createEmployeeProfile(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class CreateEmployeeProfile(
    val request: CreateEmployeeProfileRequest,
    val companyId: Int
)

@Keep
data class CreateEmployeeProfileRequest(
    @SerializedName("employee")
    val employee: CleanerProfile
)
