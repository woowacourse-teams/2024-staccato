package com.on.staccato.presentation.notification.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import com.on.staccato.presentation.R

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
        fun getAllChannels(context: Context): List<NotificationChannel> = ChannelType.entries.map { it.toChannel(context) }

        fun ChannelType.toChannel(context: Context) = NotificationChannel(id, context.getString(nameStringRes), importance)
    }
}
