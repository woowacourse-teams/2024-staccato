package com.woowacourse.staccato.presentation.moment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.model.Feeling
import com.woowacourse.staccato.domain.repository.MomentRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toCommentUiModel
import com.woowacourse.staccato.presentation.mapper.toMomentDetailUiModel
import com.woowacourse.staccato.presentation.moment.comments.CommentUiModel
import com.woowacourse.staccato.presentation.moment.detail.MomentDetailUiModel
import kotlinx.coroutines.launch

class MomentViewModel(private val momentRepository: MomentRepository) : ViewModel() {
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
                _comments.value = moment.comments.map { it.toCommentUiModel() }
            }.onFailure {
                Log.d("ㅌㅅㅌ moment", "error : ${it.message}")
                _isError.postValue(true)
            }
        }
    }
}
