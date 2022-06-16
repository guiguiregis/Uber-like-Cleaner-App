package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class GetMoreSectionItemsUseCase : BaseUseCase<Unit, List<MoreSectionItem>>(), PersistenceService {
    @Suppress("LongMethod")
    override suspend fun run(params: Unit) = try {
        val sections = mapOf(
            Type.Employees to MoreSectionItem(
                R.string.more_section_employees,
                R.drawable.ic_broom,
                Type.Employees
            ),
            Type.UnassignedMissions to MoreSectionItem(
                R.string.more_section_unassigned_missions,
                R.drawable.ic_unassigned_missions,
                Type.UnassignedMissions
            ),
            Type.Activity to MoreSectionItem(
                R.string.more_section_activity,
                R.drawable.ic_activity,
                Type.Activity
            ),
            Type.Help to MoreSectionItem(
                R.string.more_section_help,
                R.drawable.ic_help,
                Type.Help
            ),
            Type.About to MoreSectionItem(
                R.string.more_section_about,
                R.drawable.ic_about,
                Type.About
            )
        )
        val types = when (cleanerProfile?.kind) {
            Kind.EMPLOYER -> listOf(
                Type.Employees,
                Type.UnassignedMissions,
                Type.Activity,
                Type.Help,
                Type.About
            )
            Kind.INDEPENDENT -> listOf(
                Type.Activity,
                Type.Help,
                Type.About
            )
            Kind.EMPLOYEE -> listOf(
                Type.Activity,
                Type.Help,
                Type.About
            )
            else -> listOf()
        }
        Result.Success(types.mapNotNull { sections[it] })
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
enum class Type {
    Employees,
    UnassignedMissions,
    Activity,
    Help,
    About
}

@Keep
data class MoreSectionItem(
    val title: Int? = null,
    val icon: Int? = null,
    val type: Type? = null,
    @Transient
    val onClick: (MoreSectionItem) -> Unit = {}
) {
    fun onSelect() = onClick(this)
}
