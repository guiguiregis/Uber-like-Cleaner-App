package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class GetDashboardTipsUseCase : BaseUseCase<Unit, List<TipItem>>() {
    override suspend fun run(params: Unit) = try {
        val titles = listOf(
            R.string.rule_1_title,
            R.string.rule_2_title,
            R.string.rule_3_title,
            R.string.rule_4_title,
            R.string.rule_5_title
        )
        val descriptions = listOf(
            R.string.rule_1_description,
            R.string.rule_2_description,
            R.string.rule_3_description,
            R.string.rule_4_description,
            R.string.rule_5_description
        )
        Result.Success((0..4).map { TipItem(it, titles[it], descriptions[it]) })
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class TipItem(
    val id: Int? = null,
    val title: Int? = null,
    val description: Int? = null
)
