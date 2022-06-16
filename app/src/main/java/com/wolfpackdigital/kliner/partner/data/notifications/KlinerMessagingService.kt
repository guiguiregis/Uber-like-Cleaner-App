package com.wolfpackdigital.kliner.partner.data.notifications

import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.wolfpackdigital.kliner.partner.data.models.CleanerStatus
import com.wolfpackdigital.kliner.partner.shared.useCases.SendFCMTokenUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.isAppInForeground
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class KlinerMessagingService : FirebaseMessagingService(), PersistenceService {

    private val sendFirebaseMessaging by inject<SendFCMTokenUseCase>()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val gson = Gson()
        val receivedJSON = gson.toJson(message.data)
        val receivedMessage = try {
            gson.fromJson(receivedJSON, NotificationModel::class.java)
        } catch (ex: JsonSyntaxException) {
            return
        }

        if (cleanerProfile?.status != CleanerStatus.ACTIVE) return

        if (applicationContext.isAppInForeground()) {
            // App is visible to the user, notify the activity with a broadcast
            sendBroadcast(Intent(Constants.FCM_NEW_MESSAGE).apply {
                putExtras(bundleOf(Constants.FCM_NOTIFICATION_MODEL to receivedMessage))
            })
        } else {
            // In this case, the app is in background and a pending intent
            // pointing to MainActivity will be linked to our new notification
            NotificationHelper.createNotificationFromPush(applicationContext, receivedMessage)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FBNOTIF", "token: $token")
        runBlocking {
            withContext(Dispatchers.Main) {
                sendFirebaseMessaging.executeNow(Unit)
            }
        }
    }

    companion object {
        suspend fun getDeviceToken(): String? {
            return getDeviceTokenWrapper<String> { completeListener ->
                FirebaseMessaging.getInstance().token.addOnCompleteListener(completeListener)
            }
        }

        suspend fun deleteDeviceToken(): Void {
            return getDeviceTokenWrapper { completeListener ->
                FirebaseMessaging.getInstance().deleteToken()
                    .addOnCompleteListener(completeListener)
            }
        }

        private suspend fun <T> getDeviceTokenWrapper(block: (OnCompleteListener<T>) -> Unit) =
            suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
                block(OnCompleteListener<T> {
                    cont.resume(it.result)
                })
            }
    }
}
