package com.woowacourse.staccato.presentation.travel

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentTravelBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.travel.viewmodel.TravelViewModel
import com.woowacourse.staccato.presentation.travel.viewmodel.TravelViewModelFactory

class TravelFragment : BindingFragment<FragmentTravelBinding>(R.layout.fragment_travel) {
    private val viewModel: TravelViewModel by viewModels {
        TravelViewModelFactory()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        initBinding()
        observeTravel()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeTravel() {
        viewModel.loadTravel()
        viewModel.travel.observe(viewLifecycleOwner) { travel ->
            // TODO: mates, visits adapter update
        }
    }
}
