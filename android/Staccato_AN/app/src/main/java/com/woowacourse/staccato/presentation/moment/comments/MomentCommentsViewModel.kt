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
import com.woowacourse.staccato.domain.model.NewComment
import com.woowacourse.staccato.domain.repository.CommentRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toCommentUiModel
import kotlinx.coroutines.launch

class MomentCommentsViewModel(
    private val momentId: Long,
    private val commentRepository: CommentRepository,
) : ViewModel(), CommentHandler {
    private val _comments = MutableLiveData<List<CommentUiModel>>()
    val comments: LiveData<List<CommentUiModel>>
        get() = _comments

    val isEmpty: LiveData<Boolean> = _comments.map { it.isEmpty() }

    val commentInput = MutableLiveData<String>()

    private val _isDeleteSuccess = MutableSingleLiveData<Boolean>()
    val isDeleteSuccess: SingleLiveData<Boolean>
        get() = _isDeleteSuccess

    private val _isLoading = MutableSingleLiveData(false)
    val isLoading: SingleLiveData<Boolean>
        get() = _isLoading

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

    fun setComments(newComments: List<CommentUiModel>) {
        _comments.value = newComments
    }

    fun sendComment() {
        commentInput.value?.let {
            _isLoading.setValue(true)
            val newComment = NewComment(momentId, it)
            viewModelScope.launch {
                commentRepository.createComment(newComment)
                    .onSuccess {
                        commentInput.value = ""
                        fetchComments()
                        _isLoading.postValue(false)
                    }
                    .onServerError(::handleServerError)
                    .onException(::handleException)
            }
        }
    }

    private fun handleServerError(
        status: Status,
        message: String,
    ) {
        _isLoading.postValue(false)
        when (status) {
            is Status.Code ->
                Log.e(
                    this.javaClass.simpleName,
                    "ServerError(${status.code}): $message",
                )

            is Status.Message ->
                Log.e(
                    this.javaClass.simpleName,
                    "ServerError(${status.message}): $message",
                )
        }
    }

    private fun handleException(
        e: Throwable,
        message: String,
    ) {
        _isLoading.postValue(false)
        Log.e(this.javaClass.simpleName, "Exception($e): $message")
    }

    override fun onSendButtonClicked() {
        viewModelScope.launch {
            sendComment()
        }
    }

    override fun onUpdateButtonClicked(commentId: Long) {
        Log.d("hodu", "not implemented yet")
    }

    override fun onDeleteButtonClicked(commentId: Long) {
        viewModelScope.launch {
            commentRepository.deleteComment(commentId)
                .onSuccess {
                    fetchComments()
                    _isDeleteSuccess.postValue(true)
                }
                .onServerError(::handleServerError)
                .onException(::handleException)
        }
    }
}
