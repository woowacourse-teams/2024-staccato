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
import com.on.staccato.presentation.common.ShareManager
import com.on.staccato.presentation.common.clipboard.ClipboardHelper
import com.on.staccato.presentation.common.photo.originalphoto.OriginalPhotoHandler
import com.on.staccato.presentation.common.photo.originalphoto.OriginalPhotoIndex
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.staccato.comments.CommentHandler
import com.on.staccato.presentation.staccato.comments.CommentsAdapter
import com.on.staccato.presentation.staccato.comments.StaccatoCommentsViewModel
import com.on.staccato.presentation.staccato.detail.ViewpagePhotoAdapter
import com.on.staccato.presentation.staccato.feeling.StaccatoFeelingSelectionFragment
import com.on.staccato.presentation.staccato.viewmodel.StaccatoViewModel
import com.on.staccato.presentation.staccatoupdate.StaccatoUpdateActivity
import com.on.staccato.presentation.util.MenuHandler
import com.on.staccato.presentation.util.showPopupMenu
import com.on.staccato.presentation.util.showSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import com.on.staccato.util.logging.AnalyticsEvent
import com.on.staccato.util.logging.LoggingManager
import com.on.staccato.util.logging.Param
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StaccatoFragment :
    BindingFragment<FragmentStaccatoBinding>(R.layout.fragment_staccato),
    StaccatoShareHandler,
    StaccatoToolbarHandler,
    CommentHandler,
    MenuHandler,
    OriginalPhotoHandler {
    @Inject
    lateinit var loggingManager: LoggingManager

    @Inject
    lateinit var shareManager: ShareManager

    @Inject
    lateinit var clipboardHelper: ClipboardHelper

    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private val staccatoViewModel: StaccatoViewModel by viewModels()
    private val commentsViewModel: StaccatoCommentsViewModel by viewModels()
    private val commentsAdapter: CommentsAdapter by lazy { CommentsAdapter(this) }
    private val pagePhotoAdapter: ViewpagePhotoAdapter by lazy { ViewpagePhotoAdapter(this) }
    private val staccatoDeleteDialog =
        DeleteDialogFragment {
            staccatoViewModel.deleteStaccato(staccatoId)
        }

    private val commentDeleteDialog by lazy {
        DeleteDialogFragment {
            commentsViewModel.deleteComment()
        }
    }

    private val staccatoId by lazy {
        arguments?.getLong(STACCATO_ID_KEY) ?: DEFAULT_STACCATO_ID
    }

    private val isStaccatoCreated by lazy {
        arguments?.getBoolean(CREATED_STACCATO_KEY) ?: false
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        if (isStaccatoCreated) sharedViewModel.setStaccatosHasUpdated()
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
        logAccess()
    }

    override fun onShowOriginalPhoto(position: Int) {
        staccatoViewModel.changeOriginalPhotoIndex(OriginalPhotoIndex(position))
    }

    override fun onDeleteClicked() {
        staccatoDeleteDialog.show(parentFragmentManager, DeleteDialogFragment.TAG)
    }

    override fun onUpdateClicked(
        categoryId: Long,
        categoryTitle: String,
    ) {
        val staccatoUpdateLauncher = (activity as MainActivity).staccatoUpdateLauncher
        StaccatoUpdateActivity.startWithResultLauncher(
            staccatoId = staccatoId,
            categoryId = categoryId,
            categoryTitle = categoryTitle,
            context = requireContext(),
            activityLauncher = staccatoUpdateLauncher,
        )
    }

    override fun onStaccatoShareClicked() {
        staccatoViewModel.createStaccatoShareLink()
    }

    override fun onCommentLongClicked(
        view: View,
        gravity: Int,
        id: Long,
    ) {
        commentsViewModel.setSelectedComment(id)
        view.showPopupMenu(
            menuRes = R.menu.menu_comment,
            menuHandler = ::setupActionBy,
            gravity = gravity,
        )
    }

    override fun setupActionBy(menuItemId: Int) {
        when (menuItemId) {
            R.id.comment_delete -> {
                commentDeleteDialog.show(parentFragmentManager, DeleteDialogFragment.TAG)
            }
            R.id.comment_copy -> {
                val comment =
                    commentsViewModel.selectedComment?.content
                        ?: return showToast(getString(R.string.staccato_error_comment_copy))
                clipboardHelper.copyText(
                    label = LABEL_STACCATO_COMMENT,
                    text = comment,
                    context = requireContext(),
                )
            }
        }
    }

    private fun setUpBinding() {
        binding.lifecycleOwner = this
        binding.toolbarHandler = this
        binding.shareHandler = this
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
        ) { tab, _ ->
            tab.setCustomView(R.layout.item_tab_dot)
        }.attach()
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
        observeStaccatoShareLink()
    }

    private fun observeStaccatoDetail() {
        staccatoViewModel.staccatoDetail.observe(viewLifecycleOwner) { staccatoDetail ->
            pagePhotoAdapter.submitList(staccatoDetail.staccatoImageUrls)
            sharedViewModel.updateStaccatoLocation(
                staccatoDetail.latitude,
                staccatoDetail.longitude,
            )
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

    private fun observeStaccatoShareLink() {
        staccatoViewModel.shareEvent.observe(viewLifecycleOwner) { data ->
            shareManager.shareStaccato(
                staccatoTitle = data.staccatoTitle,
                nickname = data.nickname,
                url = data.url,
            )
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

    private fun logAccess() {
        loggingManager.logEvent(
            AnalyticsEvent.NAME_FRAGMENT_PAGE,
            Param(Param.KEY_FRAGMENT_NAME, Param.PARAM_STACCATO_FRAGMENT),
        )
    }

    companion object {
        const val STACCATO_ID_KEY = "staccatoId"
        const val DEFAULT_STACCATO_ID = 0L
        const val CREATED_STACCATO_KEY = "isStaccatoCreated"
        private const val LABEL_STACCATO_COMMENT = "staccatoComment"
    }
}
