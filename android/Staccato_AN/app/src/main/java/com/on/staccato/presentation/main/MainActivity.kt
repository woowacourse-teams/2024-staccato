package com.on.staccato.presentation.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.on.staccato.R
import com.on.staccato.databinding.ActivityMainBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.maps.MapsFragment.Companion.BOTTOM_SHEET_NEW_STATE
import com.on.staccato.presentation.maps.MapsFragment.Companion.BOTTOM_SHEET_STATE_REQUEST_KEY
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.moment.MomentFragment.Companion.MOMENT_ID_KEY
import com.on.staccato.presentation.momentcreation.MomentCreationActivity
import com.on.staccato.presentation.util.showToast

class MainActivity :
    BindingActivity<ActivityMainBinding>(),
    OnMapReadyCallback,
    OnRequestPermissionsResultCallback,
    MainHandler {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private lateinit var map: GoogleMap
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val locationPermissions: Array<String> =
        arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
        )
    private val sharedViewModel: SharedViewModel by viewModels()
    private val inputManager: InputMethodManager by lazy {
        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    val memoryCreationLauncher: ActivityResultLauncher<Intent> = handleMemoryResult(messageId = R.string.main_memory_creation_success)
    val memoryUpdateLauncher: ActivityResultLauncher<Intent> = handleMemoryResult(messageId = R.string.main_memory_update_success)
    val staccatoCreationLauncher: ActivityResultLauncher<Intent> = handleStaccatoResult(messageId = R.string.main_moment_creation_success)
    val staccatoUpdateLauncher: ActivityResultLauncher<Intent> = handleStaccatoResult(messageId = R.string.main_moment_update_success)

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.handler = this
        setupGoogleMap()
        setupBottomSheetController()
        setupBackPressedHandler()
        setUpBottomSheetBehaviorAction()
        setUpBottomSheetStateListener()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty()) &&
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                ) {
                    enableMyLocation()
                }
                return
            }
        }
    }

    override fun onStaccatoCreationClicked() {
        MomentCreationActivity.startWithResultLauncher(
            0L,
            "임시 추억",
            this,
            staccatoCreationLauncher,
        )
    }

    private fun enableMyLocation() {
        if (checkLocationPermissions()) return
        if (shouldShowRequestPermission()) return
        requestLocationPermissions()
    }

    // 권한이 이미 부여된 경우
    private fun checkLocationPermissions(): Boolean {
        val isFineLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        val isCoarseLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        if (isFineLocationGranted || isCoarseLocationGranted) {
            map.isMyLocationEnabled = true
            return true
        } else {
            requestLocationPermissions()
        }
        return false
    }

    // 앱에서 요청 권한 근거를 표시해야 한다고 판단하는 경우
    private fun shouldShowRequestPermission(): Boolean {
        val shouldRequestFineLocation: Boolean =
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                ACCESS_FINE_LOCATION,
            )

        val shouldRequestCoarseLocation: Boolean =
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                ACCESS_COARSE_LOCATION,
            )

        if (shouldRequestFineLocation || shouldRequestCoarseLocation) {
            requestLocationPermissions()
            return true
        }
        return false
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            locationPermissions,
            LOCATION_PERMISSION_REQUEST_CODE,
        )
    }

    private fun setupGoogleMap() {
        val map: SupportMapFragment? = supportFragmentManager.findFragmentById(R.id.fragment_container_view_map) as? SupportMapFragment
        map?.getMapAsync(this)
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
