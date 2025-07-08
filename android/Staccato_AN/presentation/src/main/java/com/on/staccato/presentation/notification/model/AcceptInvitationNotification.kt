package com.on.staccato.presentation.notification.model

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.on.staccato.presentation.notification.model.StaccatoNotification.Companion.pendingIntentFlags

data class AcceptInvitationNotification(
    override val channelType: ChannelType,
    override val title: String,
    override val body: String,
    val categoryId: Long,
) : StaccatoNotification {
    override fun createIntent(context: Context): PendingIntent {
        val deepLinkUri = "$CATEGORY_URI$categoryId".toUri()
        val intent =
            Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        return PendingIntent.getActivity(context, 0, intent, pendingIntentFlags)
    }

    companion object {
        private const val CATEGORY_URI = "staccato://category/"
    }
}
