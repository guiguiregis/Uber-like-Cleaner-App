package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class GetTextUseCase(
    private val repo: MainRepoI
) : BaseUseCase<TextType, TextResponse>() {
    override suspend fun run(params: TextType) = try {
        Result.Success(repo.getText(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
enum class TextType(private val value: String) {
    ONBOARDING_CONTRACT("onboarding_contract"),

    TERMS_AND_CONDITIONS("terms_and_conditions"),

    COVID_19("covid_19"),

    HOME_CLEANING_DESCRIPTION("home_cleaning_description"),

    OFFICE_CLEANING_DESCRIPTION("office_cleaning_description");

    override fun toString() = this.value
}

@Keep
data class TextResponse(
    val text: String
)
