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
import com.on.staccato.presentation.main.SharedViewModel
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.memorycreation.MemoryCreationActivity
import com.on.staccato.presentation.timeline.adapter.TimelineAdapter
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.presentation.timeline.viewmodel.TimelineViewModel
import com.on.staccato.presentation.util.showToast

class TimelineFragment : BindingFragment<FragmentTimelineBinding>(R.layout.fragment_timeline),
    TimelineHandler {
    private val timelineViewModel: TimelineViewModel by viewModels { TimelineViewModel.factory() }
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var adapter: TimelineAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.handler = this
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

    private fun navigateToMemory(bundle: Bundle) {
        findNavController().navigate(R.id.action_timelineFragment_to_memoryFragment, bundle)
    }

    private fun setUpAdapter() {
        adapter = TimelineAdapter(this)
        binding.rvTimeline.adapter = adapter
    }

    private fun setUpObserving() {
        timelineViewModel.timeline.observe(viewLifecycleOwner) { timeline ->
            checkTimelineEmpty(timeline)
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

    private fun checkTimelineEmpty(timeline: List<TimelineUiModel>) {
        // TODO: data binding 으로 가시성 설정되지 않는 오류 해결하기
        if (timeline.isEmpty()) {
            binding.frameTimelineAddMemory.visibility = View.GONE
            binding.ivTimelineEmpty.visibility = View.VISIBLE
            binding.tvTimelineEmpty.visibility = View.VISIBLE
            binding.btnTimelineCreateMemory.visibility = View.VISIBLE
        } else {
            binding.frameTimelineAddMemory.visibility = View.VISIBLE
            binding.ivTimelineEmpty.visibility = View.GONE
            binding.tvTimelineEmpty.visibility = View.GONE
            binding.btnTimelineCreateMemory.visibility = View.GONE
        }
    }
}
