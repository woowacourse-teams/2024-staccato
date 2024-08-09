package com.woowacourse.staccato.presentation.visit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.repository.VisitRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toVisitDefaultUiModel
import com.woowacourse.staccato.presentation.mapper.toVisitLogUiModel
import com.woowacourse.staccato.presentation.visit.model.VisitDetailUiModel
import kotlinx.coroutines.launch

class VisitViewModel(private val visitRepository: VisitRepository) : ViewModel() {
    private val _visitDefault = MutableLiveData<VisitDetailUiModel.VisitDefaultUiModel>()
    val visitDefault: LiveData<VisitDetailUiModel.VisitDefaultUiModel> get() = _visitDefault

    private val _visitLogs = MutableLiveData<List<VisitDetailUiModel.VisitLogUiModel>>()
    val visitLogs: LiveData<List<VisitDetailUiModel.VisitLogUiModel>> get() = _visitLogs

    private val _isDeleted = MutableSingleLiveData(false)
    val isDeleted: SingleLiveData<Boolean> get() = _isDeleted

    private val _isError = MutableSingleLiveData(false)
    val isError: SingleLiveData<Boolean> get() = _isError

    fun fetchVisitDetailData(visitId: Long) {
        fetchVisitData(visitId)
    }

    fun deleteVisit(visitId: Long) =
        viewModelScope.launch {
            visitRepository.deleteVisit(visitId).onSuccess {
                _isDeleted.postValue(true)
            }
        }

    private fun fetchVisitData(visitId: Long) {
        viewModelScope.launch {
            visitRepository.getVisit(visitId).onSuccess { visit ->
                _visitDefault.value = visit.toVisitDefaultUiModel()
                _visitLogs.value = visit.visitLogs.map { it.toVisitLogUiModel() }
            }.onFailure {
                _isError.postValue(true)
            }
        }
    }
}
