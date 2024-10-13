package com.on.staccato.presentation.staccato.comments

import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import androidx.fragment.app.viewModels
import com.on.staccato.R
import com.on.staccato.databinding.FragmentStaccatoCommentsBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.DEFAULT_STACCATO_ID
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaccatoCommentsFragment :
    BindingFragment<FragmentStaccatoCommentsBinding>(R.layout.fragment_staccato_comments) {
    private lateinit var commentsAdapter: CommentsAdapter

    private val staccatoId by lazy { arguments?.getLong(STACCATO_ID_KEY) ?: DEFAULT_STACCATO_ID }
    private val commentsViewModel: StaccatoCommentsViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        commentsViewModel.setMemoryId(staccatoId)
        setUpRecyclerView()
        setUpBinding()
        observeCommentsViewModel()
        loadComments()
    }

    private fun setUpRecyclerView() {
        commentsAdapter = CommentsAdapter(commentsViewModel)
        binding.rvStaccatoComments.adapter = commentsAdapter
        binding.rvStaccatoComments.itemAnimator = null
    }

    private fun setUpBinding() {
        binding.viewModel = commentsViewModel
        binding.commentHandler = commentsViewModel
    }

    private fun loadComments() {
        commentsViewModel.fetchComments()
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
}
