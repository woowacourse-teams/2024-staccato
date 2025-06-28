package com.on.staccato.presentation.common.notification

import com.on.staccato.presentation.common.notification.NotificationType.ACCEPT_INVITATION
import com.on.staccato.presentation.common.notification.NotificationType.COMMENT_CREATED
import com.on.staccato.presentation.common.notification.NotificationType.RECEIVE_INVITATION
import com.on.staccato.presentation.common.notification.NotificationType.STACCATO_CREATED

sealed interface StaccatoNotification {
    val title: String
    val body: String
    val channelType: ChannelType

    data class ReceiveInvitation(
        override val channelType: ChannelType,
        override val title: String,
        override val body: String,
    ) : StaccatoNotification

    data class AcceptInvitation(
        override val channelType: ChannelType,
        override val title: String,
        override val body: String,
        val categoryId: Long,
    ) : StaccatoNotification

    data class StaccatoCreated(
        override val channelType: ChannelType,
        override val title: String,
        override val body: String,
        val staccatoId: Long,
    ) : StaccatoNotification

    data class CommentCreated(
        override val channelType: ChannelType,
        override val title: String,
        override val body: String,
        val staccatoId: Long,
    ) : StaccatoNotification

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_BODY = "body"
        private const val KEY_CATEGORY_ID = "categoryId"
        private const val KEY_STACCATO_ID = "staccatoId"

        fun from(
            type: NotificationType,
            data: Map<String, String>,
        ): StaccatoNotification? {
            val title = data[KEY_TITLE] ?: return null
            val body = data[KEY_BODY] ?: return null
            return when (type) {
                RECEIVE_INVITATION ->
                    ReceiveInvitation(
                        channelType = type.channelType,
                        title = title,
                        body = body,
                    )

                ACCEPT_INVITATION ->
                    AcceptInvitation(
                        channelType = type.channelType,
                        title = title,
                        body = body,
                        categoryId = data[KEY_CATEGORY_ID]?.toLongOrNull() ?: return null,
                    )

                COMMENT_CREATED ->
                    CommentCreated(
                        channelType = type.channelType,
                        title = title,
                        body = body,
                        staccatoId = data[KEY_STACCATO_ID]?.toLongOrNull() ?: return null,
                    )

                STACCATO_CREATED ->
                    StaccatoCreated(
                        channelType = type.channelType,
                        title = title,
                        body = body,
                        staccatoId = data[KEY_STACCATO_ID]?.toLongOrNull() ?: return null,
                    )
            }
        }
    }
}
