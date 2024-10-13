package com.on.staccato.presentation.staccato.feeling

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.on.staccato.R
import com.on.staccato.databinding.FragmentMomentFeelingSelectionBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.staccato.MomentFragment.Companion.DEFAULT_STACCATO_ID
import com.on.staccato.presentation.staccato.MomentFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccato.viewmodel.StaccatoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MomentFeelingSelectionFragment :
    BindingFragment<FragmentMomentFeelingSelectionBinding>(R.layout.fragment_moment_feeling_selection) {
    private lateinit var feelingSelectionAdapter: FeelingSelectionAdapter
    private val staccatoViewModel: StaccatoViewModel by viewModels({ requireParentFragment() })
    private val momentFeelingSelectionViewModel: MomentFeelingSelectionViewModel by viewModels()

    private val momentId by lazy { arguments?.getLong(STACCATO_ID_KEY) ?: DEFAULT_STACCATO_ID }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        momentFeelingSelectionViewModel.setMomentId(momentId)
        initAdapter()
        observeInitialFeeling()
        observeFeelings()
    }

    private fun initAdapter() {
        feelingSelectionAdapter = FeelingSelectionAdapter(momentFeelingSelectionViewModel)
        binding.rvMomentFeelingSelection.adapter = feelingSelectionAdapter
    }

    private fun observeInitialFeeling() {
        staccatoViewModel.feeling.observe(viewLifecycleOwner) { feeling ->
            momentFeelingSelectionViewModel.setFeelings(feeling)
        }
    }

    private fun observeFeelings() {
        momentFeelingSelectionViewModel.feelings.observe(viewLifecycleOwner) { feelings ->
            feelingSelectionAdapter.updateFeelings(feelings)
        }
    }
}
