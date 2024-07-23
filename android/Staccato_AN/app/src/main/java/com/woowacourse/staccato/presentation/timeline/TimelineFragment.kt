package com.woowacourse.staccato.presentation.timeline

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentTimelineBinding
import com.woowacourse.staccato.presentation.base.BindingFragment

class TimelineFragment : BindingFragment<FragmentTimelineBinding>(R.layout.fragment_timeline) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.btnTimeline.setOnClickListener {
            findNavController().navigate(R.id.action_timelineFragment_to_travelFragment)
        }
    }
}
