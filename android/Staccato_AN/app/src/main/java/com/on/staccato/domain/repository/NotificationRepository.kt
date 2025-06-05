package com.on.staccato.domain.repository

import com.on.staccato.data.network.ApiResult
import com.on.staccato.domain.model.NotificationExistence

interface NotificationRepository {
    suspend fun getNotificationExistence(): ApiResult<NotificationExistence>
}
