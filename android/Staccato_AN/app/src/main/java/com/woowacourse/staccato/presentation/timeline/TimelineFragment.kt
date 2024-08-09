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
import com.woowacourse.staccato.presentation.timeline.adapter.TimelineAdapter
import com.woowacourse.staccato.presentation.timeline.viewmodel.TimelineViewModel
import com.woowacourse.staccato.presentation.timeline.viewmodel.TimelineViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast

class TimelineFragment :
    BindingFragment<FragmentTimelineBinding>(R.layout.fragment_timeline),
    TimelineHandler {
    private val timelineViewModel: TimelineViewModel by viewModels { TimelineViewModelFactory() }
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var adapter: TimelineAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        setUpAdapter()
        setUpObserving()
    }

    private fun setUpAdapter() {
        adapter = TimelineAdapter(this)
        binding.rvTimeline.adapter = adapter
    }

    private fun setUpObserving() {
        timelineViewModel.travels.observe(viewLifecycleOwner) { timeline ->
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

    private fun navigateToTravel(bundle: Bundle) {
        findNavController().navigate(R.id.action_timelineFragment_to_travelFragment, bundle)
    }

    override fun onTravelClicked(travelId: Long) {
        val bundle: Bundle = bundleOf(TRAVEL_ID_KEY to travelId)
        navigateToTravel(bundle)
    }

    companion object {
        const val TRAVEL_ID_KEY = "travelId"
    }
}
