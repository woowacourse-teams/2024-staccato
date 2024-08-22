package com.woowacourse.staccato.presentation.maps

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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.staccato.R
import com.woowacourse.staccato.data.StaccatoClient.momentApiService
import com.woowacourse.staccato.data.moment.MomentDefaultRepository
import com.woowacourse.staccato.data.moment.MomentRemoteDataSource
import com.woowacourse.staccato.domain.model.MomentLocation
import com.woowacourse.staccato.presentation.maps.model.MarkerUiModel
import com.woowacourse.staccato.presentation.moment.MomentFragment.Companion.MOMENT_ID_KEY

class MapsFragment : Fragment() {
    private val viewModel: MapsViewModel by viewModels {
        MapsViewModelFactory(
            MomentDefaultRepository(
                MomentRemoteDataSource(momentApiService),
            ),
        )
    }

    private val mapReadyCallback =
        OnMapReadyCallback { googleMap ->
            checkLocationPermissions(googleMap)
            observeMomentLocations(googleMap)
            moveCamera(googleMap)
            onMarkerClicked(googleMap)
        }

    private val locationPermissions: Array<String> =
        arrayOf(
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
        mapFragment?.getMapAsync(mapReadyCallback)
        observeStaccatoId()
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
            requestPermission.launch(locationPermissions)
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

    private fun makeSnackBar(message: String) = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)

    private fun Snackbar.setAction() {
        setAction(R.string.snack_bar_move_to_setting) {
            val uri = Uri.fromParts(PACKAGE_SCHEME, context.packageName, null)
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
            startActivity(intent)
        }
    }

    private fun observeMomentLocations(googleMap: GoogleMap) {
        viewModel.momentLocations.observe(viewLifecycleOwner) { momentLocations ->
            addMarkers(momentLocations, googleMap)
        }
    }

    private fun addMarkers(
        momentLocations: List<MomentLocation>,
        googleMap: GoogleMap,
    ) {
        val markers: MutableList<MarkerUiModel> = mutableListOf()
        momentLocations.forEach { momentLocation ->
            val latLng = LatLng(momentLocation.latitude, momentLocation.longitude)
            val markerOptions: MarkerOptions = MarkerOptions().position(latLng)
            val marker: Marker = googleMap.addMarker(markerOptions) ?: return
            val markerId: String = marker.id
            markers.add(MarkerUiModel(momentLocation.momentId, markerId))
        }
        viewModel.setMarkers(markers)
    }

    private fun moveCamera(googleMap: GoogleMap) {
        val woowacourse = LatLng(37.5057434, 127.0506698)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(woowacourse, 15f))
    }

    private fun onMarkerClicked(googleMap: GoogleMap) {
        googleMap.setOnMarkerClickListener { marker ->
            viewModel.findStaccatoId(marker.id)
            false
        }
    }

    private fun observeStaccatoId() {
        viewModel.staccatoId.observe(viewLifecycleOwner) { staccatoId ->
            val bundle =
                bundleOf(MOMENT_ID_KEY to staccatoId)
            findNavController().navigate(R.id.momentFragment, bundle)
            parentFragmentManager.setFragmentResult(
                BOTTOM_SHEET_STATE_REQUEST_KEY,
                bundleOf(BOTTOM_SHEET_NEW_STATE to BottomSheetBehavior.STATE_EXPANDED),
            )
        }
    }

    companion object {
        const val PACKAGE_SCHEME = "package"
        const val BOTTOM_SHEET_STATE_REQUEST_KEY = "requestKey"
        const val BOTTOM_SHEET_NEW_STATE = "newState"
    }
}
