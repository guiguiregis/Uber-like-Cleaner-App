package com.wolfpackdigital.kliner.partner.data.notifications

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class NotificationModel(
    var title: String?,
    val body: String?,
    val action: PushNotificationAction?,
    @SerializedName("resource_name")
    val resourceName: String?,
    @SerializedName("resource_id")
    val resourceId: Int?
) : Parcelable

@Keep
enum class PushNotificationAction {
    @SerializedName("activate_cleaner_profile")
    ACTIVATE_CLEANER_PROFILE,

    @SerializedName("client_cancelled_mission")
    CLIENT_CANCELLED_MISSION,

    @SerializedName("mission_ends_soon")
    MISSION_ENDS_SOON,

    @SerializedName("new_mission_offer")
    NEW_MISSION_OFFER
}
