package com.on.staccato.presentation.moment.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.on.staccato.data.StaccatoClient.commentApiService
import com.on.staccato.data.comment.CommentDefaultRepository
import com.on.staccato.data.comment.CommentRemoteDataSource

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
