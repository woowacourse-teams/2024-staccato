package com.on.staccato.presentation.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterManager
import com.on.staccato.presentation.R
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.databinding.FragmentMapsBinding
import com.on.staccato.presentation.location.GPSManager
import com.on.staccato.presentation.location.LocationDialogFragment.Companion.KEY_HAS_VISITED_SETTINGS
import com.on.staccato.presentation.location.LocationPermissionManager
import com.on.staccato.presentation.main.HomeRefresh
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.map.cluster.ClusterDrawManager
import com.on.staccato.presentation.map.cluster.StaccatoMarkerClusterRenderer
import com.on.staccato.presentation.map.model.StaccatoMarkerUiModel
import com.on.staccato.presentation.map.viewmodel.MapsViewModel
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.util.logging.AnalyticsEvent
import com.on.staccato.util.logging.LoggingManager
import com.on.staccato.util.logging.Param
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: 스타카토 마커 호출 횟수 줄이기
@AndroidEntryPoint
class MapsFragment : Fragment(), OnMyLocationButtonClickListener {
    @Inject
    lateinit var gpsManager: GPSManager

    @Inject
    lateinit var locationPermissionManager: LocationPermissionManager

    @Inject
    lateinit var loggingManager: LoggingManager

    @Inject
    lateinit var clusterDrawManager: ClusterDrawManager

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<StaccatoMarkerUiModel>
    private lateinit var permissionRequestLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var display: DisplayMetrics
    private val yPixel: Float by lazy { display.heightPixels / 10 * 2.5f }

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by viewModels()
    private val callback =
        OnMapReadyCallback { googleMap ->
            map = googleMap
            googleMap.setMapStyle()
            googleMap.setMapPadding()
            googleMap.moveDefaultLocation()
            checkLocationSetting()
            clusterManager = ClusterManager(context, googleMap)
            clusterManager.setup(googleMap)
            onMarkerClicked()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        display = view.resources.displayMetrics
        mapsViewModel.loadStaccatoMarkers()
        observeCurrentLocationEvent()
        setupMap()
        setupBinding()
        setupPermissionRequestLauncher()
        registerSettingsResultListener()
        observeStaccatoMarkers()
        observeLocation()
        observeHomeRefreshEvent()
        observeMessageEvent()
        observeIsRetry()
    }

    private fun setupBinding() {
        binding.cvMapsStaccatos.setContent {
            StaccatosScreen(
                onStaccatoClick = ::onStaccatoClicked,
            )
        }
    }

    private fun onStaccatoClicked(staccatoId: Long) {
        mapsViewModel.switchClusterMode(isClusterMode = false)
        navigateToStaccatoBy(staccatoId)
    }

    private fun navigateToStaccatoBy(id: Long) {
        val bundle =
            bundleOf(
                STACCATO_ID_KEY to id,
            )
        findNavController().navigate(R.id.action_staccatoFragment, bundle)
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.updateIsSettingClicked(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMyLocationButtonClick(): Boolean {
        mapsViewModel.getCurrentLocation()
        return false
    }

    private fun observeCurrentLocationEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.currentLocationEvent.collect {
                    checkLocationSetting()
                }
            }
        }
    }

    private fun setupMap() {
        val map: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.fragment_maps) as SupportMapFragment?
        map?.getMapAsync(callback)
    }

    private fun GoogleMap.setMapStyle() {
        setMapStyle(
            MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.google_map_style),
        )
        uiSettings.isMyLocationButtonEnabled = false
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

    private fun setupPermissionRequestLauncher() {
        permissionRequestLauncher =
            locationPermissionManager.requestPermissionLauncher(
                activity = requireActivity(),
                activityResultCaller = this,
                actionWhenHavePermission = ::enableMyLocation,
            )
    }

    private fun registerSettingsResultListener() {
        childFragmentManager.setFragmentResultListener(
            KEY_HAS_VISITED_SETTINGS,
            viewLifecycleOwner,
        ) { _, bundle ->
            val hasVisitedSettings = bundle.getBoolean(KEY_HAS_VISITED_SETTINGS, false)
            if (hasVisitedSettings) checkLocationSetting()
        }
    }

    private fun checkLocationSetting() {
        gpsManager.checkLocationSetting(
            context = requireContext(),
            activity = requireActivity(),
            actionWhenGPSIsOn = ::enableMyLocation,
        )
    }

    @SuppressLint("MissingPermission")
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
                locationPermissionManager.showLocationRequestRationaleDialog(
                    childFragmentManager,
                )
            }

            else -> {
                permissionRequestLauncher.launch(
                    LocationPermissionManager.locationPermissions,
                )
            }
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
        sharedViewModel.isBottomSheetHalfExpanded.observe(viewLifecycleOwner) { isHalfExpanded ->
            val mapPaddingBottom =
                if (isHalfExpanded) (requireView().height / BOTTOM_SHEET_HALF_RATIO).toInt() else DEFAULT_MAP_PADDING
            val yPixel = if (isHalfExpanded) yPixel else -yPixel

            setPadding(
                DEFAULT_MAP_PADDING,
                DEFAULT_MAP_PADDING,
                DEFAULT_MAP_PADDING,
                mapPaddingBottom,
            )
            animateCamera(CameraUpdateFactory.scrollBy(DEFAULT_X_PIXEL, yPixel))
        }
    }

    private fun ClusterManager<StaccatoMarkerUiModel>.setup(googleMap: GoogleMap) {
        renderer = createStaccatoMarkerClusterRenderer(googleMap)
        onClusterClicked()
        googleMap.setOnCameraIdleListener(clusterManager)
    }

    private fun createStaccatoMarkerClusterRenderer(googleMap: GoogleMap) =
        StaccatoMarkerClusterRenderer(
            context = requireContext(),
            map = googleMap,
            clusterManager = clusterManager,
            clusterDrawManager = clusterDrawManager,
        )

    private fun ClusterManager<StaccatoMarkerUiModel>.onClusterClicked() {
        setOnClusterClickListener {
            mapsViewModel.switchClusterMode(
                isClusterMode = true,
                markers = it.items.toList(),
            )
            true
        }
    }

    private fun observeStaccatoMarkers() {
        mapsViewModel.staccatoMarkers.observe(viewLifecycleOwner) { markers ->
            if (this::map.isInitialized.not()) return@observe
            clusterManager.clearItems()
            clusterManager.addItems(markers)
            clusterManager.cluster()
        }
    }

    private fun onMarkerClicked() {
        clusterManager.setOnClusterItemClickListener { item ->
            navigateToStaccatoBy(id = item.staccatoId)
            sharedViewModel.updateHalfModeEvent()

            loggingManager.logEvent(
                AnalyticsEvent.NAME_STACCATO_READ,
                Param(Param.KEY_IS_VIEWED_BY_MARKER, true),
            )
            true
        }
    }

    private fun observeHomeRefreshEvent() {
        sharedViewModel.homeRefresh.observe(viewLifecycleOwner) {
            if (it is HomeRefresh.All || it is HomeRefresh.Map) {
                Log.d("hye", "지도 화면: $it")
                mapsViewModel.loadStaccatoMarkers()
            }
        }
    }

    private fun observeMessageEvent() {
        mapsViewModel.messageEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is MessageEvent.Text -> {}
                is MessageEvent.ResId -> sharedViewModel.emitMessageEvent(event)
            }
        }
    }

    private fun observeIsRetry() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.retryEvent.collect {
                    mapsViewModel.loadStaccatoMarkers()
                }
            }
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
