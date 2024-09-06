package com.on.staccato.presentation.moment.comments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import com.on.staccato.R
import com.on.staccato.databinding.FragmentMomentCommentsBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.moment.viewmodel.MomentViewModel
import com.on.staccato.presentation.moment.viewmodel.MomentViewModelFactory
import com.on.staccato.presentation.util.showToast
import kotlin.properties.Delegates

class MomentCommentsFragment :
    BindingFragment<FragmentMomentCommentsBinding>(R.layout.fragment_moment_comments),
    KeyboardActionHandler {
    private lateinit var commentsAdapter: CommentsAdapter
    private var momentId by Delegates.notNull<Long>()
    private val commentsViewModel: MomentCommentsViewModel by viewModels {
        MomentCommentsViewModelFactory(momentId)
    }
    private val momentViewModel: MomentViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
    ) {
        MomentViewModelFactory()
    }
    private val inputManager: InputMethodManager by lazy {
        requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        momentId = arguments?.getLong(MOMENT_ID_KEY) ?: return
        initAdapter()
        setUpBinding()
        observeMomentViewModel()
        observeCommentsViewModel()
    }

    private fun initAdapter() {
        commentsAdapter = CommentsAdapter(commentsViewModel)
        binding.rvMomentComments.adapter = commentsAdapter
    }

    private fun setUpBinding() {
        binding.viewModel = commentsViewModel
        binding.commentHandler = commentsViewModel
        binding.keyboardHandler = this
    }

    private fun observeMomentViewModel() {
        momentViewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentsViewModel.setComments(comments)
        }
    }

    private fun observeCommentsViewModel() {
        commentsViewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentsAdapter.updateComments(comments)
        }

        commentsViewModel.isDeleteSuccess.observe(viewLifecycleOwner) { issDeleted ->
            if (issDeleted) {
                showToast(getString(R.string.moment_comment_has_been_deleted))
            }
        }

        commentsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                requireActivity().window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
            } else {
                requireActivity().window.clearFlags(FLAG_NOT_TOUCHABLE)
            }
        }
    }

    override fun onScreenTouchEvent() {
        requireParentFragment().view?.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
    }

    private fun hideKeyboard() {
        requireActivity().currentFocus?.let { view ->
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS,
            )
            view.clearFocus()
        }
    }

    companion object {
        const val MOMENT_ID_KEY = "momentId"
    }
}
