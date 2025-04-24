package com.on.staccato.presentation.map

import android.os.Bundle
import android.util.DisplayMetrics
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
import com.google.android.gms.maps.model.MarkerOptions
import com.on.staccato.R
import com.on.staccato.domain.model.StaccatoLocation
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.common.location.GPSManager
import com.on.staccato.presentation.common.location.LocationPermissionManager
import com.on.staccato.presentation.common.location.PermissionCancelListener
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.map.viewmodel.MapsViewModel
import com.on.staccato.presentation.util.showSnackBar
import com.on.staccato.util.logging.AnalyticsEvent
import com.on.staccato.util.logging.LoggingManager
import com.on.staccato.util.logging.Param
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMyLocationButtonClickListener {
    @Inject
    lateinit var gpsManager: GPSManager

    @Inject
    lateinit var locationPermissionManager: LocationPermissionManager

    @Inject
    lateinit var loggingManager: LoggingManager

    private lateinit var map: GoogleMap
    private lateinit var permissionRequestLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var display: DisplayMetrics
    private val yPixel: Float by lazy { display.heightPixels / 10 * 2.5f }

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
            googleMap.setMapPadding()
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
        display = view.resources.displayMetrics
        mapsViewModel.loadStaccatos()
        setupMap()
        setupPermissionRequestLauncher(view)
        observeStaccatoId()
        observeMarkerOptions()
        observeUpdatedStaccato()
        observeLocation()
        observeIsTimelineUpdated()
        observeException()
        observeIsRetry()
    }

    override fun onResume() {
        super.onResume()
        if (this::map.isInitialized) checkLocationSetting()
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.updateIsSettingClicked(false)
    }

    override fun onMyLocationButtonClick(): Boolean {
        mapsViewModel.getCurrentLocation()
        return false
    }

    private fun setupMap() {
        val map: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
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
                activityResultCaller = this,
                actionWhenHavePermission = ::enableMyLocation,
            )
    }

    private fun checkLocationSetting() {
        gpsManager.checkLocationSetting(
            context = requireContext(),
            activity = requireActivity(),
            actionWhenGPSIsOn = ::enableMyLocation,
        )
    }

    private fun enableMyLocation() {
        val isLocationPermissionGranted = locationPermissionManager.checkSelfLocationPermission()
        val shouldShowRequestLocationPermissionsRationale =
            locationPermissionManager.shouldShowRequestLocationPermissionsRationale(activity = requireActivity())

        when {
            isLocationPermissionGranted -> {
                map.isMyLocationEnabled = true
                mapsViewModel.getCurrentLocation()
            }

            shouldShowRequestLocationPermissionsRationale -> {
                observeIsPermissionCanceled {
                    locationPermissionManager.showLocationRequestRationaleDialog(
                        childFragmentManager,
                    )
                }
            }

            else -> {
                observeIsPermissionCanceled {
                    permissionRequestLauncher.launch(
                        LocationPermissionManager.locationPermissions,
                    )
                }
            }
        }
    }

    private fun observeIsPermissionCanceled(listener: PermissionCancelListener) {
        sharedViewModel.isPermissionCanceled.observe(viewLifecycleOwner) { isCanceled ->
            if (!isCanceled) listener.requestPermission()
        }
    }

    private fun observeLocation() {
        observeFocusLocation()
        observeStaccatoLocation()
    }

    private fun observeStaccatoLocation() {
        sharedViewModel.staccatoLocation.observe(viewLifecycleOwner) {
            mapsViewModel.updateFocusLocation(it.latitude, it.longitude)
        }
    }

    private fun observeFocusLocation() {
        mapsViewModel.focusLocation.observe(viewLifecycleOwner) { location ->
            moveCamera(location.latitude, location.longitude)
        }
    }

    private fun moveCamera(
        latitude: Double,
        longitude: Double,
    ) {
        val currentLocation = LatLng(latitude, longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM))
    }

    private fun GoogleMap.setMapPadding() {
        sharedViewModel.isBottomSheetHalf.observe(viewLifecycleOwner) { isBottomSheetHalf ->
            val mapPaddingBottom =
                if (isBottomSheetHalf) (requireView().height / BOTTOM_SHEET_HALF_RATIO).toInt() else DEFAULT_MAP_PADDING
            val yPixel = if (isBottomSheetHalf) yPixel else -yPixel

            setPadding(
                DEFAULT_MAP_PADDING,
                DEFAULT_MAP_PADDING,
                DEFAULT_MAP_PADDING,
                mapPaddingBottom,
            )
            animateCamera(CameraUpdateFactory.scrollBy(DEFAULT_X_PIXEL, yPixel))
        }
    }

    private fun observeMarkerOptions() {
        mapsViewModel.staccatoLocations.observe(viewLifecycleOwner) { staccatoLocations ->
            if (this::map.isInitialized) {
                map.clear()
                addMarkersAndGetIds(staccatoLocations)?.let { markerIds ->
                    mapsViewModel.updateMarkers(
                        markerIds,
                        staccatoLocations.map { it.staccatoId },
                    )
                }
            }
        }
    }

    private fun addMarkersAndGetIds(staccatoLocation: List<StaccatoLocation>): List<String>? =
        staccatoLocation.map {
            val markerOption = it.toMarkerOption()
            val addedMarker =
                map.addMarker(markerOption) ?: run {
                    handleMarkerError()
                    return null
                }
            addedMarker.id
        }

    private fun StaccatoLocation.toMarkerOption() =
        MarkerOptions()
            .position(LatLng(latitude, longitude))
            .icon(BitmapDescriptorFactory.fromResource(CategoryColor.getMarkerResBy(color)))

    private fun handleMarkerError() {
        map.clear()
        requireView().showSnackBar(getString(R.string.maps_markers_loading_error))
    }

    private fun GoogleMap.onMarkerClicked() {
        setOnMarkerClickListener { marker ->
            mapsViewModel.findStaccatoId(marker.id)
            loggingManager.logEvent(
                AnalyticsEvent.NAME_STACCATO_READ,
                Param(Param.KEY_IS_VIEWED_BY_MARKER, true),
            )
            false
        }
    }

    private fun observeStaccatoId() {
        mapsViewModel.staccatoId.observe(viewLifecycleOwner) { id ->
            sharedViewModel.updateStaccatoId(id)
        }
    }

    private fun observeUpdatedStaccato() {
        sharedViewModel.isStaccatosUpdated.observe(viewLifecycleOwner) { isUpdated ->
            if (isUpdated) mapsViewModel.loadStaccatos()
        }
    }

    private fun observeIsTimelineUpdated() {
        sharedViewModel.isTimelineUpdated.observe(viewLifecycleOwner) {
            if (it) mapsViewModel.loadStaccatos()
        }
    }

    private fun observeException() {
        mapsViewModel.exception.observe(viewLifecycleOwner) { state ->
            sharedViewModel.updateException(state)
        }
    }

    private fun observeIsRetry() {
        sharedViewModel.isRetry.observe(viewLifecycleOwner) {
            if (it) mapsViewModel.loadStaccatos()
        }
    }

    companion object {
        private const val DEFAULT_MAP_PADDING = 0
        private const val DEFAULT_ZOOM = 15f
        private const val SEOUL_STATION_LATITUDE = 37.554677038139815
        private const val SEOUL_STATION_LONGITUDE = 126.97061201084968
        private const val DEFAULT_X_PIXEL = 0f
        private const val BOTTOM_SHEET_HALF_RATIO = 1.8
    }
}
