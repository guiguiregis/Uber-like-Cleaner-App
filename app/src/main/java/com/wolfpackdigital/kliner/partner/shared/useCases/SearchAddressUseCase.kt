package com.wolfpackdigital.kliner.partner.shared.useCases

import android.content.Context
import androidx.annotation.Keep
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError

class SearchAddressUseCase(
    private val context: Context
) : BaseUseCase<SearchRequest, List<AddressItem>>() {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun run(params: SearchRequest): Result<List<AddressItem>> = try {
        Places.initialize(context, context.getString(R.string.places_api))
        val client = Places.createClient(context)

        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setTypeFilter(params.addressType.typeFilter)
            .setCountry(params.country)
            .setSessionToken(token)
            .setQuery(params.query)
            .build()

        val predictions = Tasks.await(client.findAutocompletePredictions(request))
        Result.Success(predictions.autocompletePredictions.mapNotNull {
            if (it.getSecondaryText(null).isNotEmpty())
                AddressItem(
                    null,
                    it.getPrimaryText(null).toString(),
                    it.placeId,
                    it.getFullText(null).toString()
                )
            else null
        })
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
data class SearchRequest(
    val query: String,
    val addressType: AddressType,
    val country: String? = Constants.DEFAULT_COUNTRY_CODE
)

enum class AddressType(val typeFilter: TypeFilter) {
    ADDRESS(TypeFilter.ADDRESS),
    REGIONS(TypeFilter.REGIONS)
}

@Keep
data class AddressItem(
    val id: Int? = null,
    val city: String? = null,
    @SerializedName("place_id")
    val placeID: String?,
    @SerializedName("street_line_one")
    val streetLine: String?,
    @SerializedName("_destroy")
    var destroy: Int = 0,
    var default: Boolean = false,
    @Transient
    val onDeleteCallback: (AddressItem) -> Unit = {}
) {

    fun onDelete() {
        onDeleteCallback(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        val addressItem = other as? AddressItem

        if (id != addressItem?.id) return false
        if (city != addressItem?.city) return false
        if (streetLine != addressItem?.streetLine) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + city.hashCode()
        result = 31 * result + placeID.hashCode()
        result = 31 * result + streetLine.hashCode()
        return result
    }
}

fun List<AddressItem>.equalItems(items: List<AddressItem>?): Boolean {
    items ?: return false
    if (size != items.size) return false

    var result = true
    forEachIndexed { index, addressItem ->
        if (addressItem != items[index]) result = false
    }
    return result
}
