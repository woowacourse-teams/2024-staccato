package com.on.staccato.presentation.staccato.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.onException2
import com.on.staccato.data.onServerError
import com.on.staccato.data.onSuccess
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toStaccatoDetailUiModel
import com.on.staccato.presentation.staccato.comments.CommentUiModel
import com.on.staccato.presentation.staccato.detail.StaccatoDetailUiModel
import com.on.staccato.presentation.util.ExceptionState2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaccatoViewModel
    @Inject
    constructor(
        private val staccatoRepository: StaccatoRepository,
    ) : ViewModel() {
        private val _staccatoDetail = MutableLiveData<StaccatoDetailUiModel>()
        val staccatoDetail: LiveData<StaccatoDetailUiModel> get() = _staccatoDetail

        private val _comments = MutableLiveData<List<CommentUiModel>>()
        val comments: LiveData<List<CommentUiModel>> get() = _comments

        private val _feeling = MutableLiveData<Feeling>()
        val feeling: LiveData<Feeling> get() = _feeling

        private val _isDeleted = MutableSingleLiveData(false)
        val isDeleted: SingleLiveData<Boolean> get() = _isDeleted

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _exception: MutableSingleLiveData<ExceptionState2> = MutableSingleLiveData()
        val exception: SingleLiveData<ExceptionState2> get() = _exception

        fun loadStaccato(staccatoId: Long) {
            fetchStaccatoData(staccatoId)
        }

        fun deleteStaccato(staccatoId: Long) =
            viewModelScope.launch {
                staccatoRepository.deleteStaccato(staccatoId)
                    .onSuccess {
                        _isDeleted.postValue(true)
                    }.onException2(::handleException)
                    .onServerError(::handleServerError)
            }

        private fun fetchStaccatoData(staccatoId: Long) {
            viewModelScope.launch {
                staccatoRepository.getStaccato(staccatoId)
                    .onSuccess { staccato ->
                        _staccatoDetail.value = staccato.toStaccatoDetailUiModel()
                        _feeling.value = staccato.feeling
                    }.onException2(::handleException)
                    .onServerError(::handleServerError)
            }
        }

        private fun handleServerError(errorMessage: String) {
            _errorMessage.postValue(errorMessage)
        }

        private fun handleException(state: ExceptionState2) {
            _exception.setValue(state)
        }
    }
