package com.on.staccato.presentation.timeline

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.on.staccato.R
import com.on.staccato.databinding.FragmentTimelineBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.memorycreation.MemoryCreationActivity
import com.on.staccato.presentation.timeline.adapter.TimelineAdapter
import com.on.staccato.presentation.timeline.viewmodel.TimelineViewModel
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

    override fun onMemoryClicked(memoryId: Long) {
        val bundle: Bundle = bundleOf(MEMORY_ID_KEY to memoryId)
        navigateToMemory(bundle)
    }

    override fun onMemoryCreationClicked() {
        val memoryCreationLauncher = (activity as MainActivity).memoryCreationLauncher
        MemoryCreationActivity.startWithResultLauncher(
            requireActivity(),
            memoryCreationLauncher,
        )
    }

    override fun onSortClicked() {
        showToast(getString(R.string.all_default_not_supported))
    }

    private fun setupBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = timelineViewModel
        binding.handler = this
    }

    private fun navigateToMemory(bundle: Bundle) {
        findNavController().navigate(R.id.action_timelineFragment_to_memoryFragment, bundle)
    }

    private fun setUpAdapter() {
        adapter = TimelineAdapter(this)
        binding.rvTimeline.adapter = adapter
    }

    private fun setUpObserving() {
        timelineViewModel.timeline.observe(viewLifecycleOwner) { timeline ->
            adapter.updateTimeline(timeline)
        }

        timelineViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            showToast(message)
        }

        sharedViewModel.isTimelineUpdated.observe(viewLifecycleOwner) { isUpdated ->
            if (isUpdated) {
                timelineViewModel.loadTimeline()
            }
        }
    }
}
