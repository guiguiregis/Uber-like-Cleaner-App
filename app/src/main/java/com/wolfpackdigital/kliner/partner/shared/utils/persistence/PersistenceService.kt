package com.wolfpackdigital.kliner.partner.shared.utils.persistence

import com.orhanobut.hawk.Hawk
import com.wolfpackdigital.kliner.partner.data.models.Token
import com.wolfpackdigital.kliner.partner.data.notifications.KlinerMessagingService
import com.wolfpackdigital.kliner.partner.shared.useCases.CleanerProfile
import com.wolfpackdigital.kliner.partner.shared.utils.Constants.MIN_DAYS_LEFT_TO_UPLOAD_CERTIFICATE

interface PersistenceService {

    var token: Token?
        get() = Hawk.get(HawkKeys.TOKEN_KEY)
        set(token) {
            Hawk.put(HawkKeys.TOKEN_KEY, token)
        }

    var cleanerProfile: CleanerProfile?
        get() = Hawk.get(HawkKeys.CLEANER_PROFILE)
        set(profile) {
            Hawk.put(HawkKeys.CLEANER_PROFILE, profile)
        }

    var companyID: Int?
        get() = Hawk.get(HawkKeys.COMPANY_ID)
        set(id) {
            Hawk.put(HawkKeys.COMPANY_ID, id)
        }

    var contractSignedAt: String?
        get() = Hawk.get(HawkKeys.CONTRACT_SIGNED_AT)
        set(contract) {
            Hawk.put(HawkKeys.CONTRACT_SIGNED_AT, contract)
        }

    var lastKnownLeftDaysToUploadCertificate: Int
        get() = Hawk.get(
            HawkKeys.LAST_KNOWN_LEFT_DAYS_TO_UPLOAD_CERTIFICATE,
            MIN_DAYS_LEFT_TO_UPLOAD_CERTIFICATE
        )
        set(days) {
            Hawk.put(HawkKeys.LAST_KNOWN_LEFT_DAYS_TO_UPLOAD_CERTIFICATE, days)
        }

    var showOnboarding: Boolean
        get() = Hawk.get(HawkKeys.ONBOARDING_SHOWN, true)
        set(value) {
            Hawk.put(HawkKeys.ONBOARDING_SHOWN, value)
        }

    var showNotificationSetting: Boolean
        get() = Hawk.get(HawkKeys.SHOW_GEOLOCATION_SETTING, false)
        set(value) {
            Hawk.put(HawkKeys.SHOW_GEOLOCATION_SETTING, value)
        }

    var showGeolocationSetting: Boolean
        get() = Hawk.get(HawkKeys.SHOW_NOTIFICATION_SETTING, false)
        set(value) {
            Hawk.put(HawkKeys.SHOW_NOTIFICATION_SETTING, value)
        }

    suspend fun deleteAllUserData() {
        KlinerMessagingService.deleteDeviceToken()
        listOf(
            HawkKeys.TOKEN_KEY,
            HawkKeys.CLEANER_PROFILE,
            HawkKeys.COMPANY_ID,
            HawkKeys.CONTRACT_SIGNED_AT
        ).forEach {
            Hawk.delete(it)
        }
    }
}
