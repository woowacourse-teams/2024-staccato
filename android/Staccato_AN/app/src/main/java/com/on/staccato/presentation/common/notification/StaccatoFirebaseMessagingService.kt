package com.on.staccato.presentation.common.notification

import android.content.Intent
import com.google.firebase.messaging.Constants.MessageNotificationKeys
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.on.staccato.domain.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StaccatoFirebaseMessagingService :
    FirebaseMessagingService() {
    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var staccatoNotificationManager: StaccatoNotificationManager

    private var _job: Job? = null
    private val job get() = requireNotNull(_job)

    override fun onNewToken(token: String) {
        _job = SupervisorJob()
        CoroutineScope(job).launch {
            notificationRepository.updateFcmToken(token)
        }
    }

    override fun handleIntent(intent: Intent?) {
        val bundleWithoutNotification =
            intent?.extras?.apply {
                remove(MessageNotificationKeys.ENABLE_NOTIFICATION)
                remove(ENABLE_NOTIFICATION)
            }
        intent?.replaceExtras(bundleWithoutNotification)
        super.handleIntent(intent)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title
        val body = message.notification?.body
        if (title.isNullOrBlank() || body.isNullOrBlank()) return

        staccatoNotificationManager.notify(title = title, body = body)
    }

    override fun onDestroy() {
        _job?.cancel()
        super.onDestroy()
    }

    companion object {
        const val ENABLE_NOTIFICATION = "gcm.notification.e"
    }
}
