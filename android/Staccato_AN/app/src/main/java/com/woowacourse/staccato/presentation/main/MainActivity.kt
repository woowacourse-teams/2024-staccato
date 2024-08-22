package com.woowacourse.staccato.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityMainBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.maps.MapsFragment.Companion.BOTTOM_SHEET_NEW_STATE
import com.woowacourse.staccato.presentation.maps.MapsFragment.Companion.BOTTOM_SHEET_STATE_REQUEST_KEY
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.memorycreation.MemoryCreationActivity
import com.woowacourse.staccato.presentation.moment.MomentFragment.Companion.MOMENT_ID_KEY
import com.woowacourse.staccato.presentation.momentcreation.MomentCreationActivity
import com.woowacourse.staccato.presentation.util.showToast

class MainActivity : BindingActivity<ActivityMainBinding>(), MainHandler {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val sharedViewModel: SharedViewModel by viewModels()

    val memoryCreationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    sharedViewModel.setTimelineHasUpdated()
                    showToast(getString(R.string.main_memory_creation_success))
                    val createdMemoryId = it.getLongExtra(MEMORY_ID_KEY, 0L)
                    val bundle = bundleOf(MEMORY_ID_KEY to createdMemoryId)
                    navigateTo(R.id.memoryFragment, R.id.timelineFragment, bundle, false)
                }
            }
        }

    val memoryUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    sharedViewModel.setTimelineHasUpdated()
                    showToast(getString(R.string.main_memory_update_success))
                    val updatedMemoryId = it.getLongExtra(MEMORY_ID_KEY, 0L)
                    val bundle = bundleOf(MEMORY_ID_KEY to updatedMemoryId)
                    navigateTo(R.id.memoryFragment, R.id.timelineFragment, bundle, false)
                }
            }
        }

    val visitCreationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    showToast(getString(R.string.main_moment_creation_success))
                    val createdVisitId = it.getLongExtra(MOMENT_ID_KEY, 0L)
                    val bundle =
                        bundleOf(
                            MOMENT_ID_KEY to createdVisitId,
                        )
                    navigateTo(R.id.momentFragment, R.id.momentFragment, bundle, true)
                }
            }
        }

    val visitUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    showToast(getString(R.string.main_moment_update_success))
                    val updatedVisitId = it.getLongExtra(MOMENT_ID_KEY, 0L)
                    val bundle =
                        bundleOf(
                            MOMENT_ID_KEY to updatedVisitId,
                        )
                    navigateTo(R.id.momentFragment, R.id.momentFragment, bundle, true)
                }
            }
        }

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.handler = this
        setupBottomSheetController()
        setupBackPressedHandler()
        setUpBottomSheetBehaviorAction()
        setUpBottomSheetStateListener()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreationClicked() {
        val popup = inflateCreationMenu(binding.ivMainCreation)
        setUpCreationMenu(popup)
        popup.show()
    }

    private fun inflateCreationMenu(view: View): PopupMenu {
        val popup = PopupMenu(this, view, Gravity.END, 0, R.style.Theme_Staccato_AN_PopupMenu)
        popup.menuInflater.inflate(R.menu.menu_creation, popup.menu)
        return popup
    }

    private fun setUpCreationMenu(popup: PopupMenu) {
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.memory_creation -> navigateToMemoryCreation()
                R.id.staccato_creation -> navigateToStaccatoCreation()
            }
            false
        }
    }

    private fun navigateToStaccatoCreation() {
        val isAccessFineLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        val isAccessCoarseLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        if (isAccessFineLocationGranted || isAccessCoarseLocationGranted) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                // TODO: 역지오코딩, 위경도 올바른지 확인하기
                Log.d("hye", "latitude : ${location.latitude}, longitude: ${location.longitude}")
                MomentCreationActivity.startWithResultLauncher(
                    0L,
                    "임시 추억",
                    this,
                    visitCreationLauncher,
                )
                // TODO : 현재 날짜, 시간을 기준으로 여행이 있으면 메인 -> 방문 기록 생성 플로우 구현
            }
            return
        } else {
            // requestPermission.launch(locationPermissions)
        }
    }

    private fun navigateToMemoryCreation() {
        MemoryCreationActivity.startWithResultLauncher(
            this,
            memoryCreationLauncher,
        )
    }

    private fun setupBackPressedHandler() {
        var backPressedTime = 0L
        onBackPressedDispatcher.addCallback {
            if (behavior.state == STATE_EXPANDED) {
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
        behavior = BottomSheetBehavior.from(binding.constraintMainBottomSheet)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_main_bottom_sheet) as NavHostFragment
        navController = navHostFragment.navController
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
                    ) { }

                    override fun onSlide(
                        bottomSheet: View,
                        slideOffset: Float,
                    ) {
                        binding.tvMainBottomSheetRemindYourMemories.alpha = 1 - slideOffset
                    }
                },
            )
        }
    }

    private fun setUpBottomSheetStateListener() {
        supportFragmentManager.setFragmentResultListener(BOTTOM_SHEET_STATE_REQUEST_KEY, this) { _, bundle ->
            val newState = bundle.getInt(BOTTOM_SHEET_NEW_STATE)
            behavior.state = newState
        }
    }
}
