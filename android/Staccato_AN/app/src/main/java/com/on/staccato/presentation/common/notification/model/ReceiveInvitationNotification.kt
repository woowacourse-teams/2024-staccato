package com.on.staccato.presentation.common.notification.model

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import com.on.staccato.presentation.common.notification.ChannelType
import com.on.staccato.presentation.invitation.InvitationManagementActivity
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.mypage.MyPageActivity

data class ReceiveInvitationNotification(
    override val channelType: ChannelType,
    override val title: String,
    override val body: String,
) : StaccatoNotification {
    override fun createIntent(context: Context): PendingIntent {
        val mainIntent = Intent(context, MainActivity::class.java)
        val myPageIntent = Intent(context, MyPageActivity::class.java)
        val invitationIntent = Intent(context, InvitationManagementActivity::class.java)

        val pendingIntent =
            TaskStackBuilder.create(context).apply {
                addNextIntent(mainIntent)
                addNextIntent(myPageIntent)
                addNextIntent(invitationIntent)
            }.getPendingIntent(0, StaccatoNotification.pendingIntentFlags)
        return pendingIntent
    }
}
