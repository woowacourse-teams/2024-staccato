package com.woowacourse.staccato.presentation.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.staccato.R

class MapsFragment : Fragment() {
    private val callback =
        OnMapReadyCallback { googleMap ->
            checkLocationPermissions(googleMap)
            val woowacourse = LatLng(37.505, 127.050)
            googleMap.addMarker(MarkerOptions().position(woowacourse).title("우아한테크코스"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(woowacourse, 15f))
        }

    private val locationPermissions: List<String> =
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

    private val requestPermission = initRequestPermissionsLauncher()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun checkLocationPermissions(googleMap: GoogleMap) {
        val isAccessFineLocationGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        val isAccessCoarseLocationGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        if (isAccessFineLocationGranted || isAccessCoarseLocationGranted) {
            googleMap.isMyLocationEnabled = true
            return
        } else {
            requestPermission.launch(locationPermissions.toTypedArray())
        }
    }

    private fun initRequestPermissionsLauncher() =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { (_, isGranted) ->
                if (isGranted) {
                    makeSnackBar(getString(R.string.maps_location_permission_granted_message)).show()
                } else {
                    showPermissionRequestSnackBar()
                }
            }
        }

    private fun showPermissionRequestSnackBar() {
        val snackBar = makeSnackBar(getString(R.string.maps_location_permission_required_message))
        snackBar.setAction()
        snackBar.show()
    }

    private fun makeSnackBar(message: String): Snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)

    private fun Snackbar.setAction() {
        setAction(R.string.snack_bar_move_to_setting) {
            val uri = Uri.fromParts(PACKAGE_SCHEME, context.packageName, null)
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
            startActivity(intent)
        }
    }

    companion object {
        const val PACKAGE_SCHEME = "package"
    }
}
