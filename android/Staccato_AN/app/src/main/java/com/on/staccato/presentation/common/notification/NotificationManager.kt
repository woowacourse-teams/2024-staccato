package com.on.staccato.presentation.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.on.staccato.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val permissionLauncher: NotificationPermissionManager,
    ) {
        fun notify(
            title: String,
            body: String,
        ) {
            if (permissionLauncher.isNotificationUnavailable()) return

            val channel = NotificationChannelType.toChannel(title)
            registerNotificationChannel(channel)

            val notification = createNotification(channel, title, body)
            val notificationId = System.currentTimeMillis().toInt()

            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }

        private fun registerNotificationChannel(channel: NotificationChannel) {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        private fun createNotification(
            channel: NotificationChannel,
            title: String,
            body: String,
        ) = NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }
