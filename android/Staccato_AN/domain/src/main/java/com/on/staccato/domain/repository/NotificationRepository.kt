package com.on.staccato.domain.repository

import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.model.notification.NotificationExistence

interface NotificationRepository {
    suspend fun registerCurrentFcmToken()

    suspend fun updateFcmToken(newToken: String)

    suspend fun getNotificationExistence(): ApiResult<NotificationExistence>
}
