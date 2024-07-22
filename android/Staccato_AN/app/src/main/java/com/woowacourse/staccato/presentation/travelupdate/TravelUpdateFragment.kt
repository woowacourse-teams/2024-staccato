package com.woowacourse.staccato.presentation.travelupdate

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentTravelCreationBinding
import com.woowacourse.staccato.presentation.base.BindingFragment

class TravelUpdateFragment :
    BindingFragment<FragmentTravelCreationBinding>(R.layout.fragment_travel_creation) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.tvTmpTitle.text = "여행 수정 화면"
        binding.btnTravelUpdateDone.setOnClickListener {
            findNavController().popBackStack(R.id.travelFragment, false)
        }
    }
}
