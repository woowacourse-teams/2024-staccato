package com.on.staccato.presentation.common.notification.model

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.on.staccato.presentation.common.notification.ChannelType
import com.on.staccato.presentation.common.notification.NotificationType
import com.on.staccato.presentation.common.notification.NotificationType.ACCEPT_INVITATION
import com.on.staccato.presentation.common.notification.NotificationType.COMMENT_CREATED
import com.on.staccato.presentation.common.notification.NotificationType.RECEIVE_INVITATION
import com.on.staccato.presentation.common.notification.NotificationType.STACCATO_CREATED

sealed interface StaccatoNotification {
    val title: String
    val body: String
    val channelType: ChannelType

    fun createIntent(context: Context): PendingIntent

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_BODY = "body"
        private const val KEY_CATEGORY_ID = "categoryId"
        private const val KEY_STACCATO_ID = "staccatoId"
        val pendingIntentFlags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        fun from(
            type: NotificationType,
            data: Map<String, String>,
        ): StaccatoNotification? {
            val title = data[KEY_TITLE] ?: return null
            val body = data[KEY_BODY] ?: return null
            return when (type) {
                RECEIVE_INVITATION ->
                    ReceiveInvitationNotification(
                        channelType = type.channelType,
                        title = title,
                        body = body,
                    )

                ACCEPT_INVITATION ->
                    AcceptInvitationNotification(
                        channelType = type.channelType,
                        title = title,
                        body = body,
                        categoryId = data[KEY_CATEGORY_ID]?.toLongOrNull() ?: return null,
                    )

                COMMENT_CREATED ->
                    CommentCreatedNotification(
                        channelType = type.channelType,
                        title = title,
                        body = body,
                        staccatoId = data[KEY_STACCATO_ID]?.toLongOrNull() ?: return null,
                    )

                STACCATO_CREATED ->
                    StaccatoCreatedNotification(
                        channelType = type.channelType,
                        title = title,
                        body = body,
                        staccatoId = data[KEY_STACCATO_ID]?.toLongOrNull() ?: return null,
                    )
            }
        }
    }
}
