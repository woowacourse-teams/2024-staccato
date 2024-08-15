package com.woowacourse.staccato.presentation.memory

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.data.StaccatoClient.memoryApiService
import com.woowacourse.staccato.data.memory.MemoryDefaultRepository
import com.woowacourse.staccato.data.memory.MemoryRemoteDataSource
import com.woowacourse.staccato.databinding.FragmentMemoryBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.common.DeleteDialogFragment
import com.woowacourse.staccato.presentation.common.DialogHandler
import com.woowacourse.staccato.presentation.common.ToolbarHandler
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.main.SharedViewModel
import com.woowacourse.staccato.presentation.memory.adapter.MatesAdapter
import com.woowacourse.staccato.presentation.memory.adapter.VisitsAdapter
import com.woowacourse.staccato.presentation.memory.viewmodel.MemoryViewModel
import com.woowacourse.staccato.presentation.memory.viewmodel.MemoryViewModelFactory
import com.woowacourse.staccato.presentation.memoryupdate.TravelUpdateActivity
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visit.VisitFragment.Companion.VISIT_ID_KEY
import com.woowacourse.staccato.presentation.visitcreation.VisitCreationActivity

class MemoryFragment :
    BindingFragment<FragmentMemoryBinding>(R.layout.fragment_memory),
    ToolbarHandler,
    MemoryHandler,
    DialogHandler {
    private val memoryId by lazy {
        arguments?.getLong(MEMORY_ID_KEY) ?: throw IllegalArgumentException()
    }
    private val viewModel: MemoryViewModel by viewModels {
        MemoryViewModelFactory(MemoryDefaultRepository(MemoryRemoteDataSource(memoryApiService)))
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
        viewModel.loadMemory(memoryId)
    }

    override fun onUpdateClicked() {
        val memoryUpdateLauncher = (activity as MainActivity).travelUpdateLauncher
        TravelUpdateActivity.startWithResultLauncher(
            memoryId,
            requireActivity(),
            memoryUpdateLauncher,
        )
    }

    override fun onDeleteClicked() {
        val fragmentManager = parentFragmentManager
        deleteDialog.apply {
            show(fragmentManager, DeleteDialogFragment.TAG)
        }
    }

    override fun onVisitClicked(visitId: Long) {
        viewModel.memory.value?.let {
            val bundle =
                bundleOf(
                    VISIT_ID_KEY to visitId,
                    MEMORY_ID_KEY to memoryId,
                    MEMORY_TITLE_KEY to it.title,
                )
            findNavController().navigate(R.id.action_travelFragment_to_visitFragment, bundle)
        }
    }

    override fun onConfirmClicked() {
        viewModel.deleteMemory(memoryId)
    }

    override fun onVisitCreationClicked() {
        if (viewModel.isInPeriod()) {
            viewModel.memory.value?.let {
                val visitCreationLauncher = (activity as MainActivity).visitCreationLauncher
                VisitCreationActivity.startWithResultLauncher(
                    memoryId,
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
        viewModel.memory.observe(viewLifecycleOwner) { memory ->
            matesAdapter.updateMates(memory.mates)
            visitsAdapter.updateVisits(memory.visits)
        }
    }

    private fun observeIsDeleteSuccess() {
        viewModel.isDeleteSuccess.observe(viewLifecycleOwner) { isDeleteSuccess ->
            if (isDeleteSuccess) {
                sharedViewModel.setTimelineHasUpdated()
                findNavController().popBackStack()
                showToast(getString(R.string.memory_delete_complete))
            }
        }
    }

    private fun initMatesAdapter() {
        matesAdapter = MatesAdapter()
        binding.rvMemoryMates.adapter = matesAdapter
    }

    private fun initVisitsAdapter() {
        visitsAdapter = VisitsAdapter(handler = this)
        binding.rvMemoryVisits.adapter = visitsAdapter
    }

    private fun showErrorToast() {
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    companion object {
        const val MEMORY_ID_KEY = "memoryId"
        const val MEMORY_TITLE_KEY = "memoryTitle"
    }
}