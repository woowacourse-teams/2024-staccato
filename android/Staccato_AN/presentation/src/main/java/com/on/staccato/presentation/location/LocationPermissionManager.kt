package com.on.staccato.presentation.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.on.staccato.presentation.R
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationPermissionManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val gpsManager: GPSManager,
    ) {
        private val locationDialog = LocationDialogFragment()

        fun requestPermissionLauncher(
            activityResultCaller: ActivityResultCaller,
            activity: Activity,
            actionWhenHavePermission: () -> Unit,
        ) = activityResultCaller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { (_, isGranted) ->
                if (isGranted) {
                    context.showToast(context.resources.getString(R.string.maps_location_permission_granted_message))
                    gpsManager.checkLocationSetting(context, activity, actionWhenHavePermission)
                } else {
                    context.showToast(context.resources.getString(R.string.all_location_permission_denial))
                }
            }
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

        fun shouldShowRequestLocationPermissionsRationale(activity: Activity): Boolean {
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

        companion object {
            val locationPermissions: Array<String> =
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
        }
    }
