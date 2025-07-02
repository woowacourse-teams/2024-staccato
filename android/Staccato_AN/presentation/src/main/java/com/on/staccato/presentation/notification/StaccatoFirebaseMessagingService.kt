package com.on.staccato.presentation.notification

import android.content.Intent
import com.google.firebase.messaging.Constants.MessageNotificationKeys
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.on.staccato.domain.repository.NotificationRepository
import com.on.staccato.presentation.notification.model.NotificationType
import com.on.staccato.presentation.notification.model.StaccatoNotification
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
        val data = message.data
        if (data.isEmpty()) return
        val typeValue = data[KEY_TYPE] ?: return
        val type = NotificationType.from(typeValue) ?: return
        val staccatoNotification = StaccatoNotification.from(type, data) ?: return
        staccatoNotificationManager.notify(staccatoNotification)
    }

    override fun onDestroy() {
        _job?.cancel()
        super.onDestroy()
    }

    companion object {
        private const val KEY_TYPE = "type"
        private const val ENABLE_NOTIFICATION = "gcm.notification.e"
    }
}
