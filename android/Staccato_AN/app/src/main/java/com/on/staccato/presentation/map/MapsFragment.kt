package com.on.staccato.presentation.map

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.on.staccato.R
import com.on.staccato.domain.model.StaccatoLocation
import com.on.staccato.presentation.common.location.LocationManager
import com.on.staccato.presentation.common.location.LocationPermissionManager
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.map.model.MarkerUiModel
import com.on.staccato.presentation.map.viewmodel.MapsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMyLocationButtonClickListener {
    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var locationPermissionManager: LocationPermissionManager

    private lateinit var map: GoogleMap
    private lateinit var permissionRequestLauncher: ActivityResultLauncher<Array<String>>

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by viewModels()
    private val callback =
        OnMapReadyCallback { googleMap ->
            map = googleMap
            googleMap.setMapStyle()
            googleMap.moveDefaultLocation()
            checkLocationSetting()
            googleMap.onMarkerClicked()
            googleMap.setOnMyLocationButtonClickListener(this)
        }

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
        setupMap()
        setupPermissionRequestLauncher(view)
        observeStaccatoId()
        observeStaccatoLocations()
        observeDeletedStaccato()
        observeLocation()
    }

    override fun onResume() {
        super.onResume()
        if (this::map.isInitialized) checkLocationSetting()
        mapsViewModel.loadStaccatos()
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.updateIsSettingClicked(false)
    }

    override fun onMyLocationButtonClick(): Boolean {
        getMyLocation()
        return false
    }

    private fun setupMap() {
        val map: SupportMapFragment? = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        map?.getMapAsync(callback)
    }

    private fun GoogleMap.setMapStyle() {
        setMapStyle(
            MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.google_map_style),
        )
    }

    private fun GoogleMap.moveDefaultLocation() {
        val defaultLocation =
            LatLng(SEOUL_STATION_LATITUDE, SEOUL_STATION_LONGITUDE)
        animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                defaultLocation,
                DEFAULT_ZOOM,
            ),
        )
    }

    private fun setupPermissionRequestLauncher(view: View) {
        permissionRequestLauncher =
            locationPermissionManager.requestPermissionLauncher(
                activity = requireActivity(),
                view = view,
                actionWhenHavePermission = ::enableMyLocation,
            )
    }

    private fun checkLocationSetting() {
        locationManager.checkLocationSetting(activity = requireActivity(), actionWhenGPSIsOn = ::enableMyLocation)
    }

    private fun enableMyLocation() {
        val isLocationPermissionGranted = locationPermissionManager.checkSelfLocationPermission()
        val shouldShowRequestLocationPermissionsRationale =
            locationPermissionManager.shouldShowRequestLocationPermissionsRationale(activity = requireActivity())

        when {
            isLocationPermissionGranted -> {
                map.isMyLocationEnabled = true
                getMyLocation()
            }

            shouldShowRequestLocationPermissionsRationale -> {
                observeIsPermissionCancelClicked {
                    locationPermissionManager.showLocationRequestRationaleDialog(
                        childFragmentManager,
                    )
                }
            }

            else -> {
                observeIsPermissionCancelClicked {
                    permissionRequestLauncher.launch(
                        LocationPermissionManager.locationPermissions,
                    )
                }
            }
        }
    }

    private fun getMyLocation() {
        val currentLocation: Task<Location> = locationManager.getCurrentLocation()
        currentLocation.addOnSuccessListener {
            mapsViewModel.setCurrentLocation(it.latitude, it.longitude)
        }
    }

    private fun observeIsPermissionCancelClicked(requestLocationPermissions: () -> Unit) {
        sharedViewModel.isPermissionCancelClicked.observe(viewLifecycleOwner) { isCancel ->
            if (!isCancel) requestLocationPermissions()
        }
    }

    private fun observeLocation() {
        observeCurrentLocation()
        observeStaccatoLocation()
    }

    private fun observeStaccatoLocation() {
        sharedViewModel.staccatoLocation.observe(viewLifecycleOwner) {
            mapsViewModel.setCurrentLocation(it.latitude, it.longitude)
        }
    }

    private fun observeCurrentLocation() {
        mapsViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            moveCamera(location.latitude, location.longitude)
        }
    }

    private fun moveCamera(
        latitude: Double,
        longitude: Double,
    ) {
        val currentLocation = LatLng(latitude, longitude)
        sharedViewModel.isHalf.observe(viewLifecycleOwner) { isHalf ->
            if (isHalf) {
                val mapPaddingBottom = (requireView().height / 1.8).toInt()
                setMapPadding(currentLocation, mapPaddingBottom)
            } else {
                setMapPadding(currentLocation)
            }
        }
    }

    private fun setMapPadding(
        currentLocation: LatLng,
        mapPaddingBottom: Int = DEFAULT_MAP_PADDING,
    ) {
        map.setPadding(
            DEFAULT_MAP_PADDING,
            DEFAULT_MAP_PADDING,
            DEFAULT_MAP_PADDING,
            mapPaddingBottom,
        )
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM))
    }

    private fun observeStaccatoLocations() {
        mapsViewModel.staccatoLocations.observe(viewLifecycleOwner) { staccatoLocations ->
            if (this::map.isInitialized) {
                map.clear()
                addMarkers(staccatoLocations)
            }
        }
    }

    private fun addMarkers(staccatoLocations: List<StaccatoLocation>) {
        val markers =
            staccatoLocations.map { staccatoLocation ->
                val latLng = LatLng(staccatoLocation.latitude, staccatoLocation.longitude)
                val markerOptions: MarkerOptions = MarkerOptions().position(latLng)
                val marker: Marker =
                    map.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_pin_3x)))
                        ?: return
                MarkerUiModel(staccatoLocation.staccatoId, marker.id)
            }
        mapsViewModel.setMarkers(markers)
    }

    private fun GoogleMap.onMarkerClicked() {
        setOnMarkerClickListener { marker ->
            mapsViewModel.findStaccatoId(marker.id)
            false
        }
    }

    private fun observeStaccatoId() {
        mapsViewModel.staccatoId.observe(viewLifecycleOwner) { id ->
            sharedViewModel.updateStaccatoId(id)
        }
    }

    private fun observeDeletedStaccato() {
        sharedViewModel.isStaccatosUpdated.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) mapsViewModel.loadStaccatos()
        }
    }

    companion object {
        private const val DEFAULT_MAP_PADDING = 0
        private const val DEFAULT_ZOOM = 15f
        private const val SEOUL_STATION_LATITUDE = 37.554677038139815
        private const val SEOUL_STATION_LONGITUDE = 126.97061201084968
    }
}
