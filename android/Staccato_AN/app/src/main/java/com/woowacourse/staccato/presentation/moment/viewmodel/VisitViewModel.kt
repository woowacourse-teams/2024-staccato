package com.woowacourse.staccato.presentation.moment.viewmodel

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

class VisitViewModel(private val momentRepository: MomentRepository) : ViewModel() {
    private val _visitDefault = MutableLiveData<MomentDetailUiModel.MomentDefaultUiModel>()
    val visitDefault: LiveData<MomentDetailUiModel.MomentDefaultUiModel> get() = _visitDefault

    private val _visitLogs = MutableLiveData<List<MomentDetailUiModel.VisitLogUiModel>>()
    val visitLogs: LiveData<List<MomentDetailUiModel.VisitLogUiModel>> get() = _visitLogs

    private val _isDeleted = MutableSingleLiveData(false)
    val isDeleted: SingleLiveData<Boolean> get() = _isDeleted

    private val _isError = MutableSingleLiveData(false)
    val isError: SingleLiveData<Boolean> get() = _isError

    fun fetchVisitDetailData(visitId: Long) {
        fetchVisitData(visitId)
    }

    fun deleteVisit(visitId: Long) =
        viewModelScope.launch {
            momentRepository.deleteMoment(visitId).onSuccess {
                _isDeleted.postValue(true)
            }
        }

    private fun fetchVisitData(visitId: Long) {
        viewModelScope.launch {
            momentRepository.getMoment(visitId).onSuccess { visit ->
                _visitDefault.value = visit.toVisitDefaultUiModel()
                _visitLogs.value = visit.comments.map { it.toVisitLogUiModel() }
            }.onFailure {
                _isError.postValue(true)
            }
        }
    }
}
