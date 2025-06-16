package com.on.staccato.domain.repository

import com.on.staccato.data.network.ApiResult
import com.on.staccato.domain.model.notification.NotificationExistence

interface NotificationRepository {
    suspend fun registerCurrentFcmToken()

    suspend fun updateNewFcmToken(newToken: String)

    suspend fun getNotificationExistence(): ApiResult<NotificationExistence>
}
