package com.on.staccato.presentation.moment.feeling

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.on.staccato.R
import com.on.staccato.databinding.FragmentMomentFeelingSelectionBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.moment.viewmodel.MomentViewModel
import com.on.staccato.presentation.moment.viewmodel.MomentViewModelFactory
import kotlin.properties.Delegates

class MomentFeelingSelectionFragment :
    BindingFragment<FragmentMomentFeelingSelectionBinding>(R.layout.fragment_moment_feeling_selection) {
    private lateinit var feelingSelectionAdapter: FeelingSelectionAdapter
    private val momentViewModel: MomentViewModel by viewModels({ requireParentFragment() }) {
        MomentViewModelFactory()
    }
    private val momentFeelingSelectionViewModel: MomentFeelingSelectionViewModel by viewModels {
        MomentFeelingSelectionViewModelFactory(momentId)
    }
    private var momentId by Delegates.notNull<Long>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        momentId = arguments?.getLong(MOMENT_ID_KEY) ?: return
        initAdapter()
        observeInitialFeeling()
        observeFeelings()
    }

    private fun initAdapter() {
        feelingSelectionAdapter = FeelingSelectionAdapter(momentFeelingSelectionViewModel)
        binding.rvMomentFeelingSelection.adapter = feelingSelectionAdapter
    }

    private fun observeInitialFeeling() {
        momentViewModel.feeling.observe(viewLifecycleOwner) { feeling ->
            momentFeelingSelectionViewModel.setFeelings(feeling)
        }
    }

    private fun observeFeelings() {
        momentFeelingSelectionViewModel.feelings.observe(viewLifecycleOwner) { feelings ->
            feelingSelectionAdapter.updateFeelings(feelings)
        }
    }

    companion object {
        const val MOMENT_ID_KEY = "momentId"
    }
}
