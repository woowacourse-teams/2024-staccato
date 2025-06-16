package com.on.staccato.presentation.common.notification

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationPermissionManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private var permissionLauncher: ActivityResultLauncher<String>? = null

        fun isNotificationUnavailable() = isPermissionRequired() && isPermissionNotGranted()

        fun register(
            caller: ActivityResultCaller,
            activity: Activity,
            showRationale: () -> Unit,
        ) {
            permissionLauncher =
                caller.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted.not() && shouldShowRationale(activity)) {
                        showRationale()
                    }
                }
        }

        fun launch() {
            if (isNotificationUnavailable()) {
                permissionLauncher?.launch(NOTIFICATION_PERMISSION)
            }
        }

        /**
         * Android 13 (API 33) 이상에서는 POST_NOTIFICATIONS 권한이 있어야 알림을 보낼 수 있습니다.
         * 그 이전 버전에서는 별도의 권한 없이도 알림이 가능합니다.
         **/
        private fun isPermissionRequired(): Boolean = Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT

        private fun isPermissionNotGranted(): Boolean =
            ContextCompat.checkSelfPermission(
                context,
                NOTIFICATION_PERMISSION,
            ) != PackageManager.PERMISSION_GRANTED

        private fun shouldShowRationale(activity: Activity): Boolean =
            ActivityCompat.shouldShowRequestPermissionRationale(activity, NOTIFICATION_PERMISSION)

        companion object {
            private const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
        }
    }
