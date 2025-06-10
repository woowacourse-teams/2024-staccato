package com.on.staccato.presentation.staccato.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.network.onException
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.StaccatoShareLink
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.common.photo.originalphoto.OriginalPhotoIndex
import com.on.staccato.presentation.mapper.toStaccatoDetailUiModel
import com.on.staccato.presentation.staccato.detail.StaccatoDetailUiModel
import com.on.staccato.presentation.staccato.detail.StaccatoShareEvent
import com.on.staccato.presentation.util.ExceptionState
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

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _exceptionMessage: MutableSingleLiveData<String> = MutableSingleLiveData()
        val exceptionMessage: SingleLiveData<String> get() = _exceptionMessage

        private val _shareEvent = MutableSingleLiveData<StaccatoShareEvent>()
        val shareEvent: SingleLiveData<StaccatoShareEvent> get() = _shareEvent

        private val _originalPhotoIndex = MutableStateFlow(OriginalPhotoIndex.unavailable)
        val originalPhotoIndex: StateFlow<OriginalPhotoIndex> get() = _originalPhotoIndex

        fun createStaccatoShareLink() {
            val staccatoId = staccatoDetail.value?.id
            if (staccatoId == null) {
                handleException(ExceptionState.UnknownError)
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
                    .onSuccess {
                        shareStaccato(staccatoId, it)
                    }.onFailure {
                        handleException(ExceptionState.UnknownError)
                    }
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
                .onException(::handleException)
                .onServerError(::handleServerError)
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
                    }.onException(::handleException)
                    .onServerError(::handleServerError)
            }

        private fun fetchStaccatoData(staccatoId: Long) {
            viewModelScope.launch {
                staccatoRepository.getStaccato(staccatoId)
                    .onSuccess { staccato ->
                        _staccatoDetail.value = staccato.toStaccatoDetailUiModel()
                        _feeling.value = staccato.feeling
                    }.onException(::handleException)
                    .onServerError(::handleServerError)
            }
        }

        private fun handleServerError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionState) {
            _exceptionMessage.postValue(state.message)
        }
    }
