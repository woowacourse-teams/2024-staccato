package com.on.staccato.presentation.memory

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.on.staccato.R
import com.on.staccato.data.StaccatoClient.memoryApiService
import com.on.staccato.data.memory.MemoryDefaultRepository
import com.on.staccato.data.memory.MemoryRemoteDataSource
import com.on.staccato.databinding.FragmentMemoryBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.common.DeleteDialogFragment
import com.on.staccato.presentation.common.DialogHandler
import com.on.staccato.presentation.common.ToolbarHandler
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.memory.adapter.MatesAdapter
import com.on.staccato.presentation.memory.adapter.VisitsAdapter
import com.on.staccato.presentation.memory.viewmodel.MemoryViewModel
import com.on.staccato.presentation.memory.viewmodel.MemoryViewModelFactory
import com.on.staccato.presentation.memoryupdate.MemoryUpdateActivity
import com.on.staccato.presentation.moment.MomentFragment.Companion.MOMENT_ID_KEY
import com.on.staccato.presentation.momentcreation.MomentCreationActivity
import com.on.staccato.presentation.util.showToast

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
        observeMemory()
        observeIsDeleteSuccess()
        showErrorToast()
        viewModel.loadMemory(memoryId)
    }

    override fun onUpdateClicked() {
        val memoryUpdateLauncher = (activity as MainActivity).memoryUpdateLauncher
        MemoryUpdateActivity.startWithResultLauncher(
            memoryId,
            requireActivity(),
            memoryUpdateLauncher,
        )
    }

    override fun onDeleteClicked() {
        deleteDialog.show(parentFragmentManager, DeleteDialogFragment.TAG)
    }

    override fun onVisitClicked(visitId: Long) {
        viewModel.memory.value?.let {
            val bundle =
                bundleOf(
                    MOMENT_ID_KEY to visitId,
                )
            findNavController().navigate(R.id.action_memoryFragment_to_momentFragment, bundle)
        }
    }

    override fun onConfirmClicked() {
        viewModel.deleteMemory(memoryId)
    }

    override fun onVisitCreationClicked() {
        if (viewModel.isInPeriod()) {
            viewModel.memory.value?.let {
                val visitCreationLauncher = (activity as MainActivity).staccatoCreationLauncher
                MomentCreationActivity.startWithResultLauncher(
                    memoryId,
                    it.title,
                    requireContext(),
                    visitCreationLauncher,
                )
            }
        } else {
            showToast(getString(R.string.memory_unable_to_create_moment))
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.toolbarHandler = this
        binding.memoryHandler = this
    }

    private fun initToolbar() {
        binding.includeMemoryToolbar.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeMemory() {
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
