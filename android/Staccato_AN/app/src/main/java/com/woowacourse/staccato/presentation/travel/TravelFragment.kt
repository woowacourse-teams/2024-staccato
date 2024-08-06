package com.woowacourse.staccato.presentation.travel

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.data.StaccatoClient.travelApiService
import com.woowacourse.staccato.data.travel.TravelDefaultRepository
import com.woowacourse.staccato.data.travel.TravelRemoteDataSource
import com.woowacourse.staccato.databinding.FragmentTravelBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.common.DeleteDialogFragment
import com.woowacourse.staccato.presentation.common.ToolbarHandler
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.travel.adapter.MatesAdapter
import com.woowacourse.staccato.presentation.travel.adapter.VisitsAdapter
import com.woowacourse.staccato.presentation.travel.viewmodel.TravelViewModel
import com.woowacourse.staccato.presentation.travel.viewmodel.TravelViewModelFactory
import com.woowacourse.staccato.presentation.travelupdate.TravelUpdateActivity
import com.woowacourse.staccato.presentation.util.showToast

class TravelFragment :
    BindingFragment<FragmentTravelBinding>(R.layout.fragment_travel),
    ToolbarHandler,
    TravelHandler {
    private val travelId by lazy { arguments?.getLong(TRAVEL_ID_KEY) ?: throw IllegalArgumentException() }
    private val viewModel: TravelViewModel by viewModels {
        TravelViewModelFactory(TravelDefaultRepository(TravelRemoteDataSource(travelApiService)))
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
        showErrorToast()
        viewModel.loadTravel(travelId)
    }

    override fun onUpdateClicked() {
        val travelUpdateLauncher = (activity as MainActivity).travelUpdateLauncher
        TravelUpdateActivity.startWithResultLauncher(
            travelId,
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

    override fun onVisitClicked(visitId: Long) {
        val bundle = bundleOf(VISIT_ID_KEY to visitId, TRAVEL_ID_KEY to travelId)
        findNavController().navigate(R.id.action_travelFragment_to_visitFragment, bundle)
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
        visitsAdapter = VisitsAdapter(handler = this)
        binding.rvTravelVisits.adapter = visitsAdapter
    }

    private fun showErrorToast() {
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    companion object {
        const val VISIT_ID_KEY = "visitId"
        const val TRAVEL_ID_KEY = "travelId"
    }
}
