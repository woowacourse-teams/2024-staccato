package com.on.staccato.presentation.timeline

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.on.staccato.R
import com.on.staccato.databinding.FragmentTimelineBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_ID_KEY
import com.on.staccato.presentation.categorycreation.CategoryCreationActivity
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.timeline.model.SortType
import com.on.staccato.presentation.timeline.viewmodel.TimelineViewModel
import com.on.staccato.presentation.util.MenuHandler
import com.on.staccato.presentation.util.showPopupMenu
import com.on.staccato.presentation.util.showToast
import com.on.staccato.util.logging.AnalyticsEvent.Companion.NAME_FRAGMENT_PAGE
import com.on.staccato.util.logging.LoggingManager
import com.on.staccato.util.logging.Param
import com.on.staccato.util.logging.Param.Companion.KEY_FRAGMENT_NAME
import com.on.staccato.util.logging.Param.Companion.PARAM_CATEGORY_LIST
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimelineFragment :
    BindingFragment<FragmentTimelineBinding>(R.layout.fragment_timeline),
    TimelineHandler,
    MenuHandler {
    @Inject
    lateinit var loggingManager: LoggingManager

    private val timelineViewModel: TimelineViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        setupBinding()
        setUpObserving()
        logAccess()
    }

    override fun onCategoryClicked(categoryId: Long) {
        val bundle: Bundle = bundleOf(CATEGORY_ID_KEY to categoryId)
        navigateToCategory(bundle)
    }

    override fun onCategoryCreationClicked() {
        val categoryCreationLauncher = (activity as MainActivity).categoryCreationLauncher
        CategoryCreationActivity.startWithResultLauncher(
            requireActivity(),
            categoryCreationLauncher,
        )
    }

    override fun onSortClicked() {
        val sortButton = binding.linearTimelineSortCategories
        sortButton.showPopupMenu(
            R.menu.menu_sort,
            menuHandler = ::setupActionBy,
        )
    }

    override fun setupActionBy(menuItemId: Int) {
        val sortType: SortType = SortType.from(menuItemId)
        timelineViewModel.sortTimeline(sortType)
    }

    override fun onFilterClicked() {
        timelineViewModel.changeFilterState()
    }

    override fun onChangeToHalfClicked() {
        sharedViewModel.updateHalfModeEvent()
    }

    private fun setupBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = timelineViewModel
        binding.handler = this
        binding.cvTimelineCategories.setContent {
            TimelineScreen(sharedViewModel = sharedViewModel) {
                onCategoryClicked(it)
            }
        }
    }

    private fun navigateToCategory(bundle: Bundle) {
        findNavController().navigate(R.id.action_timelineFragment_to_categoryFragment, bundle)
    }

    private fun setUpObserving() {
        timelineViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            showToast(message)
        }

        observeException()
        observeIsRetry()

        sharedViewModel.isTimelineUpdated.observe(viewLifecycleOwner) { isUpdated ->
            if (isUpdated) {
                timelineViewModel.loadTimeline()
            }
        }

        sharedViewModel.memberProfile.observe(viewLifecycleOwner) { memberProfile ->
            binding.nickname = memberProfile.nickname
        }

        observeIsBottomSheetExpanded()
        observeIsAtTop()
    }

    private fun observeIsBottomSheetExpanded() {
        sharedViewModel.isBottomSheetExpanded.observe(viewLifecycleOwner) {
            binding.isBottomSheetExpanded = it
            if (it) {
                sharedViewModel.updateIsDraggable()
                sharedViewModel.updateLatestIsDraggable()
            }
        }
    }

    private fun observeIsAtTop() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.isAtTop.collect {
                    sharedViewModel.updateIsDraggable()
                }
            }
        }
    }

    private fun observeException() {
        timelineViewModel.exception.observe(viewLifecycleOwner) { state ->
            sharedViewModel.updateException(state)
        }
    }

    private fun observeIsRetry() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.retryEvent.collect {
                    timelineViewModel.loadTimeline()
                }
            }
        }
    }

    private fun logAccess() {
        loggingManager.logEvent(
            NAME_FRAGMENT_PAGE,
            Param(KEY_FRAGMENT_NAME, PARAM_CATEGORY_LIST),
        )
    }
}
