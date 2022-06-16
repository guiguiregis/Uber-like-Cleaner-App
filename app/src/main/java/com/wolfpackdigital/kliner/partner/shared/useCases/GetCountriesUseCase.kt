package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.AuthRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class GetCountriesUseCase(private val repo: AuthRepoI) : BaseUseCase<Unit, List<ItemCountry>>() {
    override suspend fun run(params: Unit): Result<List<ItemCountry>> = try {
        Result.Success(repo.getCountryCodes())
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class ItemCountry(
    val name: String,
    @SerializedName("phone_country_code")
    val countryCode: String,
    val flag: String,
    var locationSelected: Boolean = false,
    val onSelectCallback: (ItemCountry) -> Unit = {}
) {
    fun onSelect() {
        onSelectCallback(this)
    }
}
