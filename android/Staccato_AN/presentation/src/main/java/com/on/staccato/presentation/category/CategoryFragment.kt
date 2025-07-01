package com.on.staccato.presentation.category

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.domain.model.Participants
import com.on.staccato.domain.model.Role
import com.on.staccato.presentation.R
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.category.adapter.MemberInviteHandler
import com.on.staccato.presentation.category.adapter.MembersAdapter
import com.on.staccato.presentation.category.adapter.StaccatosAdapter
import com.on.staccato.presentation.category.component.ExitDialogScreen
import com.on.staccato.presentation.category.invite.InviteScreen
import com.on.staccato.presentation.category.model.CategoryEvent
import com.on.staccato.presentation.category.model.CategoryUiModel
import com.on.staccato.presentation.category.model.CategoryUiModel.Companion.DEFAULT_CATEGORY_ID
import com.on.staccato.presentation.category.viewmodel.CategoryViewModel
import com.on.staccato.presentation.categorycreation.CategoryCreationActivity.Companion.KEY_IS_CATEGORY_CREATED
import com.on.staccato.presentation.categoryupdate.CategoryUpdateActivity
import com.on.staccato.presentation.categoryupdate.CategoryUpdateActivity.Companion.KEY_IS_CATEGORY_UPDATED
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.common.dialog.DeleteDialogFragment
import com.on.staccato.presentation.common.dialog.DialogHandler
import com.on.staccato.presentation.common.toolbar.ToolbarHandler
import com.on.staccato.presentation.databinding.FragmentCategoryBinding
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
import kotlinx.coroutines.flow.combine
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

    private val isCategoryUpdated: Boolean by lazy {
        arguments?.getBoolean(KEY_IS_CATEGORY_UPDATED) ?: false
    }

    private val isCategoryCreated: Boolean by lazy {
        arguments?.getBoolean(KEY_IS_CATEGORY_CREATED) ?: false
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
        viewModel.updateCategoryId(categoryId)

        if (isCategoryCreated || isCategoryUpdated) sharedViewModel.updateIsTimelineUpdated(true)
        if (isCategoryUpdated) viewModel.loadCategory(categoryId)

        initBinding()
        initToolbar()
        initAdapter()
        observeCategoryInformation()
        observeIsStaccatosUpdated()
        observeCategoryState()
        observeMessageEvent()
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

    override fun onExitClicked() {
        viewModel.showLeaveDialog()
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

    override fun onCategoryRefreshClicked() {
        viewModel.loadCategory(categoryId)
        // TODO: 스타카토 목록 동일하면 갱신하지 않도록 리팩터링
        sharedViewModel.updateCategoryRefreshEvent()
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
        binding.cvCategoryExitDialog.setContent { ExitDialogScreen() }
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

    private fun observeCategoryInformation() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.category,
                    viewModel.participatingMembers,
                ) { category, participatingMembers ->
                    category to participatingMembers
                }.collect { (category, participatingMembers) ->
                    updateAdapters(category, participatingMembers)
                }
            }
        }
    }

    private fun observeIsStaccatosUpdated() {
        sharedViewModel.isStaccatosUpdated.observe(viewLifecycleOwner) { isUpdated ->
            if (isUpdated) {
                viewModel.loadCategory(categoryId)
                sharedViewModel.updateIsStaccatosUpdated(false)
            }
        }
    }

    private fun updateAdapters(
        category: CategoryUiModel,
        participatingMembers: Participants,
    ) {
        staccatosAdapter.updateStaccatos(category.staccatos)
        membersAdapter.updateInvitable(category.myRole == Role.HOST)
        membersAdapter.submitMembers(participatingMembers.members)
    }

    private fun observeCategoryState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.categoryEvent.collect { state -> onCategoryState(state) }
            }
        }
    }

    private fun onCategoryState(state: CategoryEvent) {
        when (state) {
            is CategoryEvent.Deleted -> {
                if (state.success) {
                    sharedViewModel.updateIsTimelineUpdated(true)
                    showToast(getString(R.string.category_delete_complete))
                } else {
                    showToast(getString(R.string.category_delete_error))
                }
                findNavController().popBackStack()
            }

            is CategoryEvent.Exited -> {
                sharedViewModel.updateIsTimelineUpdated(true)
                showToast(getString(R.string.category_exit_complete))
                findNavController().popBackStack()
            }

            is CategoryEvent.Error -> {
                showToast(getString(R.string.category_empty_error))
                findNavController().popBackStack()
            }

            is CategoryEvent.InviteSuccess -> {
                showToast(getString(R.string.invite_member_success, state.count))
            }
        }
    }

    private fun observeMessageEvent() {
        viewModel.messageEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is MessageEvent.Text -> showToast(event.message)
                is MessageEvent.ResId -> showExceptionSnackBar(event.messageId)
            }
        }
    }

    private fun showExceptionSnackBar(
        @StringRes messageId: Int,
    ) {
        view?.showSnackBarWithAction(
            message = getString(messageId),
            actionLabel = R.string.all_retry,
            onAction = ::onRetryAction,
            Snackbar.LENGTH_INDEFINITE,
        )
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
