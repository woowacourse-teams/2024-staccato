package com.woowacourse.staccato.presentation.main

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import com.woowacourse.staccato.presentation.memorycreation.TravelCreationActivity
import com.woowacourse.staccato.presentation.timeline.TimelineFragment.Companion.TRAVEL_ID_KEY
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visit.VisitFragment.Companion.VISIT_ID_KEY
import com.woowacourse.staccato.presentation.visitcreation.VisitCreationActivity
import com.woowacourse.staccato.presentation.visitcreation.VisitCreationActivity.Companion.TRAVEL_TITLE_KEY

class MainActivity : BindingActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by viewModels()

    private val travelCreationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    sharedViewModel.setTimelineHasUpdated()
                    showToast("새로운 여행을 만들었어요!")
                    val createdTravelId = it.getLongExtra(TRAVEL_ID_KEY, 0L)
                    val bundle = bundleOf(TRAVEL_ID_KEY to createdTravelId)
                    navigateTo(R.id.travelFragment, R.id.timelineFragment, bundle, false)
                }
            }
        }

    val travelUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    sharedViewModel.setTimelineHasUpdated()
                    showToast("여행을 수정했어요!")
                    val updatedTravelId = it.getLongExtra(TRAVEL_ID_KEY, 0L)
                    val bundle = bundleOf(TRAVEL_ID_KEY to updatedTravelId)
                    navigateTo(R.id.travelFragment, R.id.timelineFragment, bundle, false)
                }
            }
        }

    val visitCreationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    showToast("새로운 방문 기록을 만들었어요!")
                    val createdVisitId = it.getLongExtra(VISIT_ID_KEY, 0L)
                    val travelId = it.getLongExtra(TRAVEL_ID_KEY, 0L)
                    val travelTitle = it.getStringExtra(TRAVEL_TITLE_KEY)
                    val bundle =
                        bundleOf(
                            VISIT_ID_KEY to createdVisitId,
                            TRAVEL_ID_KEY to travelId,
                            TRAVEL_TITLE_KEY to travelTitle,
                        )
                    navigateTo(R.id.visitFragment, R.id.visitFragment, bundle, true)
                }
            }
        }

    val visitUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    showToast("방문 기록을 수정했어요!")
                    val updatedVisitId = it.getLongExtra(VISIT_ID_KEY, 0L)
                    val travelId = it.getLongExtra(TRAVEL_ID_KEY, 0L)
                    val travelTitle = it.getStringExtra(TRAVEL_TITLE_KEY)
                    val bundle =
                        bundleOf(
                            VISIT_ID_KEY to updatedVisitId,
                            TRAVEL_TITLE_KEY to travelTitle,
                            TRAVEL_ID_KEY to travelId,
                        )
                    navigateTo(R.id.visitFragment, R.id.visitFragment, bundle, true)
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
        behavior = BottomSheetBehavior.from(binding.constraintMainBottomSheet)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_main_bottom_sheet) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setupBottomSheetNavigation() {
        binding.btnMainTravelCreation.setOnClickListener {
            TravelCreationActivity.startWithResultLauncher(
                this,
                travelCreationLauncher,
            )
        }
        binding.btnMainVisitCreation.setOnClickListener {
            // TODO : 현재 날짜, 시간을 기준으로 여행이 있으면 메인 -> 방문 기록 생성 플로우 구현
            VisitCreationActivity.startWithResultLauncher(
                1,
                "임시 여행",
                this,
                visitCreationLauncher,
            )
        }
        binding.btnMainTimeline.setOnClickListener {
            navigateTo(R.id.timelineFragment, R.id.timelineFragment, null, false)
        }
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
                                binding.btnMainTimeline.visibility = View.INVISIBLE
                            }

                            else -> {
                                binding.btnMainTimeline.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onSlide(
                        bottomSheet: View,
                        slideOffset: Float,
                    ) {
                        binding.tvMainBottomSheetRemindYourMemories.alpha = 1 - slideOffset
                        binding.btnMainTimeline.alpha = 1 - slideOffset
                    }
                },
            )
        }
    }
}
