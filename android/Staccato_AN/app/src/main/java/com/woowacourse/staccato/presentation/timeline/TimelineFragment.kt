package com.woowacourse.staccato.presentation.timeline

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentTimelineBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.main.SharedViewModel
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.timeline.adapter.TimelineAdapter
import com.woowacourse.staccato.presentation.timeline.model.TimelineUiModel
import com.woowacourse.staccato.presentation.timeline.viewmodel.TimelineViewModel
import com.woowacourse.staccato.presentation.util.showToast

class TimelineFragment :
    BindingFragment<FragmentTimelineBinding>(R.layout.fragment_timeline),
    TimelineHandler {
    private val timelineViewModel: TimelineViewModel by viewModels { TimelineViewModel.factory() }
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var adapter: TimelineAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        setUpAdapter()
        setUpObserving()
    }

    override fun onMemoryClicked(memoryId: Long) {
        val bundle: Bundle = bundleOf(MEMORY_ID_KEY to memoryId)
        navigateToMemory(bundle)
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
            binding.ivTimelineEmpty.visibility = View.VISIBLE
            binding.tvTimelineEmpty.visibility = View.VISIBLE
        } else {
            binding.ivTimelineEmpty.visibility = View.GONE
            binding.tvTimelineEmpty.visibility = View.GONE
        }
    }
}
