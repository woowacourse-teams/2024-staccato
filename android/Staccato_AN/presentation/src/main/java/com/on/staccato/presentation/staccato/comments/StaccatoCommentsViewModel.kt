package com.on.staccato.presentation.staccato.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.ExceptionType
import com.on.staccato.domain.model.Comment
import com.on.staccato.domain.model.NewComment
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.CommentRepository
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.common.event.MutableSingleLiveData
import com.on.staccato.presentation.common.event.SingleLiveData
import com.on.staccato.presentation.mapper.toCommentUiModel
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

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent>
            get() = _messageEvent

        private var staccatoId: Long = STACCATO_DEFAULT_ID

        private var myMemberId: Long = 0L

        var selectedComment: CommentUiModel? = null
            private set

        init {
            fetchMemberId()
        }

        fun deleteComment() {
            viewModelScope.launch {
                commentRepository.deleteComment(
                    selectedComment?.id ?: return@launch handleMessageEvent(MessageEvent.from(ExceptionType.UNKNOWN)),
                )
                    .onSuccess {
                        fetchComments(staccatoId)
                        _isDeleteSuccess.postValue(true)
                    }
                    .onServerError { handleMessageEvent(MessageEvent.from(it)) }
                    .onException { handleMessageEvent(MessageEvent.from(it)) }
            }
        }

        fun fetchComments(id: Long) {
            updateStaccatoId(id)
            viewModelScope.launch {
                commentRepository.fetchComments(id)
                    .onSuccess(::updateComments)
                    .onServerError { handleMessageEvent(MessageEvent.from(it)) }
                    .onException { handleMessageEvent(MessageEvent.from(it)) }
            }
        }

        fun setSelectedComment(id: Long) {
            selectedComment = _comments.value?.find { it.id == id }
        }

        fun sendComment() {
            commentInput.value?.let { commentText ->
                val newComment = NewComment(staccatoId, commentText)
                commentInput.value = ""
                viewModelScope.launch {
                    commentRepository.createComment(newComment)
                        .onSuccess {
                            fetchComments(staccatoId)
                            _isSendingSuccess.postValue(true)
                        }
                        .onServerError { handleMessageEvent(MessageEvent.from(it)) }
                        .onException { handleMessageEvent(MessageEvent.from(it)) }
                }
            }
        }

        private fun fetchMemberId() {
            viewModelScope.launch {
                memberRepository.getMemberId()
                    .onSuccess { myMemberId = it }
                    .onFailure { handleMessageEvent(MessageEvent.from(ExceptionType.UNKNOWN)) }
            }
        }

        private fun updateComments(newComments: List<Comment>) {
            _comments.value =
                newComments.map { comment ->
                    val isMine = comment.memberId == myMemberId
                    comment.toCommentUiModel(isMine)
                }
        }

        private fun updateStaccatoId(id: Long) {
            if (staccatoId == STACCATO_DEFAULT_ID) {
                staccatoId = id
            }
        }

        private fun handleMessageEvent(messageEvent: MessageEvent) {
            _messageEvent.setValue(messageEvent)
        }

        companion object {
            const val STACCATO_DEFAULT_ID = -1L
        }
    }
