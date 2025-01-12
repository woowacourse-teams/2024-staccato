package com.on.staccato.presentation.common.location

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

class LocationManager(
    private val activity: AppCompatActivity,
) {
    fun checkLocationSetting(actionWhenHavePermission: () -> Unit) {
        val locationRequest: LocationRequest = buildLocationRequest()

        val builder =
            LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest)

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(activity)
        val locationSettingsResponse: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponse.handleLocationSettings(actionWhenHavePermission, activity)
    }

    private fun buildLocationRequest(): LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setWaitForAccurateLocation(true)
            .build()

    private fun Task<LocationSettingsResponse>.handleLocationSettings(
        actionWhenHavePermission: () -> Unit,
        activity: Activity,
    ) {
        addOnSuccessListener { actionWhenHavePermission() }
        addOnFailureListener { exception ->
            exception.actionWhenHaveNoPermission(activity)
        }
    }

    private fun Exception.actionWhenHaveNoPermission(activity: Activity) {
        if (this is ResolvableApiException) {
            startResolutionForResult(
                activity,
                REQUEST_CODE_LOCATION,
            )
        }
    }

    companion object {
        private const val INTERVAL_MILLIS = 10000L
        private const val REQUEST_CODE_LOCATION = 100
    }
}
