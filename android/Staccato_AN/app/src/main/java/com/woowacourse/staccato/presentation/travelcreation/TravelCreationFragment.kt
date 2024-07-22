package com.woowacourse.staccato.presentation.travelcreation

import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
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
        binding.btnTravelUpdateDone.setOnClickListener {
            findNavController().navigate(
                R.id.travelFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.travelCreationFragment, true).build(),
            )
        }
    }
}
