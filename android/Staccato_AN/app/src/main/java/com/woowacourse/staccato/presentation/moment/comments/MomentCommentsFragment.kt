package com.woowacourse.staccato.presentation.moment.comments

import android.os.Bundle
import android.view.View
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentMomentCommentsBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import kotlin.properties.Delegates

class MomentCommentsFragment :
    BindingFragment<FragmentMomentCommentsBinding>(R.layout.fragment_moment_comments) {
    private lateinit var commentsAdapter: CommentsAdapter
    private var momentId by Delegates.notNull<Long>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        momentId = arguments?.getLong(MOMENT_ID_KEY) ?: return
        initAdapter()
    }

    private fun initAdapter() {
        commentsAdapter = CommentsAdapter()
        binding.rvMomentComments.adapter = commentsAdapter
    }

    companion object {
        const val MOMENT_ID_KEY = "momentId"
    }
}
