package com.wolfpackdigital.kliner.partner.shared.utils

object Constants {

    // Date formats
    private const val WEEKDAY = "EEEE"
    const val DAY_MONTH_SPACE_FORMAT = "dd MMM"
    const val DAY_MONTH_DOT_FORMAT = "dd.MM"
    const val FULL_DATE_SHORT_FORMAT = "dd.MM.yy"
    const val WEEKDAY_MONTH_FORMAT = "$WEEKDAY $DAY_MONTH_SPACE_FORMAT"
    const val PRICE_FORMAT = "#,###.##"

    // Notifications
    const val PUSH_NOTIFICATION_CHANNEL_ID = "kliner_android_notification_channel"
    const val FCM_NEW_MESSAGE = "fcm_new_message"
    const val FCM_NOTIFICATION_MODEL = "fcm_notification_model"

    // Others
    const val REGISTER_MINIMUM_YEARS = 18L
    const val NOT_A_NR = "N/A"
    const val SEARCH_DEBOUNCE = 750L
    const val UPDATE_DEBOUNCE = 750L
    const val DEFAULT_PHONE_PREFIX = "+33"
    const val DEFAULT_COUNTRY_CODE = "FR"
    const val MAX_DAYS_LEFT_TO_UPLOAD_CERTIFICATE = 90
    const val MIN_DAYS_LEFT_TO_UPLOAD_CERTIFICATE = 0

    // SavedStateHandlers keys
    const val REFRESH_EMPLOYEES = "refresh_employees"
    const val REFRESH_MISSION = "refresh_mission"
    const val SHOW_MESSAGE = "show_message"

    // Planning - Calendar
    const val DOT_RADIUS = 11f
}
