package com.on.staccato.presentation.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager

enum class NotificationChannelType(
    val id: String,
    val importance: Int,
) {
    Invitation(
        id = "invitation",
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    Category(
        id = "category",
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    Staccato(
        id = "staccato",
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    Comment(
        id = "comment",
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    ;

    companion object {
        private fun NotificationChannelType.toNotificationChannel() = NotificationChannel(id, name, importance)

        fun toChannel(title: String): NotificationChannel {
            return when {
                title.contains("님이 초대를 보냈어요") -> Invitation.toNotificationChannel()
                title.contains("님이 참여했어요") -> Category.toNotificationChannel()
                title.contains("스타카토가 추가됐어요") -> Staccato.toNotificationChannel()
                title.contains("님의 코멘트") -> Comment.toNotificationChannel()
                else -> Staccato.toNotificationChannel()
            }
        }
    }
}
