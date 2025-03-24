package com.on.staccato.presentation.main

import android.content.Intent
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.on.staccato.R
import com.on.staccato.databinding.ActivityMainBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_ID_KEY
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.mypage.MyPageActivity
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.CREATED_STACCATO_KEY
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccatocreation.StaccatoCreationActivity
import com.on.staccato.presentation.util.showToast
import com.on.staccato.util.logging.AnalyticsEvent.Companion.NAME_BOTTOM_SHEET
import com.on.staccato.util.logging.AnalyticsEvent.Companion.NAME_STACCATO_CREATION
import com.on.staccato.util.logging.LoggingManager
import com.on.staccato.util.logging.Param
import com.on.staccato.util.logging.Param.Companion.KEY_BOTTOM_SHEET_DURATION
import com.on.staccato.util.logging.Param.Companion.KEY_BOTTOM_SHEET_STATE
import com.on.staccato.util.logging.Param.Companion.KEY_IS_CREATED_IN_MAIN
import com.on.staccato.util.logging.Param.Companion.PARAM_BOTTOM_SHEET_COLLAPSED
import com.on.staccato.util.logging.Param.Companion.PARAM_BOTTOM_SHEET_EXPANDED
import com.on.staccato.util.logging.Param.Companion.PARAM_BOTTOM_SHEET_HALF_EXPANDED
import dagger.hilt.android.AndroidEntryPoint
import java.lang.System.currentTimeMillis
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity :
    BindingActivity<ActivityMainBinding>(),
    MainHandler {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    @Inject
    lateinit var loggingManager: LoggingManager
    private var previousBottomSheetStateTime: Long = currentTimeMillis()

    private val sharedViewModel: SharedViewModel by viewModels()

    val categoryCreationLauncher: ActivityResultLauncher<Intent> = handleCategoryResult()
    val categoryUpdateLauncher: ActivityResultLauncher<Intent> = handleCategoryResult()
    val staccatoCreationLauncher: ActivityResultLauncher<Intent> = handleStaccatoResult()
    val staccatoUpdateLauncher: ActivityResultLauncher<Intent> = handleStaccatoResult()
    private val myPageLauncher: ActivityResultLauncher<Intent> = handleMyPageResult()

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        loadMemberProfile()
        observeStaccatoId()
        setupBottomSheetController()
        setupBackPressedHandler()
        setUpBottomSheetBehaviorAction()
        setUpBottomSheetStateListener()
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.updateIsSettingClicked(false)
    }

    override fun onStaccatoCreationClicked(isPermissionCanceled: Boolean) {
        loggingManager.logEvent(
            NAME_STACCATO_CREATION,
            Param(KEY_IS_CREATED_IN_MAIN, true),
        )
        StaccatoCreationActivity.startWithResultLauncher(
            context = this,
            activityLauncher = staccatoCreationLauncher,
            isPermissionCanceled,
        )
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        binding.handler = this
    }

    override fun onMyPageClicked() {
        MyPageActivity.startWithResultLauncher(this, myPageLauncher)
    }

    private fun loadMemberProfile() {
        sharedViewModel.fetchMemberProfile()
    }

    private fun observeStaccatoId() {
        sharedViewModel.staccatoId.observe(this) { staccatoId ->
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
                .setPopUpTo(R.id.staccatoFragment, true)
                .build()
        val bundle =
            bundleOf(STACCATO_ID_KEY to staccatoId)

        navController.navigate(R.id.staccatoFragment, bundle, navOptions)
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

    private fun handleCategoryResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    sharedViewModel.setTimelineHasUpdated()
                    val bundle: Bundle = makeBundle(it, CATEGORY_ID_KEY)
                    navigateTo(R.id.categoryFragment, R.id.timelineFragment, bundle, false)
                }
            }
        }

    private fun handleStaccatoResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val id = it.getLongExtra(STACCATO_ID_KEY, 0L)
                    val isStaccatoCreated = it.getBooleanExtra(CREATED_STACCATO_KEY, false)
                    val bundle = bundleOf(STACCATO_ID_KEY to id, CREATED_STACCATO_KEY to isStaccatoCreated)
                    navigateTo(R.id.staccatoFragment, R.id.staccatoFragment, bundle, true)
                }
            }
        }

    private fun handleMyPageResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadMemberProfile()
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
                                sharedViewModel.setIsBottomSheetHalf(false)
                                binding.viewMainDragBar.visibility =
                                    View.INVISIBLE
                                binding.constraintMainBottomSheet.setBackgroundResource(
                                    R.drawable.shape_bottom_sheet_square,
                                )
                                changeSkipCollapsed(skipCollapsed = false)
                                loggingManager.logEvent(
                                    NAME_BOTTOM_SHEET,
                                    Param(KEY_BOTTOM_SHEET_STATE, PARAM_BOTTOM_SHEET_EXPANDED),
                                    Param(KEY_BOTTOM_SHEET_DURATION, calculateBottomSheetTimeDuration()),
                                )
                            }

                            STATE_HALF_EXPANDED -> {
                                sharedViewModel.setIsBottomSheetHalf(true)
                                currentFocus?.let { clearFocusAndHideKeyboard(it) }
                                changeSkipCollapsed()
                                loggingManager.logEvent(
                                    NAME_BOTTOM_SHEET,
                                    Param(KEY_BOTTOM_SHEET_STATE, PARAM_BOTTOM_SHEET_HALF_EXPANDED),
                                    Param(KEY_BOTTOM_SHEET_DURATION, calculateBottomSheetTimeDuration()),
                                )
                            }

                            STATE_COLLAPSED -> {
                                sharedViewModel.setIsBottomSheetHalf(false)
                                loggingManager.logEvent(
                                    NAME_BOTTOM_SHEET,
                                    Param(KEY_BOTTOM_SHEET_STATE, PARAM_BOTTOM_SHEET_COLLAPSED),
                                    Param(KEY_BOTTOM_SHEET_DURATION, calculateBottomSheetTimeDuration()),
                                )
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
                        sharedViewModel.updateIsDragging(state == STATE_DRAGGING)
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

    private fun calculateBottomSheetTimeDuration(): Double {
        val currentTime = currentTimeMillis()
        val timeDuration = currentTime - previousBottomSheetStateTime
        previousBottomSheetStateTime = currentTime
        return timeDuration / MILLISECONDS_TO_SECONDS
    }

    companion object {
        private const val BOTTOM_SHEET_STATE_REQUEST_KEY = "requestKey"
        private const val BOTTOM_SHEET_NEW_STATE = "newState"
        private const val MILLISECONDS_TO_SECONDS = 1000.0
    }
}
