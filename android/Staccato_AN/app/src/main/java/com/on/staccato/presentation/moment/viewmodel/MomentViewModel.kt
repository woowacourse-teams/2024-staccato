package com.on.staccato.presentation.moment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.repository.MomentRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toMomentDetailUiModel
import com.on.staccato.presentation.moment.comments.CommentUiModel
import com.on.staccato.presentation.moment.detail.MomentDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MomentViewModel
    @Inject
    constructor(
        private val momentRepository: MomentRepository,
    ) : ViewModel() {
        private val _momentDetail = MutableLiveData<MomentDetailUiModel>()
        val momentDetail: LiveData<MomentDetailUiModel> get() = _momentDetail

        private val _comments = MutableLiveData<List<CommentUiModel>>()
        val comments: LiveData<List<CommentUiModel>> get() = _comments

        private val _feeling = MutableLiveData<Feeling>()
        val feeling: LiveData<Feeling> get() = _feeling

        private val _isDeleted = MutableSingleLiveData(false)
        val isDeleted: SingleLiveData<Boolean> get() = _isDeleted

        private val _isError = MutableSingleLiveData(false)
        val isError: SingleLiveData<Boolean> get() = _isError

        fun loadMoment(momentId: Long) {
            fetchMomentData(momentId)
        }

        fun deleteMoment(momentId: Long) =
            viewModelScope.launch {
                momentRepository.deleteMoment(momentId).onSuccess {
                    _isDeleted.postValue(true)
                }
            }

        private fun fetchMomentData(momentId: Long) {
            viewModelScope.launch {
                momentRepository.getMoment(momentId).onSuccess { moment ->
                    _momentDetail.value = moment.toMomentDetailUiModel()
                    _feeling.value = moment.feeling
                }.onFailure {
                    _isError.postValue(true)
                }
            }
        }
    }
