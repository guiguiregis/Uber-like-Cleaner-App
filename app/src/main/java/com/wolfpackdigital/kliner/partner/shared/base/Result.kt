package com.wolfpackdigital.kliner.partner.shared.base

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
@Keep
sealed class Result<out R> {

    @Keep
    data class Success<out T>(val data: T) : Result<T>()
    @Keep
    data class Error(val error: String) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$error]"
        }
    }
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Result.Success && data != null

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}

@Keep
@Parcelize
data class ApiError(
    val message: String = "",
    val code: String = "",
    val errors: List<String>?
) : Parcelable
