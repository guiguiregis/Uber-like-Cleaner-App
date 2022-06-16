package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class AssignEmployeeUseCase(
    private val repo: MainRepoI
) : BaseUseCase<UpdateMissionRequest, Mission>() {
    override suspend fun run(params: UpdateMissionRequest) = try {
        Result.Success(repo.assignEmployeeToMission(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class UpdateMissionRequest(
    val missionId: Int,
    val cleanerProfileId: Int,
    val assignmentScope: AssignmentScope? = null
)

@Keep
data class AssignEmployeeRequest(val mission: MissionBody)

@Keep
data class MissionBody(
    @SerializedName("cleaner_profile_id")
    val cleanerProfileId: Int,
    @SerializedName("assignment_scope")
    val assignmentScope: AssignmentScope? = null
)

@Keep
enum class AssignmentScope {
    @SerializedName("single")
    SINGLE,

    @SerializedName("multiple")
    MULTIPLE
}
