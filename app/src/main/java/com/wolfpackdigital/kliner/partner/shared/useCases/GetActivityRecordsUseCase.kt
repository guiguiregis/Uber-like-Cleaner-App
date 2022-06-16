package com.wolfpackdigital.kliner.partner.shared.useCases

import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class GetActivityRecordsUseCase(
    private val repo: MainRepoI
) : BaseUseCase<Unit, ActivityRecordsResponse>() {
    override suspend fun run(params: Unit) = try {
        Result.Success(repo.getActivityRecords())
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

data class ActivityRecordsResponse(
    val id: Int? = null,
    @SerializedName("total_missions")
    val totalMissions: Number? = null,
    @SerializedName("total_duration")
    val totalDuration: Number? = null,
    @SerializedName("total_revenue")
    val totalRevenue: Number? = null
)
