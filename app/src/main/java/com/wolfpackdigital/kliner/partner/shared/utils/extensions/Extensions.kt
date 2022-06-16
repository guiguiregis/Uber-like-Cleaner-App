package com.wolfpackdigital.kliner.partner.shared.utils.extensions

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.wolfpackdigital.kliner.partner.BuildConfig
import com.wolfpackdigital.kliner.partner.shared.base.ApiError
import com.wolfpackdigital.kliner.partner.shared.useCases.GeneralOption
import java.util.regex.Pattern
import retrofit2.HttpException

private const val GENERIC_SERVER_ERROR = "Something went wrong, please try again."
private const val PARSING_SERVER_ERROR = "The response could not be parsed"
private const val SYNTAX_SERVER_ERROR = "The response doesn't have a valid format"

fun Context.getColorCompat(id: Int) = ContextCompat.getColor(this, id)

fun Context.getDrawableCompat(id: Int) = ContextCompat.getDrawable(this, id)

fun Throwable.getParsedError(): String =
    try {
        val response = (this as? HttpException)?.response()?.errorBody()
        val model = Gson().fromJson(response?.string(), ApiError::class.java)
        model?.errors?.firstOrNull() ?: model?.message ?: GENERIC_SERVER_ERROR
    } catch (ex: JsonParseException) {
        ex.localizedMessage ?: PARSING_SERVER_ERROR
    } catch (ex: JsonSyntaxException) {
        ex.localizedMessage ?: SYNTAX_SERVER_ERROR
    }

val appVersion: String
    get() = "v${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"

fun String.hasEmailPattern(): Boolean {
    val pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    return pattern.matcher(this).matches()
}

operator fun MutableLiveData<Int>.minusAssign(t: Int) {
    val current = this.value ?: 0
    this.value = current - t
}

operator fun MutableLiveData<Int>.plusAssign(t: Int) {
    val current = this.value ?: 0
    this.value = current + t
}

fun <T : Any, R : Any> whenAllNotNull(vararg options: T?, block: (List<T>) -> R) {
    if (options.all { it != null }) {
        block(options.filterNotNull()) // or do unsafe cast to non null collection
    }
}

fun <T : Any, R : Any> whenAnyNotNull(vararg options: T?, block: (List<T>) -> R) {
    if (options.any { it != null }) {
        block(options.filterNotNull())
    }
}

inline fun <T : Any> guardLet(vararg elements: T?, closure: () -> Nothing): List<T> {
    return if (elements.all { it != null }) {
        elements.filterNotNull()
    } else {
        closure()
    }
}

inline fun guard(vararg elements: Boolean, closure: () -> Nothing) {
    if (!elements.all { it }) {
        closure()
    }
}

fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

// Usage: condition then "yes" ?: "no"
infix fun <T> Boolean.then(param: T): T? = if (this) param else null

inline fun <reified T : Enum<T>> safeValueOf(type: String?): T? {
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        null
    } catch (e: NullPointerException) {
        null
    }
}

/**
 * This function is used to get the selection conditions of work types based on what service types
 * are selected.
 *
 * @return a list containing one boolean value for each work type of the selected service type,
 * if none is selected then return empty list.
 */
fun List<GeneralOption>.getServiceTypeConditioning(): MutableList<Boolean> {
    val serviceTypeConditioning: MutableList<Boolean> by lazy { mutableListOf() }
    /**
     * Filter and return a list containing only selected service types, if none is selected
     * then return empty list.
     */
    this.filter { item ->
        item.checked
    }.map { checkedServiceType ->
        /**
         * @property[checkedServiceType] the selected service type.
         * @property[workType] the work type of the selected service type.
         *
         * If the list of selection conditions is empty, add the selection condition
         * for each work type of the first selected service type
         *
         * If the list of selection conditions is NOT empty, compare the selection condition
         * for each work type of the selected service types with the already existing
         * selection conditions, and replace the old value with the result of OR operator
         */
        if (serviceTypeConditioning.isEmpty()) {
            checkedServiceType.workTypes?.map { workType ->
                serviceTypeConditioning.add(workType.checked)
            }
        } else {
            checkedServiceType.workTypes?.mapIndexed { index, workType ->
                serviceTypeConditioning[index] = workType.checked || serviceTypeConditioning[index]
            }
        }
    }
    return serviceTypeConditioning
}

/**
 * This function is used to apply the selection conditions to the list of work types
 *
 * @property[typesOfWork] the list of existing work types.
 */
fun MutableList<Boolean>.autoSelectWorkTypeByServiceTypeConditioning(
    typesOfWork: MutableLiveData<List<GeneralOption>>
) {
    /**
     * If the list of new selection conditions is NOT empty, compare the existing selection
     * for each work type with the new selection conditions, and:
     *      - replace the old value for the checked property with the result of OR operator
     *      - replace the old value for the enabled property with the negate value
     *      of the new selection condition
     *
     * If the list of new selection conditions is empty, then keep the existing selections and
     * make all work types selectable
     */
    if (this.isNotEmpty()) {
        typesOfWork.value = typesOfWork.value?.mapIndexed { index, workType ->
            workType.copy(
                checked = this[index] || (typesOfWork.value?.get(index)?.checked ?: false),
                enabled = !this[index]
            )
        }
    } else {
        typesOfWork.value = typesOfWork.value?.map { workType ->
            workType.copy(
                checked = workType.checked,
                enabled = true
            )
        }
    }
}

fun List<GeneralOption>.filterCheckedItems() = this.filter { item ->
    item.checked
}.map { item ->
    item.id
}

fun onOptionsClicked(
    option: GeneralOption,
    list: MutableLiveData<List<GeneralOption>>
) {
    val new = list.value?.map {
        if (it.id == option.id)
            option.copy(checked = !option.checked)
        else
            it
    }
    list.value = new
}
