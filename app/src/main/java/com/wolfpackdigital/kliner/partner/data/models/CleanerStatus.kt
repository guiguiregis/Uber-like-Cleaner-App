package com.wolfpackdigital.kliner.partner.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
enum class CleanerStatus {
    @SerializedName("inactive")
    INACTIVE,

    @SerializedName("incomplete")
    INCOMPLETE,

    @SerializedName("active")
    ACTIVE,

    @SerializedName("blocked")
    BLOCKED
}
