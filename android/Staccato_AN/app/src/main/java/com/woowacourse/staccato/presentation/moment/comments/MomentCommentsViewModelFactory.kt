package com.woowacourse.staccato.presentation.moment.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.StaccatoClient.commentApiService
import com.woowacourse.staccato.data.comment.CommentDefaultRepository
import com.woowacourse.staccato.data.comment.CommentRemoteDataSource

class MomentCommentsViewModelFactory(private val momentId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MomentCommentsViewModel::class.java)) {
            return MomentCommentsViewModel(
                momentId,
                CommentDefaultRepository(
                    commentDataSource = CommentRemoteDataSource(commentApiService),
                ),
            ) as T
        }
        throw IllegalArgumentException()
    }
}
