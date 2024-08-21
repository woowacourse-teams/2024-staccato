package com.woowacourse.staccato.presentation.moment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.repository.MomentRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toVisitDefaultUiModel
import com.woowacourse.staccato.presentation.mapper.toVisitLogUiModel
import com.woowacourse.staccato.presentation.moment.model.MomentDetailUiModel
import kotlinx.coroutines.launch

class MomentViewModel(private val momentRepository: MomentRepository) : ViewModel() {
    private val _momentDefault = MutableLiveData<MomentDetailUiModel.MomentDefaultUiModel>()
    val momentDefault: LiveData<MomentDetailUiModel.MomentDefaultUiModel> get() = _momentDefault

    private val _visitLogs = MutableLiveData<List<MomentDetailUiModel.CommentsUiModel>>()
    val visitLogs: LiveData<List<MomentDetailUiModel.CommentsUiModel>> get() = _visitLogs

    private val _isDeleted = MutableSingleLiveData(false)
    val isDeleted: SingleLiveData<Boolean> get() = _isDeleted

    private val _isError = MutableSingleLiveData(false)
    val isError: SingleLiveData<Boolean> get() = _isError

    fun fetchMomentDetailData(momentId: Long) {
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
                _momentDefault.value = moment.toVisitDefaultUiModel()
                _visitLogs.value = moment.comments.map { it.toVisitLogUiModel() }
            }.onFailure {
                Log.d("ㅌㅅㅌ moment", "error : ${it.message}")
                _isError.postValue(true)
            }
        }
    }
}
