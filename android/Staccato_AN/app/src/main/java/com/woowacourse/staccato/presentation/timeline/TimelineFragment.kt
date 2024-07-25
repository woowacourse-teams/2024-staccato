package com.woowacourse.staccato.presentation.timeline

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentTimelineBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.timeline.adapter.TimelineAdapter

class TimelineFragment :
    BindingFragment<FragmentTimelineBinding>(R.layout.fragment_timeline),
    TimelineHandler {
    private val viewModel: TimelineViewModel by viewModels { TimelineViewModelFactory() }
    private lateinit var adapter: TimelineAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        setUpAdapter()
        setUpObserving()
        viewModel.loadTimeline()
    }

    private fun setUpAdapter() {
        adapter = TimelineAdapter(this)
        binding.rvTimeline.adapter = adapter
    }

    private fun setUpObserving() {
        viewModel.travels.observe(viewLifecycleOwner) { timeline ->
            adapter.setTravels(timeline)
        }
        viewModel.errorState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { state ->
                if (state) {
                    showToastMessage(ERROR_MESSAGE)
                }
            }
        }
    }

    private fun navigateToTravel(bundle: Bundle) {
        findNavController().navigate(R.id.action_timelineFragment_to_travelFragment, bundle)
    }

    override fun onTravelClicked(travelId: Long) {
        val bundle: Bundle = bundleOf(TRAVEL_ID_KEY to travelId)
        // Log.d("ㅌㅅㅌ", "clicked item: $travelId")
        navigateToTravel(bundle)
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val TRAVEL_ID_KEY = "travelId"
        const val ERROR_MESSAGE = "일시적인 오류가 발생했습니다.\n잠시 후 다시 시도해주세요."
    }
}
