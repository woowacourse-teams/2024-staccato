package com.on.staccato.data.notification

import com.on.staccato.data.dto.notification.FcmTokenRequest
import com.on.staccato.data.dto.notification.NotificationExistenceResponse
import com.on.staccato.data.network.ApiResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotificationApiService {
    @GET(NOTIFICATION_EXISTS_PATH)
    suspend fun getNotificationExistence(): ApiResult<NotificationExistenceResponse>

    @POST(NOTIFICATION_TOKENS_PATH)
    suspend fun postFcmToken(
        @Body fcmTokenRequest: FcmTokenRequest,
    ): ApiResult<Unit>

    companion object {
        private const val NOTIFICATION_PATH = "/notifications"
        private const val NOTIFICATION_EXISTS_PATH = "$NOTIFICATION_PATH/exists"
        private const val NOTIFICATION_TOKENS_PATH = "$NOTIFICATION_PATH/tokens"
    }
}
