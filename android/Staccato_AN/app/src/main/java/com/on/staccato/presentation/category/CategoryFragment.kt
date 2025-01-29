package com.on.staccato.presentation.category

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.FragmentCategoryBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.common.DeleteDialogFragment
import com.on.staccato.presentation.common.DialogHandler
import com.on.staccato.presentation.common.ToolbarHandler
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.category.adapter.MatesAdapter
import com.on.staccato.presentation.category.adapter.StaccatosAdapter
import com.on.staccato.presentation.category.viewmodel.CategoryViewModel
import com.on.staccato.presentation.memoryupdate.MemoryUpdateActivity
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccatocreation.StaccatoCreationActivity
import com.on.staccato.presentation.util.showSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment :
    BindingFragment<FragmentCategoryBinding>(R.layout.fragment_category),
    ToolbarHandler,
    CategoryHandler,
    DialogHandler {
    private val categoryId by lazy {
        arguments?.getLong(CATEGORY_ID_KEY) ?: throw IllegalArgumentException()
    }
    private val viewModel: CategoryViewModel by viewModels()
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
        observeCategory()
        observeIsDeleteSuccess()
        showErrorToast()
        showExceptionSnackBar()
        viewModel.loadCategory(categoryId)
    }

    override fun onUpdateClicked() {
        val categoryUpdateLauncher = (activity as MainActivity).memoryUpdateLauncher
        MemoryUpdateActivity.startWithResultLauncher(
            categoryId,
            requireActivity(),
            categoryUpdateLauncher,
        )
    }

    override fun onDeleteClicked() {
        deleteDialog.show(parentFragmentManager, DeleteDialogFragment.TAG)
    }

    override fun onStaccatoClicked(staccatoId: Long) {
        viewModel.category.value?.let {
            val bundle =
                bundleOf(
                    STACCATO_ID_KEY to staccatoId,
                )
            findNavController().navigate(R.id.action_memoryFragment_to_staccatoFragment, bundle)
        }
    }

    override fun onConfirmClicked() {
        viewModel.deleteCategory(categoryId)
    }

    override fun onStaccatoCreationClicked() {
        viewModel.category.value?.let {
            val staccatoCreationLauncher = (activity as MainActivity).staccatoCreationLauncher
            StaccatoCreationActivity.startWithResultLauncher(
                requireContext(),
                staccatoCreationLauncher,
                categoryId,
                it.title,
            )
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.toolbarHandler = this
        binding.categoryHandler = this
    }

    private fun initToolbar() {
        binding.includeCategoryToolbar.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeCategory() {
        viewModel.category.observe(viewLifecycleOwner) { category ->
            matesAdapter.updateMates(category.mates)
            staccatosAdapter.updateStaccatos(category.staccatos)
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
        binding.rvCategoryMates.adapter = matesAdapter
    }

    private fun initStaccatosAdapter() {
        staccatosAdapter = StaccatosAdapter(handler = this)
        binding.rvCategoryStaccatos.adapter = staccatosAdapter
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
        viewModel.loadCategory(categoryId)
    }

    companion object {
        const val CATEGORY_ID_KEY = "categoryId"
        const val CATEGORY_TITLE_KEY = "categoryTitle"
    }
}
