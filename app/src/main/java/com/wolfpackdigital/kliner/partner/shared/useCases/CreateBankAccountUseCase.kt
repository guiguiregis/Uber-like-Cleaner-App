package com.wolfpackdigital.kliner.partner.shared.useCases

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError
import kotlinx.parcelize.Parcelize

class CreateBankAccountUseCase(
    private val repo: MainRepoI
) : BaseUseCase<BankAccount, BankAccount>() {
    override suspend fun run(params: BankAccount) = try {
        Result.Success(repo.createBankAccount(params))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class BankAccountRequest(
    @SerializedName("bank_account")
    val bankAccount: BankAccount? = null
)

@Keep
@Parcelize
data class BankAccount(
    val id: Int? = null,
    @SerializedName("account_holder_name")
    val accountHolderName: String? = null,
    @SerializedName("account_number")
    val iban: String? = null,
    @SerializedName("payment_periodicity")
    val paymentPeriodicity: String? = null
) : Parcelable
