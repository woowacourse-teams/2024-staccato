package com.on.staccato.presentation.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.ActivityMainBinding
import com.on.staccato.domain.model.MomentLocation
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.location.LocationDialogFragment
import com.on.staccato.presentation.main.model.MarkerUiModel
import com.on.staccato.presentation.main.viewmodel.MapsViewModel
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.moment.MomentFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.momentcreation.MomentCreationActivity
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    BindingActivity<ActivityMainBinding>(),
    OnMapReadyCallback,
    MainHandler {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private lateinit var googleMap: GoogleMap
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val locationPermissions: Array<String> =
        arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
        )
    private val sharedViewModel: SharedViewModel by viewModels()
    private val mapsViewModel: MapsViewModel by viewModels()
    private val locationDialog = LocationDialogFragment()

    private val requestPermissionLauncher = initRequestPermissionsLauncher()

    val memoryCreationLauncher: ActivityResultLauncher<Intent> = handleMemoryResult()
    val memoryUpdateLauncher: ActivityResultLauncher<Intent> = handleMemoryResult()
    val staccatoCreationLauncher: ActivityResultLauncher<Intent> = handleStaccatoResult()
    val staccatoUpdateLauncher: ActivityResultLauncher<Intent> = handleStaccatoResult()

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.handler = this
        setupGoogleMap()
        setupFusedLocationProviderClient()
        observeCurrentLocation()
        observeMomentLocations()
        observeStaccatoId()
        setupBottomSheetController()
        setupBackPressedHandler()
        setUpBottomSheetBehaviorAction()
        setUpBottomSheetStateListener()
    }

    override fun onResume() {
        super.onResume()
        if (this::googleMap.isInitialized) checkLocationSetting()
        mapsViewModel.loadStaccatos()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        moveDefaultLocation()
        checkLocationSetting()
        onMarkerClicked(map)
    }

    private fun moveDefaultLocation() {
        val defaultLocation =
            LatLng(SEOUL_STATION_LATITUDE, SEOUL_STATION_LONGITUDE)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM))
    }

    override fun onStaccatoCreationClicked() {
        MomentCreationActivity.startWithResultLauncher(
            0L,
            "임시 추억",
            this,
            staccatoCreationLauncher,
        )
    }

    private fun initRequestPermissionsLauncher() =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { (_, isGranted) ->
                if (isGranted) {
                    makeSnackBar(
                        getString(R.string.maps_location_permission_granted_message),
                    ).show()
                    checkLocationSetting()
                } else {
                    makeSnackBar(
                        getString(R.string.all_location_permission_denial),
                    ).show()
                }
            }
        }

    private fun setupGoogleMap() {
        val map: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_map) as? SupportMapFragment
        map?.getMapAsync(this)
    }

    private fun checkLocationSetting() {
        val locationRequest: LocationRequest = buildLocationRequest()

        val builder =
            LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest)

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponse: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        succeedLocationSettings(locationSettingsResponse)
        failLocationSettings(locationSettingsResponse)
    }

    private fun succeedLocationSettings(locationSettingsResponse: Task<LocationSettingsResponse>) {
        locationSettingsResponse.addOnSuccessListener {
            enableMyLocation()
        }
    }

    private fun failLocationSettings(locationSettingsResponse: Task<LocationSettingsResponse>) {
        locationSettingsResponse.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                exception.startResolutionForResult(
                    this,
                    REQUEST_CODE_LOCATION,
                )
            }
        }
    }

    private fun buildLocationRequest(): LocationRequest =
        LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

    private fun enableMyLocation() {
        when {
            checkSelfLocationPermission() -> {
                googleMap.isMyLocationEnabled = true
                val currentLocation: Task<Location> =
                    fusedLocationProviderClient.getCurrentLocation(
                        PRIORITY_HIGH_ACCURACY,
                        null,
                    )
                mapsViewModel.setCurrentLocation(currentLocation)
            }

            shouldShowRequestLocationPermissionsRationale() -> {
                observeIsLocationDenial { showLocationRequestRationaleDialog() }
            }

            else -> {
                observeIsLocationDenial { requestPermissionLauncher.launch(locationPermissions) }
            }
        }
    }

    private fun observeIsLocationDenial(action: () -> Unit) {
        sharedViewModel.isLocationDenial.observe(this) { isCancel ->
            if (!isCancel) action()
        }
    }

    private fun checkSelfLocationPermission(): Boolean {
        val isGrantedCoarseLocation =
            ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        val isGrantedFineLocation =
            ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        return isGrantedCoarseLocation && isGrantedFineLocation
    }

    private fun shouldShowRequestLocationPermissionsRationale(): Boolean {
        val shouldRequestCoarseLocation =
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                ACCESS_COARSE_LOCATION,
            )

        val shouldRequestFineLocation =
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                ACCESS_FINE_LOCATION,
            )

        return shouldRequestCoarseLocation && shouldRequestFineLocation
    }

    private fun showLocationRequestRationaleDialog() {
        if (!locationDialog.isAdded) {
            locationDialog.show(supportFragmentManager, LocationDialogFragment.TAG)
        }
    }

    private fun setupFusedLocationProviderClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun observeCurrentLocation() {
        mapsViewModel.currentLocation.observe(this) { currentLocation ->
            moveCurrentLocation(currentLocation)
        }
    }

    private fun moveCurrentLocation(currentLocation: Task<Location>) {
        currentLocation.addOnSuccessListener { location ->
            moveCamera(location)
        }
    }

    private fun moveCamera(location: Location?) {
        if (location != null) observeMapIsHalf(location)
    }

    private fun observeMapIsHalf(location: Location) {
        val currentLocation = LatLng(location.latitude, location.longitude)
        mapsViewModel.isHalf.observe(this) { isHalf ->
            if (isHalf) {
                val mapPaddingBottom = (binding.root.height / 1.8).toInt()
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
        googleMap.setPadding(
            DEFAULT_MAP_PADDING,
            DEFAULT_MAP_PADDING,
            DEFAULT_MAP_PADDING,
            mapPaddingBottom,
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM))
    }

    private fun observeMomentLocations() {
        mapsViewModel.momentLocations.observe(this) { momentLocations ->
            if (this::googleMap.isInitialized) googleMap.clear()
            addMarkers(momentLocations)
        }
    }

    private fun addMarkers(momentLocations: List<MomentLocation>) {
        val markers: MutableList<MarkerUiModel> = mutableListOf()
        momentLocations.forEach { momentLocation ->
            val latLng = LatLng(momentLocation.latitude, momentLocation.longitude)
            val markerOptions: MarkerOptions = MarkerOptions().position(latLng)
            val marker: Marker = googleMap.addMarker(markerOptions) ?: return
            val markerId: String = marker.id
            markers.add(MarkerUiModel(momentLocation.momentId, markerId))
        }
        mapsViewModel.setMarkers(markers)
    }

    private fun onMarkerClicked(googleMap: GoogleMap) {
        googleMap.setOnMarkerClickListener { marker ->
            mapsViewModel.findStaccatoId(marker.id)
            false
        }
    }

    private fun observeStaccatoId() {
        mapsViewModel.staccatoId.observe(this) { staccatoId ->
            navigateToStaccato(staccatoId)
            supportFragmentManager.setFragmentResult(
                BOTTOM_SHEET_STATE_REQUEST_KEY,
                bundleOf(BOTTOM_SHEET_NEW_STATE to STATE_EXPANDED),
            )
        }
    }

    private fun navigateToStaccato(staccatoId: Long?) {
        val navOptions =
            NavOptions.Builder()
                .setPopUpTo(R.id.momentFragment, true)
                .build()
        val bundle =
            bundleOf(STACCATO_ID_KEY to staccatoId)

        navController.navigate(R.id.momentFragment, bundle, navOptions)
    }

    private fun makeSnackBar(message: String) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)

    private fun setupBackPressedHandler() {
        var backPressedTime = 0L
        onBackPressedDispatcher.addCallback {
            if (behavior.state == STATE_EXPANDED) {
                behavior.state = STATE_HALF_EXPANDED
            } else if (behavior.state == STATE_HALF_EXPANDED) {
                behavior.state = STATE_COLLAPSED
            } else {
                handleBackPressedTwice(backPressedTime).also {
                    backPressedTime = it
                }
            }
        }
    }

    private fun handleBackPressedTwice(backPressedTime: Long): Long {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime >= 3000L) {
            showToast(getString(R.string.main_end))
        } else {
            finish()
        }
        return currentTime
    }

    private fun setupBottomSheetController() {
        behavior =
            BottomSheetBehavior.from(binding.constraintMainBottomSheet)
                .apply { setState(STATE_HALF_EXPANDED) }
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_main_bottom_sheet) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun handleMemoryResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    sharedViewModel.setTimelineHasUpdated()
                    val bundle: Bundle = makeBundle(it, MEMORY_ID_KEY)
                    navigateTo(R.id.memoryFragment, R.id.timelineFragment, bundle, false)
                }
            }
        }

    private fun handleStaccatoResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val bundle: Bundle = makeBundle(it, STACCATO_ID_KEY)
                    navigateTo(R.id.momentFragment, R.id.momentFragment, bundle, true)
                }
            }
        }

    private fun makeBundle(
        it: Intent,
        keyName: String,
    ): Bundle {
        val id = it.getLongExtra(keyName, 0L)
        return bundleOf(keyName to id)
    }

    private fun navigateTo(
        navigateToId: Int,
        popUpToId: Int,
        bundle: Bundle?,
        inclusive: Boolean,
    ) {
        val navOptions = buildNavOptions(popUpToId, inclusive)
        navController.navigate(navigateToId, bundle, navOptions)
        behavior.state = STATE_EXPANDED
    }

    private fun buildNavOptions(
        popUpToId: Int,
        inclusive: Boolean,
    ) = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setPopUpTo(popUpToId, inclusive)
        .build()

    private fun setUpBottomSheetBehaviorAction() {
        behavior.apply {
            addBottomSheetCallback(
                object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(
                        bottomSheet: View,
                        newState: Int,
                    ) {
                        when (newState) {
                            STATE_EXPANDED -> {
                                binding.viewMainDragBar.visibility =
                                    View.INVISIBLE
                                binding.constraintMainBottomSheet.setBackgroundResource(
                                    R.drawable.shape_bottom_sheet_square,
                                )
                            }

                            STATE_HALF_EXPANDED -> {
                                mapsViewModel.setIsHalf(isHalf = true)
                                currentFocus?.let { clearFocusAndHideKeyboard(it) }
                            }

                            else -> {
                                binding.viewMainDragBar.visibility = View.VISIBLE
                                binding.constraintMainBottomSheet.setBackgroundResource(
                                    R.drawable.shape_bottom_sheet_20dp,
                                )
                                mapsViewModel.setIsHalf(isHalf = false)
                                currentFocus?.let { clearFocusAndHideKeyboard(it) }
                            }
                        }
                    }

                    override fun onSlide(
                        p0: View,
                        p1: Float,
                    ) {
                    }
                },
            )
        }
    }

    private fun setUpBottomSheetStateListener() {
        supportFragmentManager.setFragmentResultListener(
            BOTTOM_SHEET_STATE_REQUEST_KEY,
            this,
        ) { _, bundle ->
            val newState = bundle.getInt(BOTTOM_SHEET_NEW_STATE)
            behavior.state = newState
            Log.d("hye", "$newState")
        }
    }

    private fun clearFocusAndHideKeyboard(view: View) {
        view.clearFocus()
        hideKeyboard(view)
    }

    private fun hideKeyboard(view: View) {
        inputMethodManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS,
        )
    }

    companion object {
        private const val INTERVAL_MILLIS = 10000L
        private const val REQUEST_CODE_LOCATION = 100
        private const val DEFAULT_MAP_PADDING = 0
        private const val DEFAULT_ZOOM = 15f
        private const val SEOUL_STATION_LATITUDE = 37.554677038139815
        private const val SEOUL_STATION_LONGITUDE = 126.97061201084968
        private const val BOTTOM_SHEET_STATE_REQUEST_KEY = "requestKey"
        private const val BOTTOM_SHEET_NEW_STATE = "newState"
    }
}
