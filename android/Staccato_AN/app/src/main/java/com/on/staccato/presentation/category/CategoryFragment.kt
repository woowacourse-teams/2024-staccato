package com.on.staccato.presentation.category

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.FragmentCategoryBinding
import com.on.staccato.domain.model.Role
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.category.adapter.MemberInviteHandler
import com.on.staccato.presentation.category.adapter.MembersAdapter
import com.on.staccato.presentation.category.adapter.StaccatosAdapter
import com.on.staccato.presentation.category.invite.InviteScreen
import com.on.staccato.presentation.category.model.CategoryUiModel
import com.on.staccato.presentation.category.model.CategoryUiModel.Companion.DEFAULT_CATEGORY_ID
import com.on.staccato.presentation.category.viewmodel.CategoryViewModel
import com.on.staccato.presentation.categoryupdate.CategoryUpdateActivity
import com.on.staccato.presentation.common.DeleteDialogFragment
import com.on.staccato.presentation.common.DialogHandler
import com.on.staccato.presentation.common.ToolbarHandler
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccatocreation.StaccatoCreationActivity
import com.on.staccato.presentation.util.showSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import com.on.staccato.util.logging.AnalyticsEvent.Companion.NAME_FRAGMENT_PAGE
import com.on.staccato.util.logging.AnalyticsEvent.Companion.NAME_STACCATO_CREATION
import com.on.staccato.util.logging.AnalyticsEvent.Companion.NAME_STACCATO_READ
import com.on.staccato.util.logging.LoggingManager
import com.on.staccato.util.logging.Param
import com.on.staccato.util.logging.Param.Companion.KEY_FRAGMENT_NAME
import com.on.staccato.util.logging.Param.Companion.KEY_IS_CREATED_IN_MAIN
import com.on.staccato.util.logging.Param.Companion.KEY_IS_VIEWED_BY_MARKER
import com.on.staccato.util.logging.Param.Companion.PARAM_CATEGORY_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoryFragment :
    BindingFragment<FragmentCategoryBinding>(R.layout.fragment_category),
    ToolbarHandler,
    CategoryHandler,
    MemberInviteHandler,
    DialogHandler {
    private val categoryId: Long by lazy {
        arguments?.getLong(CATEGORY_ID_KEY) ?: DEFAULT_CATEGORY_ID
    }
    private val viewModel: CategoryViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private val deleteDialog = DeleteDialogFragment { onConfirmClicked() }

    @Inject
    lateinit var loggingManager: LoggingManager

    private val membersAdapter by lazy { MembersAdapter(this) }

    private val staccatosAdapter by lazy { StaccatosAdapter(handler = this) }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        viewModel.loadCategory(categoryId)
        initBinding()
        initToolbar()
        initAdapter()
        observeCategory()
        observeIsDeleteSuccess()
        showErrorToast()
        showExceptionSnackBar()
        logAccess()
    }

    override fun onUpdateClicked() {
        val categoryUpdateLauncher = (activity as MainActivity).categoryUpdateLauncher
        CategoryUpdateActivity.startWithResultLauncher(
            categoryId,
            requireActivity(),
            categoryUpdateLauncher,
        )
    }

    override fun onDeleteClicked() {
        deleteDialog.show(parentFragmentManager, DeleteDialogFragment.TAG)
    }

    override fun onStaccatoClicked(staccatoId: Long) {
        loggingManager.logEvent(
            NAME_STACCATO_READ,
            Param(KEY_IS_VIEWED_BY_MARKER, false),
        )
        val bundle =
            bundleOf(
                STACCATO_ID_KEY to staccatoId,
            )
        findNavController().navigate(R.id.action_categoryFragment_to_staccatoFragment, bundle)
    }

    override fun onConfirmClicked() {
        viewModel.deleteCategory()
    }

    override fun onInviteClicked() {
        viewModel.toggleInviteMode(true)
    }

    override fun onStaccatoCreationClicked(
        category: CategoryUiModel?,
        isPermissionCanceled: Boolean,
    ) {
        loggingManager.logEvent(
            NAME_STACCATO_CREATION,
            Param(KEY_IS_CREATED_IN_MAIN, false),
        )

        if (category == null) {
            showToast(getString(R.string.category_error_staccato_record_impossible))
            findNavController().popBackStack()
        } else {
            navigateToStaccatoCreation(category, isPermissionCanceled)
        }
    }

    private fun navigateToStaccatoCreation(
        category: CategoryUiModel,
        isPermissionCanceled: Boolean,
    ) {
        val staccatoCreationLauncher = (activity as MainActivity).staccatoCreationLauncher
        StaccatoCreationActivity.startWithResultLauncher(
            context = requireContext(),
            activityLauncher = staccatoCreationLauncher,
            isPermissionCanceled = isPermissionCanceled,
            categoryId = category.id,
            categoryTitle = category.title,
        )
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.toolbarHandler = this
        binding.categoryHandler = this
        binding.cvMemberInvite.setContent { InviteScreen() }
        observeIsPermissionCanceled()
    }

    private fun observeIsPermissionCanceled() {
        sharedViewModel.isPermissionCanceled.observe(viewLifecycleOwner) {
            binding.isPermissionCanceled = it
        }
    }

    private fun initToolbar() {
        binding.includeCategoryToolbar.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initAdapter() {
        binding.rvCategoryMates.adapter = membersAdapter
        binding.rvCategoryStaccatos.adapter = staccatosAdapter
    }

    private fun observeCategory() {
        viewModel.category.observe(viewLifecycleOwner) { category ->
            staccatosAdapter.updateStaccatos(category.staccatos)
            membersAdapter.updateInvitable(category.myRole == Role.HOST)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.participatingMembers.collect {
                membersAdapter.submitMembers(it.members)
            }
        }
    }

    private fun observeIsDeleteSuccess() {
        viewModel.isDeleted.observe(viewLifecycleOwner) { isDeleteSuccess ->
            if (isDeleteSuccess) {
                sharedViewModel.setTimelineHasUpdated()
                showToast(getString(R.string.category_delete_complete))
            } else {
                showToast(getString(R.string.category_delete_title))
            }
            findNavController().popBackStack()
        }
    }

    private fun showErrorToast() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            showToast(message)
        }
    }

    private fun showExceptionSnackBar() {
        viewModel.exceptionState.observe(viewLifecycleOwner) { state ->
            view?.showSnackBarWithAction(
                message = getString(state.messageId),
                actionLabel = R.string.all_retry,
                onAction = ::onRetryAction,
                Snackbar.LENGTH_INDEFINITE,
            )
        }
    }

    private fun onRetryAction() {
        viewModel.loadCategory(categoryId)
    }

    private fun logAccess() {
        loggingManager.logEvent(
            NAME_FRAGMENT_PAGE,
            Param(KEY_FRAGMENT_NAME, PARAM_CATEGORY_FRAGMENT),
        )
    }

    companion object {
        const val CATEGORY_ID_KEY = "categoryId"
        const val CATEGORY_TITLE_KEY = "categoryTitle"
    }
}
