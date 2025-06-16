package com.on.staccato.data.notification

import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.notification.FcmTokenRequest
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.handle
import com.on.staccato.domain.model.notification.NotificationExistence
import com.on.staccato.domain.repository.NotificationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationDefaultRepository
    @Inject
    constructor(
        private val notificationApiService: NotificationApiService,
    ) : NotificationRepository {
        override suspend fun getNotificationExistence(): ApiResult<NotificationExistence> =
            notificationApiService.getNotificationExistence().handle { it.toDomain() }

        override suspend fun registerCurrentFcmToken() =
            coroutineScope {
                val (token, deviceId) =
                    listOf(
                        async { getFcmToken() },
                        async { getDeviceId() },
                    ).awaitAll()
                postFcmTokenIfValid(token, deviceId)
            }

        override suspend fun updateNewFcmToken(newToken: String) {
            getDeviceId()?.let { deviceId ->
                postFcmTokenIfValid(newToken, deviceId)
            }
        }

        private suspend fun postFcmTokenIfValid(
            token: String?,
            deviceId: String?,
        ) {
            if (!token.isNullOrBlank() && !deviceId.isNullOrBlank()) {
                notificationApiService.postFcmToken(
                    FcmTokenRequest(token = token, deviceId = deviceId),
                )
            }
        }

        private suspend fun getFcmToken(): String? = runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull()

        private suspend fun getDeviceId(): String? = runCatching { FirebaseInstallations.getInstance().id.await() }.getOrNull()
    }
