package com.on.staccato.presentation.common.location

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import javax.inject.Inject

class LocationManager
    @Inject
    constructor(
        private val locationSettingsRequest: LocationSettingsRequest,
        private val fusedLocationProviderClient: FusedLocationProviderClient,
    ) {
        fun checkLocationSetting(
            activity: Activity,
            actionWhenGPSIsOn: () -> Unit,
        ) {
            val settingsClient: SettingsClient = LocationServices.getSettingsClient(activity)
            val locationSettingsResponse: Task<LocationSettingsResponse> =
                settingsClient.checkLocationSettings(locationSettingsRequest)

            locationSettingsResponse.handleLocationSettings(actionWhenGPSIsOn, activity)
        }

        @SuppressLint("MissingPermission")
        fun getCurrentLocation(): Task<Location> =
            fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token,
            )

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
