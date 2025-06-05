package com.on.staccato.data.notification

import com.on.staccato.data.dto.notification.NotificationExistenceResponse
import com.on.staccato.data.network.ApiResult
import retrofit2.http.GET

interface NotificationApiService {
    @GET(NOTIFICATION_EXISTS_PATH)
    suspend fun getNotificationExistence(): ApiResult<NotificationExistenceResponse>

    companion object {
        private const val NOTIFICATION_PATH = "/notifications"
        private const val NOTIFICATION_EXISTS_PATH = "$NOTIFICATION_PATH/exists"
    }
}
