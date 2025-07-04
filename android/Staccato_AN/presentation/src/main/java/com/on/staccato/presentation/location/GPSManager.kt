package com.on.staccato.presentation.location

import android.app.Activity
import android.content.Context
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import javax.inject.Inject

class GPSManager
    @Inject
    constructor(
        private val locationSettingsRequest: LocationSettingsRequest,
    ) {
        fun checkLocationSetting(
            context: Context,
            activity: Activity,
            actionWhenGPSIsOn: () -> Unit,
        ) {
            val settingsClient: SettingsClient = LocationServices.getSettingsClient(context)
            val locationSettingsResponse: Task<LocationSettingsResponse> =
                settingsClient.checkLocationSettings(locationSettingsRequest)

            locationSettingsResponse.handleLocationSettings(actionWhenGPSIsOn, activity)
        }

        private fun Task<LocationSettingsResponse>.handleLocationSettings(
            actionWhenGPSIsOn: () -> Unit,
            activity: Activity,
        ) {
            addOnSuccessListener { actionWhenGPSIsOn() }
            addOnFailureListener { exception ->
                exception.actionWhenGPSIsOff(activity)
            }
        }

        private fun Exception.actionWhenGPSIsOff(activity: Activity) {
            if (this is ResolvableApiException) {
                startResolutionForResult(
                    activity,
                    REQUEST_CODE_LOCATION,
                )
            }
        }

        companion object {
            private const val REQUEST_CODE_LOCATION = 100
        }
    }
