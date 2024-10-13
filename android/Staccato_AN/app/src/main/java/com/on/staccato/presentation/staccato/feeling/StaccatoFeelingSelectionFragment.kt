package com.on.staccato.presentation.staccato.feeling

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.on.staccato.R
import com.on.staccato.databinding.FragmentStaccatoFeelingSelectionBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.DEFAULT_STACCATO_ID
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccato.viewmodel.StaccatoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaccatoFeelingSelectionFragment :
    BindingFragment<FragmentStaccatoFeelingSelectionBinding>(R.layout.fragment_staccato_feeling_selection) {
    private lateinit var feelingSelectionAdapter: FeelingSelectionAdapter
    private val staccatoViewModel: StaccatoViewModel by viewModels({ requireParentFragment() })
    private val staccatoFeelingSelectionViewModel: StaccatoFeelingSelectionViewModel by viewModels()

    private val staccatoId by lazy { arguments?.getLong(STACCATO_ID_KEY) ?: DEFAULT_STACCATO_ID }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        staccatoFeelingSelectionViewModel.setStaccatoId(staccatoId)
        initAdapter()
        observeInitialFeeling()
        observeFeelings()
    }

    private fun initAdapter() {
        feelingSelectionAdapter = FeelingSelectionAdapter(staccatoFeelingSelectionViewModel)
        binding.rvStaccatoFeelingSelection.adapter = feelingSelectionAdapter
    }

    private fun observeInitialFeeling() {
        staccatoViewModel.feeling.observe(viewLifecycleOwner) { feeling ->
            staccatoFeelingSelectionViewModel.setFeelings(feeling)
        }
    }

    private fun observeFeelings() {
        staccatoFeelingSelectionViewModel.feelings.observe(viewLifecycleOwner) { feelings ->
            feelingSelectionAdapter.updateFeelings(feelings)
        }
    }
}
