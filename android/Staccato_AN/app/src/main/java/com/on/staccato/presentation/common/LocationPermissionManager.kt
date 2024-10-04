package com.on.staccato.presentation.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.on.staccato.R
import com.on.staccato.presentation.common.location.LocationDialogFragment
import com.on.staccato.presentation.util.showSnackBar

class LocationPermissionManager(
    private val context: Context,
    private val activity: AppCompatActivity,
) {
    private val locationDialog = LocationDialogFragment()

    fun requestPermissionLauncher(
        view: View,
        actionWhenHavePermission: () -> Unit,
    ) = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.forEach { (_, isGranted) ->
            if (isGranted) {
                view.showSnackBar(context.resources.getString(R.string.maps_location_permission_granted_message))
                checkLocationSetting(actionWhenHavePermission)
            } else {
                view.showSnackBar(context.resources.getString(R.string.all_location_permission_denial))
            }
        }
    }

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

    fun checkSelfLocationPermission(): Boolean {
        val isGrantedCoarseLocation =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        val isGrantedFineLocation =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        return isGrantedCoarseLocation && isGrantedFineLocation
    }

    fun shouldShowRequestLocationPermissionsRationale(): Boolean {
        val shouldRequestCoarseLocation =
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )

        val shouldRequestFineLocation =
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )

        return shouldRequestCoarseLocation && shouldRequestFineLocation
    }

    fun showLocationRequestRationaleDialog(fragmentManager: FragmentManager) {
        if (!locationDialog.isAdded) {
            locationDialog.show(fragmentManager, LocationDialogFragment.TAG)
        }
    }

    private fun buildLocationRequest(): LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

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
        val locationPermissions: Array<String> =
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        private const val INTERVAL_MILLIS = 10000L
        private const val REQUEST_CODE_LOCATION = 100
    }
}
