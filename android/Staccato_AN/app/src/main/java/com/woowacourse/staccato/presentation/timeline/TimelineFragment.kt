package com.woowacourse.staccato.presentation.timeline

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentTimelineBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.timeline.adapter.TimelineAdapter

class TimelineFragment : BindingFragment<FragmentTimelineBinding>(R.layout.fragment_timeline),
    TimelineHandler {
    private lateinit var viewModel: TimelineViewModel
    private lateinit var adapter: TimelineAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        setUpViewModel()
        setUpAdapter()
        setUpObserving()
        viewModel.loadTimeline()
    }

    private fun setUpViewModel() {
        val viewModelFactory = TimelineViewModelFactory(TempTimelineRepository())
        viewModel = ViewModelProvider(this, viewModelFactory)[TimelineViewModel::class.java]
    }

    private fun setUpAdapter() {
        adapter = TimelineAdapter(this)
        binding.rvTimeline.adapter = adapter
    }

    private fun setUpObserving() {
        viewModel.travels.observe(viewLifecycleOwner) { timeline ->
            adapter.setTravels(timeline)
        }
    }

    private fun navigateToTravel() {
        findNavController().navigate(R.id.action_timelineFragment_to_travelFragment)
    }

    override fun onTravelClicked(travelId: Long) {
        // Log.d("ㅌㅅㅌ", "clicked item: $travelId")
        navigateToTravel()
    }
}
