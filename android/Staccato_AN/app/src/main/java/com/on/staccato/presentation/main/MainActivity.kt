package com.on.staccato.presentation.main

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
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
import com.on.staccato.R
import com.on.staccato.databinding.ActivityMainBinding
import com.on.staccato.domain.model.MomentLocation
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.LocationPermissionManager
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
    private lateinit var permissionRequestLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val sharedViewModel: SharedViewModel by viewModels()
    private val mapsViewModel: MapsViewModel by viewModels()
    private val locationPermissionManager = LocationPermissionManager(context = this, activity = this)
    private val locationPermissions = locationPermissionManager.locationPermissions

    val memoryCreationLauncher: ActivityResultLauncher<Intent> = handleMemoryResult()
    val memoryUpdateLauncher: ActivityResultLauncher<Intent> = handleMemoryResult()
    val staccatoCreationLauncher: ActivityResultLauncher<Intent> = handleStaccatoResult()
    val staccatoUpdateLauncher: ActivityResultLauncher<Intent> = handleStaccatoResult()

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.handler = this
        setupPermissionRequestLauncher()
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

    override fun onStop() {
        super.onStop()
        sharedViewModel.updateIsSettingClicked(false)
    }

    override fun onStaccatoCreationClicked() {
        MomentCreationActivity.startWithResultLauncher(
            0L,
            "임시 추억",
            this,
            staccatoCreationLauncher,
        )
    }

    private fun setupPermissionRequestLauncher() {
        permissionRequestLauncher =
            locationPermissionManager.requestPermissionLauncher(
                activityResultCaller = this,
                view = binding.root,
                actionWhenHavePermission = ::enableMyLocation,
            )
    }

    private fun setupGoogleMap() {
        val map: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_map) as? SupportMapFragment
        map?.getMapAsync(this)
    }

    private fun checkLocationSetting() {
        locationPermissionManager.checkLocationSetting(actionWhenHavePermission = ::enableMyLocation)
    }

    private fun moveDefaultLocation() {
        val defaultLocation =
            LatLng(SEOUL_STATION_LATITUDE, SEOUL_STATION_LONGITUDE)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM))
    }

    private fun enableMyLocation() {
        val checkSelfLocationPermission = locationPermissionManager.checkSelfLocationPermission()
        val shouldShowRequestLocationPermissionsRationale = locationPermissionManager.shouldShowRequestLocationPermissionsRationale()

        when {
            checkSelfLocationPermission -> {
                googleMap.isMyLocationEnabled = true
                val currentLocation: Task<Location> =
                    fusedLocationProviderClient.getCurrentLocation(
                        PRIORITY_HIGH_ACCURACY,
                        null,
                    )
                mapsViewModel.setCurrentLocation(currentLocation)
            }

            shouldShowRequestLocationPermissionsRationale -> {
                observeIsPermissionCancelClicked {
                    locationPermissionManager.showLocationRequestRationaleDialog(supportFragmentManager)
                }
            }

            else -> {
                observeIsPermissionCancelClicked { permissionRequestLauncher.launch(locationPermissions) }
            }
        }
    }

    private fun observeIsPermissionCancelClicked(requestLocationPermissions: () -> Unit) {
        sharedViewModel.isPermissionCancelClicked.observe(this) { isCancel ->
            if (!isCancel) requestLocationPermissions()
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
            changeSkipCollapsed()
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
                                changeSkipCollapsed(skipCollapsed = false)
                            }

                            STATE_HALF_EXPANDED -> {
                                mapsViewModel.setIsHalf(isHalf = true)
                                currentFocus?.let { clearFocusAndHideKeyboard(it) }
                                changeSkipCollapsed()
                            }

                            STATE_COLLAPSED -> {
                                mapsViewModel.setIsHalf(isHalf = false)
                            }

                            else -> {
                                binding.viewMainDragBar.visibility = View.VISIBLE
                                binding.constraintMainBottomSheet.setBackgroundResource(
                                    R.drawable.shape_bottom_sheet_20dp,
                                )
                                currentFocus?.let { clearFocusAndHideKeyboard(it) }
                            }
                        }
                    }

                    override fun onSlide(
                        view: View,
                        slideOffset: Float,
                    ) {
                        if (slideOffset < 0.05) {
                            changeSkipCollapsed(isHideable = false)
                            state = STATE_COLLAPSED
                        }
                    }
                },
            )
        }
    }

    private fun BottomSheetBehavior<ConstraintLayout>.changeSkipCollapsed(
        isHideable: Boolean = true,
        skipCollapsed: Boolean = true,
    ) {
        this.isHideable = isHideable
        this.skipCollapsed = skipCollapsed
    }

    private fun setUpBottomSheetStateListener() {
        supportFragmentManager.setFragmentResultListener(
            BOTTOM_SHEET_STATE_REQUEST_KEY,
            this,
        ) { _, bundle ->
            val newState = bundle.getInt(BOTTOM_SHEET_NEW_STATE)
            behavior.state = newState
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
        private const val DEFAULT_MAP_PADDING = 0
        private const val DEFAULT_ZOOM = 15f
        private const val SEOUL_STATION_LATITUDE = 37.554677038139815
        private const val SEOUL_STATION_LONGITUDE = 126.97061201084968
        private const val BOTTOM_SHEET_STATE_REQUEST_KEY = "requestKey"
        private const val BOTTOM_SHEET_NEW_STATE = "newState"
    }
}
