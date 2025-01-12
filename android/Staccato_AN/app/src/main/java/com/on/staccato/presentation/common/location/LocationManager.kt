package com.on.staccato.presentation.common.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity.GRANULARITY_PERMISSION_LEVEL
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task

class LocationManager(
    private val activity: AppCompatActivity,
    context: Context,
) {
    private val locationRequest: LocationRequest by lazy { buildLocationRequest() }
    private val locationSettingsRequest: LocationSettingsRequest.Builder by lazy {
        LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
    }

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun checkLocationSetting(actionWhenHavePermission: () -> Unit) {
        val settingsClient: SettingsClient = LocationServices.getSettingsClient(activity)
        val locationSettingsResponse: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(locationSettingsRequest.build())

        locationSettingsResponse.handleLocationSettings(actionWhenHavePermission, activity)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Task<Location> =
        fusedLocationProviderClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token,
        )

    private fun buildLocationRequest(): LocationRequest =
        LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS)
            .setGranularity(GRANULARITY_PERMISSION_LEVEL)
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
