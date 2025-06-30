package com.on.staccato.presentation.common.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.on.staccato.presentation.R
import com.on.staccato.presentation.login.LoginActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StaccatoNotificationManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val permissionLauncher: NotificationPermissionManager,
    ) {
        @SuppressLint("MissingPermission")
        fun notify(
            title: String,
            body: String,
        ) {
            if (permissionLauncher.isNotificationUnavailable()) return

            val channel = NotificationChannelType.toChannel(context, title)
            val notification = createNotification(channel, title, body)
            val notificationId = System.currentTimeMillis().toInt()

            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }

        private fun createNotification(
            channel: NotificationChannel,
            title: String,
            body: String,
        ) = NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(createActivityPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        private fun createActivityPendingIntent(): PendingIntent {
            val intent = Intent(context, LoginActivity::class.java)
            return PendingIntent.getActivity(context, 0, intent, pendingIntentFlags)
        }

        private val pendingIntentFlags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
    }
