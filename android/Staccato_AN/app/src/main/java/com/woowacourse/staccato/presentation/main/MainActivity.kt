package com.woowacourse.staccato.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
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
import com.woowacourse.staccato.presentation.travelcreation.TravelCreationActivity
import com.woowacourse.staccato.presentation.visitcreation.VisitCreationActivity

class MainActivity : BindingActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private val travelCreationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    Toast.makeText(this, "새로운 여행을 만들었어요!", Toast.LENGTH_SHORT).show()
                    navigateTo(R.id.travelFragment, R.id.timelineFragment)
                }
            }
        }

    val travelUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    Toast.makeText(this, "여행을 수정했어요!", Toast.LENGTH_SHORT).show()
                    navigateTo(R.id.travelFragment, R.id.timelineFragment)
                }
            }
        }

    private val visitCreationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    Toast.makeText(this, "새로운 방문 기록을 만들었어요!", Toast.LENGTH_SHORT).show()
                    navigateTo(R.id.visitFragment, R.id.visitFragment)
                }
            }
        }

    val visitUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    Toast.makeText(this, "방문 기록을 수정했어요!", Toast.LENGTH_SHORT).show()
                    navigateTo(R.id.visitFragment, R.id.visitFragment)
                }
            }
        }

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
        binding.btnTravelCreate.setOnClickListener {
            TravelCreationActivity.startWithResultLauncher(
                this,
                travelCreationLauncher,
            )
        }
        binding.btnVisitCreate.setOnClickListener {
            VisitCreationActivity.startWithResultLauncher(
                this,
                visitCreationLauncher,
            )
        }
        binding.btnTimeline.setOnClickListener {
            navigateTo(R.id.timelineFragment, R.id.timelineFragment)
        }
    }

    private fun navigateTo(
        navigateToId: Int,
        popUpToId: Int,
    ) {
        val navOptions = buildNavOptions(popUpToId)
        navController.navigate(navigateToId, null, navOptions)
        behavior.state = STATE_EXPANDED
    }

    private fun buildNavOptions(popUpToId: Int) =
        NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(popUpToId, false)
            .build()
}
