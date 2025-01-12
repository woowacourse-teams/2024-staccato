package com.on.staccato.presentation.common.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.on.staccato.R
import com.on.staccato.presentation.util.showSnackBar
import javax.inject.Inject

class LocationPermissionManager(
    private val context: Context,
) {
    private val locationDialog = LocationDialogFragment()

    @Inject
    lateinit var locationManager: LocationManager

    fun requestPermissionLauncher(
        activity: AppCompatActivity,
        view: View,
        actionWhenHavePermission: () -> Unit,
    ) = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.forEach { (_, isGranted) ->
            if (isGranted) {
                view.showSnackBar(context.resources.getString(R.string.maps_location_permission_granted_message))
                locationManager.checkLocationSetting(activity, actionWhenHavePermission)
            } else {
                view.showSnackBar(context.resources.getString(R.string.all_location_permission_denial))
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

    fun shouldShowRequestLocationPermissionsRationale(activity: AppCompatActivity): Boolean {
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
