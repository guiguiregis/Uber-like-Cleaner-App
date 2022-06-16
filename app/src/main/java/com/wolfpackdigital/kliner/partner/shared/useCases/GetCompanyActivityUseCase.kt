package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

@Suppress("MagicNumber")
class GetCompanyActivityUseCase(private val repo: MainRepoI) :
    BaseUseCase<Unit, List<TransactionItem>>() {
    override suspend fun run(params: Unit) = try {
        Result.Success(repo.getCompanyActivity())
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
enum class ActivityInfoType {
    Missions,
    Revenue,
    Hours
}

@Keep
data class TransactionItem(
    val id: Int? = null,
    val description: String? = null,
    @SerializedName("created_at")
    val transferDate: String? = null,
    @SerializedName("period_start")
    val periodDateFrom: String? = null,
    @SerializedName("period_end")
    val periodDateTo: String? = null,
    val amount: String? = null,
    @Transient
    val onClick: (TransactionItem) -> Unit = {}
) {
    fun onSelect() = onClick(this)
}
