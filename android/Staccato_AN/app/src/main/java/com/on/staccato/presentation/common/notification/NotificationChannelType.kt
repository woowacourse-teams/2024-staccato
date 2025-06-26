package com.on.staccato.presentation.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import com.on.staccato.R

enum class NotificationChannelType(
    val id: String,
    @StringRes val nameStringRes: Int,
    val importance: Int,
) {
    Invitation(
        id = "invitation",
        nameStringRes = R.string.channel_name_invitation,
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    Category(
        id = "category",
        nameStringRes = R.string.channel_name_category,
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    Comment(
        id = "comment",
        nameStringRes = R.string.channel_name_comment,
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    ;

    companion object {
        fun toChannel(
            context: Context,
            title: String,
        ): NotificationChannel {
            return when {
                title.contains("님이 초대를 보냈어요") -> Invitation.toNotificationChannel(context)
                title.contains("님이 참여했어요") -> Category.toNotificationChannel(context)
                title.contains("스타카토가 추가됐어요") -> Category.toNotificationChannel(context)
                title.contains("님의 코멘트") -> Comment.toNotificationChannel(context)
                else -> Category.toNotificationChannel(context)
            }
        }

        private fun NotificationChannelType.toNotificationChannel(context: Context) = NotificationChannel(id, name, importance)
    }
}
