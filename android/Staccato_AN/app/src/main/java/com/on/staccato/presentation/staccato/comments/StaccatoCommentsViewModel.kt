package com.on.staccato.presentation.staccato.comments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onServerError
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.dto.Status
import com.on.staccato.domain.model.NewComment
import com.on.staccato.domain.repository.CommentRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toCommentUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaccatoCommentsViewModel
    @Inject
    constructor(
        private val commentRepository: CommentRepository,
    ) : ViewModel(), CommentHandler {
        private val _comments = MutableLiveData<List<CommentUiModel>>()
        val comments: LiveData<List<CommentUiModel>>
            get() = _comments

        val isEmpty: LiveData<Boolean> = _comments.map { it.isEmpty() }

        val commentInput = MutableLiveData<String>("")

        private val _isDeleteSuccess = MutableSingleLiveData<Boolean>()
        val isDeleteSuccess: SingleLiveData<Boolean>
            get() = _isDeleteSuccess

        private val _isLoading = MutableSingleLiveData(false)
        val isLoading: SingleLiveData<Boolean>
            get() = _isLoading

        private var staccatoId: Long = -1L

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

        fun fetchComments() {
            viewModelScope.launch {
                commentRepository.fetchComments(staccatoId)
                    .onSuccess { comments ->
                        setComments(comments.map { it.toCommentUiModel() })
                    }
                    .onServerError(::handleServerError)
                    .onException(::handleException)
            }
        }

        fun setMemoryId(id: Long) {
            staccatoId = id
        }

        fun setComments(newComments: List<CommentUiModel>) {
            _comments.value = newComments
        }

        private fun sendComment() {
            commentInput.value?.let {
                _isLoading.setValue(true)
                val newComment = NewComment(staccatoId, it)
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
        }

        private fun handleException(
            e: Throwable,
            message: String,
        ) {
            _isLoading.postValue(false)
        }
    }
