package com.woowacourse.staccato.presentation.travelcreation

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentTravelCreationBinding
import com.woowacourse.staccato.presentation.base.BindingFragment

class TravelCreationFragment :
    BindingFragment<FragmentTravelCreationBinding>(R.layout.fragment_travel_creation) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.btnTravel.setOnClickListener {
            findNavController().popBackStack(R.id.travelFragment, false)
//            findNavController().navigate(R.id.action_travelCreationFragment_to_travelFragment)
        }
    }
}
