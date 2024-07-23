package com.woowacourse.staccato.presentation.travel

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentTravelBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.travelupdate.TravelUpdateActivity

class TravelFragment : BindingFragment<FragmentTravelBinding>(R.layout.fragment_travel) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.btnTravelUpdate.setOnClickListener {
            val travelUpdateLauncher = (activity as MainActivity).travelUpdateLauncher
            TravelUpdateActivity.startWithResultLauncher(
                this.requireActivity(),
                travelUpdateLauncher,
            )
//            findNavController().navigate(R.id.action_travelFragment_to_travelUpdateFragment)
        }
        binding.btnVisit.setOnClickListener {
            findNavController().navigate(R.id.action_travelFragment_to_visitFragment)
        }
    }
}
