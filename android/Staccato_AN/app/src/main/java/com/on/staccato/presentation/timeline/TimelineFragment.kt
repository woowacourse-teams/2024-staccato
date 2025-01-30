package com.on.staccato.presentation.timeline

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.FragmentTimelineBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_ID_KEY
import com.on.staccato.presentation.categorycreation.CategoryCreationActivity
import com.on.staccato.presentation.timeline.adapter.TimelineAdapter
import com.on.staccato.presentation.timeline.model.SortType
import com.on.staccato.presentation.timeline.viewmodel.TimelineViewModel
import com.on.staccato.presentation.util.showSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimelineFragment :
    BindingFragment<FragmentTimelineBinding>(R.layout.fragment_timeline),
    TimelineHandler {
    private val timelineViewModel: TimelineViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var adapter: TimelineAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        setupBinding()
        setUpAdapter()
        setUpObserving()
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
        val sortButton = binding.btnTimelineSortCategories
        val popup = sortButton.inflateCreationMenu()
        setUpCreationMenu(popup)
        popup.show()
    }

    private fun setupBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = timelineViewModel
        binding.handler = this
    }

    private fun navigateToCategory(bundle: Bundle) {
        findNavController().navigate(R.id.action_timelineFragment_to_categoryFragment, bundle)
    }

    private fun setUpAdapter() {
        adapter = TimelineAdapter(this)
        binding.rvTimeline.adapter = adapter
    }

    private fun setUpObserving() {
        timelineViewModel.timeline.observe(viewLifecycleOwner) { timeline ->
            adapter.updateTimeline(timeline) {
                binding.rvTimeline.scrollToPosition(0)
            }
        }

        timelineViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            showToast(message)
        }

        timelineViewModel.exceptionMessage.observe(viewLifecycleOwner) { message ->
            view?.showSnackBarWithAction(
                message = message,
                actionLabel = R.string.all_retry,
                onAction = ::onRetryAction,
                Snackbar.LENGTH_INDEFINITE,
            )
        }

        sharedViewModel.isTimelineUpdated.observe(viewLifecycleOwner) { isUpdated ->
            if (isUpdated) {
                timelineViewModel.loadTimeline()
            }
        }
    }

    private fun View.inflateCreationMenu(): PopupMenu {
        val popup =
            PopupMenu(
                this.context,
                this,
                Gravity.END,
                0,
                R.style.Theme_Staccato_AN_PopupMenu,
            )
        popup.menuInflater.inflate(R.menu.menu_sort, popup.menu)
        return popup
    }

    private fun setUpCreationMenu(popup: PopupMenu) {
        popup.setOnMenuItemClickListener { menuItem ->
            val sortType: SortType = SortType.from(menuItem.itemId)
            timelineViewModel.sortTimeline(sortType)
            false
        }
    }

    private fun onRetryAction() {
        timelineViewModel.loadTimeline()
    }
}
