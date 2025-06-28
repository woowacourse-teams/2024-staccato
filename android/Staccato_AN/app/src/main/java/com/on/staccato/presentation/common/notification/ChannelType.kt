package com.on.staccato.presentation.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import com.on.staccato.R

enum class ChannelType(
    val id: String,
    @StringRes val nameStringRes: Int,
    val importance: Int,
) {
    INVITATION(
        id = "invitation",
        nameStringRes = R.string.channel_name_invitation,
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    CATEGORY(
        id = "category",
        nameStringRes = R.string.channel_name_category,
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    COMMENT(
        id = "comment",
        nameStringRes = R.string.channel_name_comment,
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    ;

    companion object {
        fun getAllChannels(context: Context): List<NotificationChannel> = NotificationChannelType.entries.map { it.toChannel(context) }

        fun toChannel(
            context: Context,
            title: String,
        ): NotificationChannel =
            when {
                title.contains("님이 초대를 보냈어요") -> INVITATION.toChannel(context)
                title.contains("님이 참여했어요") -> CATEGORY.toChannel(context)
                title.contains("스타카토가 추가됐어요") -> CATEGORY.toChannel(context)
                title.contains("님의 코멘트") -> COMMENT.toChannel(context)
                else -> CATEGORY.toChannel(context)
            }

        private fun NotificationChannelType.toChannel(context: Context) =
            NotificationChannel(id, context.getString(nameStringRes), importance)
    }
}
