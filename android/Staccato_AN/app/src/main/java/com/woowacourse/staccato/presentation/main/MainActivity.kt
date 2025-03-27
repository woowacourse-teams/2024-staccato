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
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.memorycreation.MemoryCreationActivity
import com.woowacourse.staccato.presentation.moment.MomentFragment.Companion.MOMENT_ID_KEY
import com.woowacourse.staccato.presentation.momentcreation.MomentCreationActivity
import com.woowacourse.staccato.presentation.momentcreation.MomentCreationActivity.Companion.MEMORY_TITLE_KEY
import com.woowacourse.staccato.presentation.util.showToast

class MainActivity : BindingActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by viewModels()

    private val memoryCreationLauncher =
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
                    val memoryId = it.getLongExtra(MEMORY_ID_KEY, 0L)
                    val memoryTitle = it.getStringExtra(MEMORY_TITLE_KEY)
                    val bundle =
                        bundleOf(
                            MOMENT_ID_KEY to createdVisitId,
                            MEMORY_ID_KEY to memoryId,
                            MEMORY_TITLE_KEY to memoryTitle,
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
                    val memoryId = it.getLongExtra(MEMORY_ID_KEY, 0L)
                    val memoryTitle = it.getStringExtra(MEMORY_TITLE_KEY)
                    val bundle =
                        bundleOf(
                            MOMENT_ID_KEY to updatedVisitId,
                            MEMORY_TITLE_KEY to memoryTitle,
                            MEMORY_ID_KEY to memoryId,
                        )
                    navigateTo(R.id.momentFragment, R.id.momentFragment, bundle, true)
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

    private fun setupBottomSheetNavigation() {
        binding.btnMainMemoryCreation.setOnClickListener {
            MemoryCreationActivity.startWithResultLauncher(
                this,
                memoryCreationLauncher,
            )
        }
        binding.btnMainVisitCreation.setOnClickListener {
            // TODO : 현재 날짜, 시간을 기준으로 여행이 있으면 메인 -> 방문 기록 생성 플로우 구현
            MomentCreationActivity.startWithResultLauncher(
                1,
                "임시 추억",
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
