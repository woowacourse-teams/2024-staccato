package com.woowacourse.staccato.presentation.moment.comments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentMomentCommentsBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.moment.viewmodel.MomentViewModel
import com.woowacourse.staccato.presentation.moment.viewmodel.MomentViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast
import kotlin.properties.Delegates

class MomentCommentsFragment :
    BindingFragment<FragmentMomentCommentsBinding>(R.layout.fragment_moment_comments) {
    private lateinit var commentsAdapter: CommentsAdapter
    private var momentId by Delegates.notNull<Long>()
    private val commentsViewModel: MomentCommentsViewModel by viewModels {
        MomentCommentsViewModelFactory(momentId)
    }
    private val momentViewModel: MomentViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    ) {
        MomentViewModelFactory()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        momentId = arguments?.getLong(MOMENT_ID_KEY) ?: return
        initAdapter()
        binding.viewModel = commentsViewModel
        binding.handler = commentsViewModel
        observeMomentViewModel()
        observeCommentsViewModel()
    }

    private fun initAdapter() {
        commentsAdapter = CommentsAdapter(commentsViewModel)
        binding.rvMomentComments.adapter = commentsAdapter
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
    }

    companion object {
        const val MOMENT_ID_KEY = "momentId"
    }
}
