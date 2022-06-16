package com.wolfpackdigital.kliner.partner.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Token(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresIn: Double,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("created_at")
    val createdAt: Double
) : Parcelable
