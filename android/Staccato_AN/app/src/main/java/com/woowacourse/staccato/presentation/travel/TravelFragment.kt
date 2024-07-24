package com.woowacourse.staccato.presentation.travel

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.DeleteDialogFragment
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentTravelBinding
import com.woowacourse.staccato.presentation.ToolbarHandler
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.travel.adapter.MatesAdapter
import com.woowacourse.staccato.presentation.travel.adapter.VisitsAdapter
import com.woowacourse.staccato.presentation.travel.viewmodel.TravelViewModel
import com.woowacourse.staccato.presentation.travel.viewmodel.TravelViewModelFactory
import com.woowacourse.staccato.presentation.travelupdate.TravelUpdateActivity

class TravelFragment : BindingFragment<FragmentTravelBinding>(R.layout.fragment_travel),
    ToolbarHandler {
    private val viewModel: TravelViewModel by viewModels {
        TravelViewModelFactory()
    }

    private lateinit var matesAdapter: MatesAdapter
    private lateinit var visitsAdapter: VisitsAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        initBinding()
        initToolbar()
        initMatesAdapter()
        initVisitsAdapter()
        observeTravel()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.toolbarHandler = this
    }

    private fun initToolbar() {
        binding.includeTravelToolbar.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeTravel() {
        viewModel.loadTravel()
        viewModel.travel.observe(viewLifecycleOwner) { travel ->
            matesAdapter.updateMates(travel.mates)
            visitsAdapter.updateVisits(travel.visits)
        }
    }

    private fun initMatesAdapter() {
        matesAdapter = MatesAdapter()
        binding.rvTravelMates.adapter = matesAdapter
    }

    private fun initVisitsAdapter() {
        visitsAdapter = VisitsAdapter()
        binding.rvTravelVisits.adapter = visitsAdapter
    }

    override fun onUpdateClicked() {
        val travelUpdateLauncher = (activity as MainActivity).travelUpdateLauncher
        TravelUpdateActivity.startWithResultLauncher(
            requireActivity(),
            travelUpdateLauncher,
        )
    }

    override fun onDeleteClicked() {
        val deleteDialog = DeleteDialogFragment()
        val fragmentManager = parentFragmentManager
        deleteDialog.apply {
            setDialogHandler {
                findNavController().popBackStack()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.travel_delete_complete),
                    Toast.LENGTH_SHORT,
                ).show()
            }
            show(fragmentManager, DeleteDialogFragment.TAG)
        }
    }
}
