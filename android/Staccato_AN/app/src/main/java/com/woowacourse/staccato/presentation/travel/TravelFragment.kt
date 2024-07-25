package com.woowacourse.staccato.presentation.travel

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.DeleteDialogFragment
import com.woowacourse.staccato.R
import com.woowacourse.staccato.data.StaccatoClient.travelApiService
import com.woowacourse.staccato.data.travel.TravelDefaultRepository
import com.woowacourse.staccato.data.travel.TravelRemoteDataSource
import com.woowacourse.staccato.databinding.FragmentTravelBinding
import com.woowacourse.staccato.presentation.ToolbarHandler
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.timeline.TimelineFragment.Companion.TRAVEL_ID_KEY
import com.woowacourse.staccato.presentation.travel.adapter.MatesAdapter
import com.woowacourse.staccato.presentation.travel.adapter.VisitsAdapter
import com.woowacourse.staccato.presentation.travel.viewmodel.TravelViewModel
import com.woowacourse.staccato.presentation.travel.viewmodel.TravelViewModelFactory
import com.woowacourse.staccato.presentation.travelupdate.TravelUpdateActivity
import kotlin.IllegalArgumentException

class TravelFragment :
    BindingFragment<FragmentTravelBinding>(R.layout.fragment_travel),
    ToolbarHandler {
    private val viewModel: TravelViewModel by viewModels {
        TravelViewModelFactory(TravelDefaultRepository(TravelRemoteDataSource(travelApiService)), getTravelId())
    }
    private val deleteDialog = DeleteDialogFragment { findNavController().popBackStack() }

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
        navigateToVisit()
        showErrorToast()
    }

    private fun getTravelId(): Long = arguments?.getLong(TRAVEL_ID_KEY) ?: throw IllegalArgumentException(INVALID_TRAVEL_ID)

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
        visitsAdapter = VisitsAdapter(handler = viewModel)
        binding.rvTravelVisits.adapter = visitsAdapter
    }

    private fun navigateToVisit() {
        viewModel.visitId.observe(viewLifecycleOwner) { visitId ->
            val bundle = bundleOf(VISIT_ID_KEY to visitId)
            findNavController().navigate(R.id.action_travelFragment_to_visitFragment, bundle)
        }
    }

    private fun showErrorToast() {
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onUpdateClicked() {
        val travelUpdateLauncher = (activity as MainActivity).travelUpdateLauncher
        TravelUpdateActivity.startWithResultLauncher(
            requireActivity(),
            travelUpdateLauncher,
        )
    }

    override fun onDeleteClicked() {
        val fragmentManager = parentFragmentManager
        deleteDialog.apply {
            show(fragmentManager, DeleteDialogFragment.TAG)
        }
    }

    companion object {
        const val VISIT_ID_KEY = "visitId"
        const val INVALID_TRAVEL_ID = "잘못된 여행 id 입니다"
    }
}
