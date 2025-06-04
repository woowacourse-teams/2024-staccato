package com.on.staccato.presentation.staccato.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.network.onException
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.model.Comment
import com.on.staccato.domain.model.NewComment
import com.on.staccato.domain.repository.CommentRepository
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toCommentUiModel
import com.on.staccato.presentation.util.ExceptionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaccatoCommentsViewModel
    @Inject
    constructor(
        private val memberRepository: MemberRepository,
        private val commentRepository: CommentRepository,
    ) : ViewModel() {
        private val _comments = MutableLiveData<List<CommentUiModel>>()
        val comments: LiveData<List<CommentUiModel>>
            get() = _comments

        val isEmpty: LiveData<Boolean> = _comments.map { it.isEmpty() }

        val commentInput = MutableLiveData("")

        private val _isDeleteSuccess = MutableSingleLiveData<Boolean>()
        val isDeleteSuccess: SingleLiveData<Boolean>
            get() = _isDeleteSuccess

        private val _isSendingSuccess = MutableSingleLiveData(false)
        val isSendingSuccess: SingleLiveData<Boolean>
            get() = _isSendingSuccess

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String>
            get() = _errorMessage

        private var staccatoId: Long = STACCATO_DEFAULT_ID

        private val myMemberId = MutableSingleLiveData<Long>()

        private val commentId = MutableLiveData<Long>()

        init {
            fetchMemberId()
        }

        fun deleteComment() {
            viewModelScope.launch {
                commentRepository.deleteComment(commentId.value ?: return@launch handleException(ExceptionState.UnknownError))
                    .onSuccess {
                        fetchComments(staccatoId)
                        _isDeleteSuccess.postValue(true)
                    }
                    .onServerError(::handleServerError)
                    .onException(::handleException)
            }
        }

        fun fetchComments(id: Long) {
            setStaccatoId(id)
            viewModelScope.launch {
                commentRepository.fetchComments(id)
                    .onSuccess(::setComments)
                    .onServerError(::handleServerError)
                    .onException(::handleException)
            }
        }

        fun updateCommentId(id: Long) {
            commentId.value = id
        }

        fun sendComment() {
            commentInput.value?.let {
                val newComment = NewComment(staccatoId, it)
                commentInput.value = ""
                viewModelScope.launch {
                    commentRepository.createComment(newComment)
                        .onSuccess {
                            fetchComments(staccatoId)
                            _isSendingSuccess.postValue(true)
                        }
                        .onServerError(::handleServerError)
                        .onException(::handleException)
                }
            }
        }

        private fun fetchMemberId() {
            viewModelScope.launch {
                memberRepository.getMemberId()
                    .onSuccess { myMemberId.setValue(it) }
                    .onFailure {
                        handleException(ExceptionState.UnknownError)
                    }
            }
        }

        private fun setComments(newComments: List<Comment>) {
            _comments.value =
                newComments.map { comment ->
                    val isMine = comment.memberId == myMemberId.getValue()
                    comment.toCommentUiModel(isMine)
                }
        }

        private fun setStaccatoId(id: Long) {
            if (staccatoId == STACCATO_DEFAULT_ID) {
                staccatoId = id
            }
        }

        private fun handleServerError(message: String) {
            _errorMessage.postValue(message)
        }

        private fun handleException(state: ExceptionState) {
            _errorMessage.postValue(state.message)
        }

        companion object {
            const val STACCATO_DEFAULT_ID = -1L
        }
    }
