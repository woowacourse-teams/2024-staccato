package com.on.staccato.presentation.notification.model

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.on.staccato.presentation.notification.model.StaccatoNotification.Companion.pendingIntentFlags

data class CommentCreatedNotification(
    override val channelType: ChannelType,
    override val title: String,
    override val body: String,
    val staccatoId: Long,
) : StaccatoNotification {
    override fun createIntent(context: Context): PendingIntent {
        val intent =
            Intent(
                Intent.ACTION_VIEW,
                "$STACCATO_URI$staccatoId".toUri(),
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        return PendingIntent.getActivity(context, 0, intent, pendingIntentFlags)
    }

    companion object {
        private const val STACCATO_URI = "staccato://staccato/"
    }
}
