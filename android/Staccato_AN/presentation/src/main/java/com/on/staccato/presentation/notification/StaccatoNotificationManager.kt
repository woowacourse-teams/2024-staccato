package com.on.staccato.presentation.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.on.staccato.presentation.R
import com.on.staccato.presentation.notification.model.ChannelType.Companion.toChannel
import com.on.staccato.presentation.notification.model.StaccatoNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StaccatoNotificationManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val permissionLauncher: NotificationPermissionManager,
    ) {
        @SuppressLint("MissingPermission")
        fun notify(staccatoNotification: StaccatoNotification) {
            if (permissionLauncher.isNotificationUnavailable()) return
            val notificationId = System.currentTimeMillis().toInt()
            val channel = staccatoNotification.channelType.toChannel(context)
            val notification = createNotification(staccatoNotification, channel)
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }

        private fun createNotification(
            notification: StaccatoNotification,
            channel: NotificationChannel,
        ) = NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setContentIntent(notification.createIntent(context))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }
