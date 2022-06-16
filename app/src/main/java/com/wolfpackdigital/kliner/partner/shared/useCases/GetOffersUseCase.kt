package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class GetOffersUseCase(private val repo: MainRepoI) : BaseUseCase<Unit, List<Mission>>() {
    override suspend fun run(params: Unit) = try {
        Result.Success(repo.getOffers().sortedBy { it.date })
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class WeekDayItem(
    val date: String? = null,
    val hour: String? = null,
    val shortDay: String? = null,
    val longDay: String? = null,
    var isSelected: Boolean? = null
)
