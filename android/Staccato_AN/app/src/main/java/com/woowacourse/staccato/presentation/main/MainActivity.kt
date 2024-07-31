package com.woowacourse.staccato.presentation.main

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityMainBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.timeline.TimelineFragment.Companion.TRAVEL_ID_KEY
import com.woowacourse.staccato.presentation.travelcreation.TravelCreationActivity
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visit.VisitFragment
import com.woowacourse.staccato.presentation.visitcreation.VisitCreationActivity
import com.woowacourse.staccato.presentation.visitcreation.VisitCreationActivity.Companion.EXTRA_VISIT_ID

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
                    showToast("새로운 여행을 만들었어요!")
                    val createdTravelId = it.getLongExtra(TRAVEL_ID_KEY, 0L)
                    val bundle = bundleOf(TRAVEL_ID_KEY to createdTravelId)
                    navigateTo(R.id.travelFragment, R.id.timelineFragment, bundle)
                }
            }
        }

    val travelUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    showToast("여행을 수정했어요!")
                    navigateTo(R.id.travelFragment, R.id.timelineFragment, null)
                }
            }
        }

    private val visitCreationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    showToast("새로운 방문 기록을 만들었어요!")
                    val createdVisitId = it.getLongExtra(EXTRA_VISIT_ID, 0L)
                    val bundle = bundleOf(VisitFragment.VISIT_ID_KEY to createdVisitId)
                    navigateTo(R.id.visitFragment, R.id.visitFragment, bundle)
                }
            }
        }

    val visitUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    showToast("방문 기록을 수정했어요!")
                    navigateTo(R.id.visitFragment, R.id.visitFragment, null)
                }
            }
        }

    override fun initStartView(savedInstanceState: Bundle?) {
        setupBottomSheetController()
        setupBottomSheetNavigation()
        setupBackPressedHandler()
        setUpBottomSheetBehaviorAction()
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
            showToast("버튼을 한 번 더 누르면 종료됩니다.")
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
            navigateTo(R.id.timelineFragment, R.id.timelineFragment, null)
        }
    }

    private fun navigateTo(
        navigateToId: Int,
        popUpToId: Int,
        bundle: Bundle?,
    ) {
        val navOptions = buildNavOptions(popUpToId)
        navController.navigate(navigateToId, bundle, navOptions)
        behavior.state = STATE_EXPANDED
    }

    private fun buildNavOptions(popUpToId: Int) =
        NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(popUpToId, false)
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
                                binding.btnTimeline.visibility = View.INVISIBLE
                            }

                            else -> {
                                binding.btnTimeline.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onSlide(
                        bottomSheet: View,
                        slideOffset: Float,
                    ) {
                        binding.tvBottomSheetRemindYourMemories.alpha = 1 - slideOffset
                        binding.btnTimeline.alpha = 1 - slideOffset
                    }
                },
            )
        }
    }
}
