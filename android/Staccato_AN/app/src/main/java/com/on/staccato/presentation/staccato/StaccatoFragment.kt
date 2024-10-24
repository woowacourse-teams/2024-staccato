package com.on.staccato.presentation.staccato

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.on.staccato.R
import com.on.staccato.databinding.FragmentStaccatoBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.common.DeleteDialogFragment
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.staccato.comments.CommentsAdapter
import com.on.staccato.presentation.staccato.comments.StaccatoCommentsViewModel
import com.on.staccato.presentation.staccato.detail.ViewpagePhotoAdapter
import com.on.staccato.presentation.staccato.feeling.StaccatoFeelingSelectionFragment
import com.on.staccato.presentation.staccato.viewmodel.StaccatoViewModel
import com.on.staccato.presentation.staccatoupdate.StaccatoUpdateActivity
import com.on.staccato.presentation.util.showSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaccatoFragment :
    BindingFragment<FragmentStaccatoBinding>(R.layout.fragment_staccato), StaccatoToolbarHandler {
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private val staccatoViewModel: StaccatoViewModel by viewModels()
    private val commentsViewModel: StaccatoCommentsViewModel by viewModels()
    private val commentsAdapter: CommentsAdapter by lazy { CommentsAdapter(commentsViewModel) }
    private val pagePhotoAdapter: ViewpagePhotoAdapter by lazy { ViewpagePhotoAdapter() }
    private val deleteDialog =
        DeleteDialogFragment {
            staccatoViewModel.deleteStaccato(staccatoId)
        }
    private val staccatoId by lazy { arguments?.getLong(STACCATO_ID_KEY) ?: DEFAULT_STACCATO_ID }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        setUpBinding()
        setNavigationClickListener()
        setUpViewPager()
        setUpComments()
        loadStaccato()
        loadComments()
        observeStaccatoViewModel()
        observeCommentsViewModel()
        setStaccatoFeelingFragment(savedInstanceState)
        showErrorToast()
        showExceptionSnackBar()
    }

    override fun onDeleteClicked() {
        deleteDialog.show(parentFragmentManager, DeleteDialogFragment.TAG)
    }

    override fun onUpdateClicked(
        memoryId: Long,
        memoryTitle: String,
    ) {
        val staccatoUpdateLauncher = (activity as MainActivity).staccatoUpdateLauncher
        StaccatoUpdateActivity.startWithResultLauncher(
            staccatoId = staccatoId,
            memoryId = memoryId,
            memoryTitle = memoryTitle,
            context = requireContext(),
            activityLauncher = staccatoUpdateLauncher,
        )
    }

    private fun setUpBinding() {
        binding.lifecycleOwner = this
        binding.toolbarHandler = this
        binding.commentHandler = commentsViewModel
        binding.staccatoViewModel = staccatoViewModel
        binding.commentsViewModel = commentsViewModel
    }

    private fun setNavigationClickListener() {
        binding.toolbarStaccato.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpComments() {
        binding.rvStaccatoComments.adapter = commentsAdapter
        binding.rvStaccatoComments.itemAnimator = null
    }

    private fun setUpViewPager() {
        binding.vpStaccatoPhotoHorizontal.adapter = pagePhotoAdapter
        TabLayoutMediator(
            binding.tabStaccatoPhotoHorizontal,
            binding.vpStaccatoPhotoHorizontal,
        ) { _, _ -> }.attach()
    }

    private fun loadStaccato() {
        staccatoViewModel.loadStaccato(staccatoId)
    }

    private fun loadComments() {
        commentsViewModel.fetchComments(staccatoId)
    }

    private fun observeStaccatoViewModel() {
        observeStaccatoDetail()
        observeStaccatoDelete()
    }

    private fun observeStaccatoDetail() {
        staccatoViewModel.staccatoDetail.observe(viewLifecycleOwner) { staccatoDetail ->
            pagePhotoAdapter.submitList(staccatoDetail.staccatoImageUrls)
        }
    }

    private fun observeStaccatoDelete() {
        staccatoViewModel.isDeleted.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                sharedViewModel.setStaccatosHasUpdated()
                findNavController().popBackStack()
            }
        }
    }

    private fun observeCommentsViewModel() {
        observeComments()
        observeDeletingComment()
        observeSendingComment()
    }

    private fun observeComments() {
        commentsViewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentsAdapter.updateComments(comments)
        }
    }

    private fun observeDeletingComment() {
        commentsViewModel.isDeleteSuccess.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                showToast(getString(R.string.staccato_comment_has_been_deleted))
            }
        }
    }

    private fun observeSendingComment() {
        commentsViewModel.isSendingSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                scrollToBottom()
            }
        }
    }

    private fun scrollToBottom() {
        with(binding.nsvStaccato) {
            viewTreeObserver.addOnGlobalLayoutListener(
                scrollableToBottomListener(),
            )
        }
    }

    private fun NestedScrollView.scrollableToBottomListener() =
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                post {
                    fullScroll(ScrollView.FOCUS_DOWN)
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        }

    private fun setStaccatoFeelingFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val staccatoFeelingSelectionFragment = createFeelingSelectionFragment()
            addFeelingSelectionFragment(staccatoFeelingSelectionFragment)
        }
    }

    private fun createFeelingSelectionFragment(): StaccatoFeelingSelectionFragment {
        val bundle =
            bundleOf(STACCATO_ID_KEY to staccatoId)
        val staccatoFeelingSelectionFragment =
            StaccatoFeelingSelectionFragment().apply {
                arguments = bundle
            }
        return staccatoFeelingSelectionFragment
    }

    private fun addFeelingSelectionFragment(staccatoFeelingSelectionFragment: StaccatoFeelingSelectionFragment) {
        childFragmentManager.beginTransaction()
            .replace(
                R.id.container_staccato_feeling_selection,
                staccatoFeelingSelectionFragment,
            )
            .commit()
    }

    private fun showErrorToast() {
        staccatoViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            showToast(message)
            findNavController().popBackStack()
        }
        commentsViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            showToast(message)
        }
    }

    private fun showExceptionSnackBar() {
        staccatoViewModel.exceptionMessage.observe(viewLifecycleOwner) { message ->
            view?.showSnackBarWithAction(
                message = message,
                actionLabel = R.string.all_retry,
                onAction = ::onRetryAction,
                Snackbar.LENGTH_INDEFINITE,
            )
        }
    }

    private fun onRetryAction() {
        loadStaccato()
        loadComments()
    }

    companion object {
        const val STACCATO_ID_KEY = "staccatoId"
        const val DEFAULT_STACCATO_ID = -1L
    }
}
