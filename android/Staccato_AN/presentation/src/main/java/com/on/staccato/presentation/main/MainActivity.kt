package com.on.staccato.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.presentation.R
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_ID_KEY
import com.on.staccato.presentation.category.model.CategoryUiModel.Companion.DEFAULT_CATEGORY_ID
import com.on.staccato.presentation.categorycreation.CategoryCreationActivity.Companion.KEY_IS_CATEGORY_CREATED
import com.on.staccato.presentation.categoryupdate.CategoryUpdateActivity.Companion.KEY_IS_CATEGORY_UPDATED
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.databinding.ActivityMainBinding
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.mypage.MyPageActivity
import com.on.staccato.presentation.mypage.MyPageActivity.Companion.UPDATED_PROFILE_KEY
import com.on.staccato.presentation.mypage.MyPageActivity.Companion.UPDATED_TIMELINE_KEY
import com.on.staccato.presentation.notification.NotificationPermissionManager
import com.on.staccato.presentation.notification.NotificationPermissionRationale
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.CREATED_STACCATO_KEY
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.DEFAULT_STACCATO_ID
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccatocreation.StaccatoCreationActivity
import com.on.staccato.presentation.staccatoupdate.StaccatoUpdateActivity.Companion.KEY_IS_STACCATO_UPDATED
import com.on.staccato.presentation.util.showSnackBarWithAction
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
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity :
    BindingActivity<ActivityMainBinding>(),
    MainHandler {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    @Inject
    lateinit var loggingManager: LoggingManager

    @Inject
    lateinit var notificationPermissionManager: NotificationPermissionManager

    val categoryCreationLauncher: ActivityResultLauncher<Intent> = handleCategoryCreationResult()
    val categoryUpdateLauncher: ActivityResultLauncher<Intent> = handleCategoryUpdateResult()
    val staccatoCreationLauncher: ActivityResultLauncher<Intent> = handleStaccatoCreationResult()
    val staccatoUpdateLauncher: ActivityResultLauncher<Intent> = handleStaccatoUpdateResult()
    private val myPageLauncher: ActivityResultLauncher<Intent> = handleMyPageResult()

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private var previousBottomSheetStateTime: Long = currentTimeMillis()

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        setupBottomSheetController()
        setUpBottomSheetBehaviorAction()
        setUpBottomSheetStateListener()
        updateBottomSheetIsDraggable()
        setupBackPressedHandler()
        observeMessageEvent()
        observeRetryEvent()
        loadMemberProfile()
        registerAndLaunchNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.fetchNotificationExistence()
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

    override fun onMyPageClicked() {
        MyPageActivity.startWithResultLauncher(this, myPageLauncher)
    }

    override fun onCurrentLocationClicked() {
        sharedViewModel.updateCurrentLocationEvent()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        binding.handler = this
    }

    private fun setupBottomSheetController() {
        behavior =
            BottomSheetBehavior.from(binding.constraintMainBottomSheet)
                .apply { setState(STATE_HALF_EXPANDED) }
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_main_bottom_sheet) as NavHostFragment
        navController = navHostFragment.navController
    }

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
                                sharedViewModel.updateBottomSheetState(isExpanded = true, isHalfExpanded = false)
                                changeSkipCollapsed(skipCollapsed = false)
                                logEventForBottomSheet(stateParam = PARAM_BOTTOM_SHEET_EXPANDED)
                            }

                            STATE_HALF_EXPANDED -> {
                                sharedViewModel.updateBottomSheetState(isExpanded = false, isHalfExpanded = true)
                                currentFocus?.let { clearFocusAndHideKeyboard(it) }
                                changeSkipCollapsed()
                                logEventForBottomSheet(stateParam = PARAM_BOTTOM_SHEET_HALF_EXPANDED)
                            }

                            STATE_COLLAPSED -> {
                                sharedViewModel.updateBottomSheetState(isExpanded = false, isHalfExpanded = false)
                                logEventForBottomSheet(stateParam = PARAM_BOTTOM_SHEET_COLLAPSED)
                            }

                            else -> {
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

    private fun updateBottomSheetIsDraggable() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTimeline = destination.id == R.id.timelineFragment
            behavior.isDraggable = !isTimeline

            if (isTimeline) observeBottomSheetIsDraggable()
        }
    }

    private fun observeBottomSheetIsDraggable() {
        observeIsAtTop()
        observeIsDraggable()
        observeLatestIsDraggable()
        observeIsHalfModeRequested()
    }

    private fun observeIsAtTop() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.isAtTop.collect {
                    sharedViewModel.updateIsDraggable()
                }
            }
        }
    }

    private fun observeIsDraggable() {
        sharedViewModel.isDraggable.observe(this) {
            behavior.isDraggable = it
        }
    }

    private fun observeLatestIsDraggable() {
        sharedViewModel.latestIsDraggable.observe(this) {
            behavior.isDraggable = it
        }
    }

    private fun observeIsHalfModeRequested() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.halfModeEvent.collect {
                    behavior.state = STATE_HALF_EXPANDED
                    behavior.isDraggable = true
                }
            }
        }
    }

    private fun setupBackPressedHandler() {
        var backPressedTime = 0L
        onBackPressedDispatcher.addCallback {
            when (behavior.state) {
                STATE_EXPANDED -> {
                    behavior.state = STATE_HALF_EXPANDED
                    behavior.isDraggable = true
                }
                STATE_HALF_EXPANDED -> {
                    behavior.state = STATE_COLLAPSED
                }
                else -> {
                    handleBackPressedTwice(backPressedTime).also {
                        backPressedTime = it
                    }
                }
            }
        }
    }

    private fun handleBackPressedTwice(backPressedTime: Long): Long {
        val currentTime = currentTimeMillis()
        if (currentTime - backPressedTime >= 3000L) {
            showToast(getString(R.string.main_end))
        } else {
            finish()
        }
        return currentTime
    }

    private fun observeMessageEvent() {
        sharedViewModel.messageEvent.observe(this) { event ->
            when (event) {
                is MessageEvent.ResId -> {
                    showSnackBar(getString(event.value))
                }

                is MessageEvent.Text -> {
                    showToast(event.value)
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        binding.root.showSnackBarWithAction(
            message = message,
            actionLabel = R.string.all_retry,
            onAction = ::onRetryAction,
            length = Snackbar.LENGTH_INDEFINITE,
        )
    }

    private fun onRetryAction() {
        sharedViewModel.updateIsRetry()
    }

    private fun observeRetryEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.retryEvent.collect {
                    sharedViewModel.fetchNotificationExistence()
                }
            }
        }
    }

    private fun loadMemberProfile() {
        sharedViewModel.fetchMemberProfile()
    }

    private fun registerAndLaunchNotificationPermission() {
        notificationPermissionManager.register(
            caller = this,
            activity = this,
            showRationale = ::showNotificationPermissionRationale,
        )
        notificationPermissionManager.launch()
    }

    private fun showNotificationPermissionRationale() {
        binding.cvMainNotificationRationaleDialog.setContent {
            NotificationPermissionRationale(::moveToNotificationSetting)
        }
    }

    private fun moveToNotificationSetting() {
        val intent =
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(intent)
    }

    private fun handleCategoryCreationResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val id = it.getLongExtra(CATEGORY_ID_KEY, DEFAULT_CATEGORY_ID)
                    val isCategoryCreated = it.getBooleanExtra(KEY_IS_CATEGORY_CREATED, false)
                    val bundle = bundleOf(CATEGORY_ID_KEY to id, KEY_IS_CATEGORY_CREATED to isCategoryCreated)
                    navigateTo(R.id.categoryFragment, R.id.timelineFragment, bundle, false)
                }
            }
        }

    private fun handleCategoryUpdateResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val id = it.getLongExtra(CATEGORY_ID_KEY, DEFAULT_CATEGORY_ID)
                    val isCategoryCreated = it.getBooleanExtra(KEY_IS_CATEGORY_UPDATED, false)
                    val bundle = bundleOf(CATEGORY_ID_KEY to id, KEY_IS_CATEGORY_UPDATED to isCategoryCreated)
                    navigateTo(R.id.categoryFragment, R.id.timelineFragment, bundle, false)
                }
            }
        }

    private fun handleStaccatoCreationResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val id = it.getLongExtra(STACCATO_ID_KEY, DEFAULT_STACCATO_ID)
                    val isStaccatoCreated = it.getBooleanExtra(CREATED_STACCATO_KEY, false)
                    val bundle = bundleOf(STACCATO_ID_KEY to id, CREATED_STACCATO_KEY to isStaccatoCreated)
                    navigateTo(R.id.staccatoFragment, R.id.staccatoFragment, bundle, true)
                }
            }
        }

    private fun handleStaccatoUpdateResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val id = it.getLongExtra(STACCATO_ID_KEY, DEFAULT_STACCATO_ID)
                    val isStaccatoCreated = it.getBooleanExtra(KEY_IS_STACCATO_UPDATED, false)
                    val bundle = bundleOf(STACCATO_ID_KEY to id, KEY_IS_STACCATO_UPDATED to isStaccatoCreated)
                    navigateTo(R.id.staccatoFragment, R.id.staccatoFragment, bundle, true)
                }
            }
        }

    private fun handleMyPageResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val hasTimelineUpdated = it.getBooleanExtra(UPDATED_TIMELINE_KEY, false)
                    val hasProfileUpdated = it.getBooleanExtra(UPDATED_PROFILE_KEY, false)
                    if (hasTimelineUpdated) sharedViewModel.updateIsTimelineUpdated(true)
                    if (hasProfileUpdated) loadMemberProfile()
                }
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

    private fun logEventForBottomSheet(stateParam: String) {
        loggingManager.logEvent(
            NAME_BOTTOM_SHEET,
            Param(KEY_BOTTOM_SHEET_STATE, stateParam),
            Param(KEY_BOTTOM_SHEET_DURATION, calculateBottomSheetTimeDuration()),
        )
    }

    private fun calculateBottomSheetTimeDuration(): Double {
        val currentTime = currentTimeMillis()
        val timeDuration = currentTime - previousBottomSheetStateTime
        previousBottomSheetStateTime = currentTime
        return timeDuration / MILLISECONDS_TO_SECONDS
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
        private const val BOTTOM_SHEET_STATE_REQUEST_KEY = "requestKey"
        private const val BOTTOM_SHEET_NEW_STATE = "newState"
        private const val MILLISECONDS_TO_SECONDS = 1000.0

        fun launch(startContext: Context) {
            val intent =
                Intent(startContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            val animationOption =
                ActivityOptionsCompat.makeCustomAnimation(
                    startContext,
                    R.anim.anim_fade_in,
                    R.anim.anim_fade_out,
                )
            startContext.startActivity(intent, animationOption.toBundle())
        }
    }
}
