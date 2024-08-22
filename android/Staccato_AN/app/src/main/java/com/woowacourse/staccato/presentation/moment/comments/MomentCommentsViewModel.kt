package com.woowacourse.staccato.presentation.moment.comments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.data.dto.Status
import com.woowacourse.staccato.domain.repository.CommentRepository
import com.woowacourse.staccato.presentation.mapper.toCommentUiModel
import kotlinx.coroutines.launch

class MomentCommentsViewModel(
    private val momentId: Long,
    private val commentRepository: CommentRepository,
) : ViewModel() {
    private val _comments = MutableLiveData<List<CommentUiModel>>()
    val comments: LiveData<List<CommentUiModel>>
        get() = _comments

    val isEmpty: LiveData<Boolean> = _comments.map { it.isEmpty() }

    fun fetchComments() {
        viewModelScope.launch {
            commentRepository.fetchComments(momentId)
                .onSuccess { comments ->
                    setComments(comments.map { it.toCommentUiModel() })
                }
                .onServerError(::handleServerError)
                .onException(::handleException)
        }
    }

    fun setComments(
        newComments: List<CommentUiModel>
    ) {
        _comments.value = newComments
    }

    private fun handleServerError(
        status: Status,
        message: String,
    ) {
        when (status) {
            is Status.Code -> Log.e(
                this.javaClass.simpleName,
                "ServerError(${status.code}): $message"
            )

            is Status.Message -> Log.e(
                this.javaClass.simpleName,
                "ServerError(${status.message}): $message"
            )
        }
    }

    private fun handleException(
        e: Throwable,
        message: String,
    ) {
        Log.e(this.javaClass.simpleName, "Exception($e): $message")
    }
}
