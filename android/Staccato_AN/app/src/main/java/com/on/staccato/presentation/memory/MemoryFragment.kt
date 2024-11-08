package com.on.staccato.presentation.memory

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.FragmentMemoryBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.common.DeleteDialogFragment
import com.on.staccato.presentation.common.DialogHandler
import com.on.staccato.presentation.common.ToolbarHandler
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.memory.adapter.MatesAdapter
import com.on.staccato.presentation.memory.adapter.StaccatosAdapter
import com.on.staccato.presentation.memory.viewmodel.MemoryViewModel
import com.on.staccato.presentation.memoryupdate.MemoryUpdateActivity
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccatocreation.StaccatoCreationActivity
import com.on.staccato.presentation.util.showSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemoryFragment :
    BindingFragment<FragmentMemoryBinding>(R.layout.fragment_memory),
    ToolbarHandler,
    MemoryHandler,
    DialogHandler {
    private val memoryId by lazy {
        arguments?.getLong(MEMORY_ID_KEY) ?: throw IllegalArgumentException()
    }
    private val viewModel: MemoryViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private val deleteDialog = DeleteDialogFragment { onConfirmClicked() }

    private lateinit var matesAdapter: MatesAdapter
    private lateinit var staccatosAdapter: StaccatosAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        initBinding()
        initToolbar()
        initMatesAdapter()
        initStaccatosAdapter()
        observeMemory()
        observeIsDeleteSuccess()
        showErrorToast()
        showExceptionSnackBar()
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

    override fun onStaccatoClicked(staccatoId: Long) {
        viewModel.memory.value?.let {
            val bundle =
                bundleOf(
                    STACCATO_ID_KEY to staccatoId,
                )
            findNavController().navigate(R.id.action_memoryFragment_to_staccatoFragment, bundle)
        }
    }

    override fun onConfirmClicked() {
        viewModel.deleteMemory(memoryId)
    }

    override fun onStaccatoCreationClicked() {
        viewModel.memory.value?.let {
            val staccatoCreationLauncher = (activity as MainActivity).staccatoCreationLauncher
            StaccatoCreationActivity.startWithResultLauncher(
                memoryId,
                it.title,
                requireContext(),
                staccatoCreationLauncher,
            )
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
            staccatosAdapter.updateStaccatos(memory.staccatos)
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

    private fun initStaccatosAdapter() {
        staccatosAdapter = StaccatosAdapter(handler = this)
        binding.rvMemoryStaccatos.adapter = staccatosAdapter
    }

    private fun showErrorToast() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            showToast(message)
        }
    }

    private fun showExceptionSnackBar() {
        viewModel.exceptionMessage.observe(viewLifecycleOwner) { message ->
            view?.showSnackBarWithAction(
                message = message,
                actionLabel = R.string.all_retry,
                onAction = ::onRetryAction,
                Snackbar.LENGTH_INDEFINITE,
            )
        }
    }

    private fun onRetryAction() {
        viewModel.loadMemory(memoryId)
    }

    companion object {
        const val MEMORY_ID_KEY = "memoryId"
        const val MEMORY_TITLE_KEY = "memoryTitle"
    }
}
