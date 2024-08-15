package com.woowacourse.staccato.presentation.memory

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.data.StaccatoClient.memoryApiService
import com.woowacourse.staccato.data.memory.TravelDefaultRepository
import com.woowacourse.staccato.data.memory.MemoryRemoteDataSource
import com.woowacourse.staccato.databinding.FragmentTravelBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.common.DeleteDialogFragment
import com.woowacourse.staccato.presentation.common.DialogHandler
import com.woowacourse.staccato.presentation.common.ToolbarHandler
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.main.SharedViewModel
import com.woowacourse.staccato.presentation.memory.adapter.MatesAdapter
import com.woowacourse.staccato.presentation.memory.adapter.VisitsAdapter
import com.woowacourse.staccato.presentation.memory.viewmodel.TravelViewModel
import com.woowacourse.staccato.presentation.memory.viewmodel.TravelViewModelFactory
import com.woowacourse.staccato.presentation.memoryupdate.TravelUpdateActivity
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visit.VisitFragment.Companion.VISIT_ID_KEY
import com.woowacourse.staccato.presentation.visitcreation.VisitCreationActivity

class TravelFragment :
    BindingFragment<FragmentTravelBinding>(R.layout.fragment_travel),
    ToolbarHandler,
    TravelHandler,
    DialogHandler {
    private val travelId by lazy {
        arguments?.getLong(TRAVEL_ID_KEY) ?: throw IllegalArgumentException()
    }
    private val viewModel: TravelViewModel by viewModels {
        TravelViewModelFactory(TravelDefaultRepository(MemoryRemoteDataSource(memoryApiService)))
    }
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private val deleteDialog = DeleteDialogFragment { onConfirmClicked() }

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
        observeIsDeleteSuccess()
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
        viewModel.travel.value?.let {
            val bundle =
                bundleOf(
                    VISIT_ID_KEY to visitId,
                    TRAVEL_ID_KEY to travelId,
                    TRAVEL_TITLE_KEY to it.title,
                )
            findNavController().navigate(R.id.action_travelFragment_to_visitFragment, bundle)
        }
    }

    override fun onConfirmClicked() {
        viewModel.deleteTravel(travelId)
    }

    override fun onVisitCreationClicked() {
        if (viewModel.isTraveling()) {
            viewModel.travel.value?.let {
                val visitCreationLauncher = (activity as MainActivity).visitCreationLauncher
                VisitCreationActivity.startWithResultLauncher(
                    travelId,
                    it.title,
                    requireContext(),
                    visitCreationLauncher,
                )
            }
        } else {
            showToast("지금은 여행 기간이 아니에요!")
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.toolbarHandler = this
        binding.travelHandler = this
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

    private fun observeIsDeleteSuccess() {
        viewModel.isDeleteSuccess.observe(viewLifecycleOwner) { isDeleteSuccess ->
            if (isDeleteSuccess) {
                sharedViewModel.setTimelineHasUpdated()
                findNavController().popBackStack()
                showToast(getString(R.string.travel_delete_complete))
            }
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
        const val TRAVEL_ID_KEY = "travelId"
        const val TRAVEL_TITLE_KEY = "travelTitle"
    }
}
