package com.woowacourse.staccato.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityMainBinding
import com.woowacourse.staccato.presentation.base.BindingActivity

class MainActivity : BindingActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun initStartView(savedInstanceState: Bundle?) {
        setupBottomSheetController()
        setupBottomSheetNavigation()
        setupBackPressedHandler()
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
            Toast.makeText(this@MainActivity, "버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT)
                .show()
        } else {
            finish()
        }
        return currentTime
    }

    private fun setupBottomSheetController() {
        behavior = BottomSheetBehavior.from(binding.constraintBottomSheet)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setupBottomSheetNavigation() {
        val navOptions = buildNavOptions()
        binding.btnTravelCreate.setOnClickListener {
            navigateTo(R.id.travelCreationFragment, navOptions)
        }
        binding.btnVisitCreate.setOnClickListener {
            navigateTo(R.id.visitCreationFragment, navOptions)
        }
        binding.btnTimeline.setOnClickListener {
            navigateTo(R.id.timelineFragment, navOptions)
        }
    }

    private fun buildNavOptions() =
        NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(R.id.timelineFragment, false)
            .build()

    private fun navigateTo(
        navResourceId: Int,
        navOptions: NavOptions,
    ) {
        navController.navigate(navResourceId, null, navOptions)
        behavior.state = STATE_EXPANDED
    }
}
