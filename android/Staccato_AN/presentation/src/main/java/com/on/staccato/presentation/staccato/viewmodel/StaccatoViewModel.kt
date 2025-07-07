package com.on.staccato.presentation.staccato.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.ExceptionType
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.StaccatoShareLink
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.common.event.MutableSingleLiveData
import com.on.staccato.presentation.common.event.SingleLiveData
import com.on.staccato.presentation.mapper.toStaccatoDetailUiModel
import com.on.staccato.presentation.photo.originalphoto.OriginalPhotoIndex
import com.on.staccato.presentation.staccato.detail.StaccatoDetailUiModel
import com.on.staccato.presentation.staccato.detail.StaccatoShareEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaccatoViewModel
    @Inject
    constructor(
        private val memberRepository: MemberRepository,
        private val staccatoRepository: StaccatoRepository,
    ) : ViewModel() {
        private val _staccatoDetail = MutableLiveData<StaccatoDetailUiModel>()
        val staccatoDetail: LiveData<StaccatoDetailUiModel> get() = _staccatoDetail

        private val _feeling = MutableLiveData<Feeling>()
        val feeling: LiveData<Feeling> get() = _feeling

        private val _isDeleted = MutableSingleLiveData(false)
        val isDeleted: SingleLiveData<Boolean> get() = _isDeleted

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent> get() = _messageEvent

        private val _shareEvent = MutableSingleLiveData<StaccatoShareEvent>()
        val shareEvent: SingleLiveData<StaccatoShareEvent> get() = _shareEvent

        private val _originalPhotoIndex = MutableStateFlow(OriginalPhotoIndex.unavailable)
        val originalPhotoIndex: StateFlow<OriginalPhotoIndex> get() = _originalPhotoIndex

        fun createStaccatoShareLink() {
            val staccatoId = staccatoDetail.value?.id
            if (staccatoId == null) {
                emitMessageEvent(MessageEvent.from(ExceptionType.UNKNOWN))
                return
            }
            getUserNickname(staccatoId)
        }

        fun changeOriginalPhotoIndex(originalPhotoIndex: OriginalPhotoIndex) {
            _originalPhotoIndex.value = originalPhotoIndex
        }

        private fun getUserNickname(staccatoId: Long) {
            viewModelScope.launch {
                memberRepository.getNickname()
                    .onSuccess { shareStaccato(staccatoId, it) }
                    .onFailure { emitMessageEvent(MessageEvent.from(ExceptionType.UNKNOWN)) }
            }
        }

        private suspend fun shareStaccato(
            staccatoId: Long,
            nickName: String,
        ) {
            staccatoRepository.createStaccatoShareLink(staccatoId)
                .onSuccess {
                    postShareEvent(nickName, it)
                }
                .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
                .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
        }

        private fun postShareEvent(
            nickname: String,
            url: StaccatoShareLink,
        ) {
            val staccatoTitle = staccatoDetail.value?.staccatoTitle ?: return
            _shareEvent.postValue(
                StaccatoShareEvent(
                    staccatoTitle = staccatoTitle,
                    nickname = nickname,
                    url = url.shareLink,
                ),
            )
        }

        fun loadStaccato(staccatoId: Long) {
            fetchStaccatoData(staccatoId)
        }

        fun deleteStaccato(staccatoId: Long) =
            viewModelScope.launch {
                staccatoRepository.deleteStaccato(staccatoId)
                    .onSuccess {
                        _isDeleted.postValue(true)
                    }
                    .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
                    .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
            }

        private fun fetchStaccatoData(staccatoId: Long) {
            viewModelScope.launch {
                staccatoRepository.getStaccato(staccatoId)
                    .onSuccess { staccato ->
                        _staccatoDetail.value = staccato.toStaccatoDetailUiModel()
                        _feeling.value = staccato.feeling
                    }
                    .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
                    .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
            }
        }

        private fun emitMessageEvent(messageEvent: MessageEvent) {
            _messageEvent.setValue(messageEvent)
        }
    }
