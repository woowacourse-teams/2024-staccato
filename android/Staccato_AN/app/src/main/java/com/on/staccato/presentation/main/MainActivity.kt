package com.on.staccato.presentation.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.ActivityMainBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.maps.MapsFragment
import com.on.staccato.presentation.maps.MapsFragment.Companion.BOTTOM_SHEET_NEW_STATE
import com.on.staccato.presentation.maps.MapsFragment.Companion.BOTTOM_SHEET_STATE_REQUEST_KEY
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.moment.MomentFragment.Companion.MOMENT_ID_KEY
import com.on.staccato.presentation.momentcreation.MomentCreationActivity
import com.on.staccato.presentation.util.showToast

class MainActivity :
    BindingActivity<ActivityMainBinding>(),
    OnMapReadyCallback,
    MainHandler {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private lateinit var map: GoogleMap
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
    private val inputManager: InputMethodManager by lazy {
        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private val requestPermissionLauncher = initRequestPermissionsLauncher()

    val memoryCreationLauncher: ActivityResultLauncher<Intent> = handleMemoryResult(messageId = R.string.main_memory_creation_success)
    val memoryUpdateLauncher: ActivityResultLauncher<Intent> = handleMemoryResult(messageId = R.string.main_memory_update_success)
    val staccatoCreationLauncher: ActivityResultLauncher<Intent> = handleStaccatoResult(messageId = R.string.main_moment_creation_success)
    val staccatoUpdateLauncher: ActivityResultLauncher<Intent> = handleStaccatoResult(messageId = R.string.main_moment_update_success)

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.handler = this
        setupGoogleMap()
        setupFusedLocationProviderClient()
        setupBottomSheetController()
        setupBackPressedHandler()
        setUpBottomSheetBehaviorAction()
        setUpBottomSheetStateListener()
    }

    override fun onResume() {
        super.onResume()
        if (this::map.isInitialized) enableMyLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
    }

    override fun onStaccatoCreationClicked() {
        MomentCreationActivity.startWithResultLauncher(
            0L,
            "임시 추억",
            this,
            staccatoCreationLauncher,
        )
    }

    // TODO: 하드 코딩된 String 제거
    private fun initRequestPermissionsLauncher() =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { (_, isGranted) ->
                if (isGranted) {
                    makeSnackBar(
                        getString(R.string.maps_location_permission_granted_message),
                    ).show()
                    enableMyLocation()
                } else {
                    makeSnackBar(
                        "위치 권한을 거부하셨습니다.",
                    ).show()
                }
            }
        }

    private fun setupGoogleMap() {
        val map: SupportMapFragment? = supportFragmentManager.findFragmentById(R.id.fragment_container_view_map) as? SupportMapFragment
        map?.getMapAsync(this)
    }

    private fun setupFusedLocationProviderClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun enableMyLocation() {
        when {
            checkSelfLocationPermission() -> {
                map.isMyLocationEnabled = true
                val currentLocation =
                    fusedLocationProviderClient.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        null,
                    )
                currentLocation.addOnSuccessListener { location ->
                    moveCamera(map, location)
                }
            }
            shouldShowRequestLocationPermissionsRationale() -> {
                sharedViewModel.isLocationDenial.observe(this) { isCancel ->
                    if (!isCancel) showPermissionRequestDialog()
                }
            }
            else -> {
                sharedViewModel.isLocationDenial.observe(this) { isCancel ->
                    if (!isCancel) requestPermissionLauncher.launch(locationPermissions)
                }
            }
        }
    }

    private fun moveCamera(
        googleMap: GoogleMap,
        location: Location?,
    ) {
        if (location != null) {
            val currentLocation = LatLng(location.latitude, location.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
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

    // TODO: CustomDialog 로 변경, 하드 코딩된 String 제거
    private fun showPermissionRequestDialog() {
        val dialog =
            MaterialAlertDialogBuilder(this)
                .setTitle("위치 권한 설정")
                .setMessage(getString(R.string.maps_location_permission_required_message))
                .setNegativeButton("취소") { dialog, _ ->
                    sharedViewModel.updateIsLocationDenial()
                    dialog.dismiss()
                }
                .setPositiveButton("설정") { dialog, _ ->
                    navigateToSetting()
                    dialog.dismiss()
                }
        dialog.show()
    }

    private fun navigateToSetting() {
        val uri = Uri.fromParts(MapsFragment.PACKAGE_SCHEME, this.packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
        startActivity(intent)
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

    private fun handleMemoryResult(messageId: Int) =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    sharedViewModel.setTimelineHasUpdated()
                    showToast(getString(messageId))
                    val bundle: Bundle = makeBundle(it, MEMORY_ID_KEY)
                    navigateTo(R.id.memoryFragment, R.id.timelineFragment, bundle, false)
                }
            }
        }

    private fun handleStaccatoResult(messageId: Int) =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    showToast(getString(messageId))
                    val bundle: Bundle = makeBundle(it, MOMENT_ID_KEY)
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

                            else -> {
                                binding.viewMainDragBar.visibility = View.VISIBLE
                                binding.constraintMainBottomSheet.setBackgroundResource(
                                    R.drawable.shape_bottom_sheet_20dp,
                                )
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
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (!isTouchInsideView(event, view)) {
                    clearFocusAndHideKeyboard(view)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun isTouchInsideView(
        event: MotionEvent,
        view: View,
    ): Boolean {
        val rect = android.graphics.Rect()
        view.getGlobalVisibleRect(rect)
        return rect.contains(event.rawX.toInt(), event.rawY.toInt())
    }

    private fun clearFocusAndHideKeyboard(view: View) {
        view.clearFocus()
        hideKeyboard(view)
    }

    private fun hideKeyboard(view: View) {
        inputManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS,
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
