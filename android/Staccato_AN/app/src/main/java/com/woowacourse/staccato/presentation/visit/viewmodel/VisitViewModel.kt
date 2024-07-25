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

class VisitViewModel(private val repository: VisitRepository) : ViewModel() {
    private val _visitDefault = MutableLiveData<VisitDetailUiModel.VisitDefaultUiModel>()
    val visitDefault: LiveData<VisitDetailUiModel.VisitDefaultUiModel> get() = _visitDefault

    private val _visitLogs = MutableLiveData<List<VisitDetailUiModel.VisitLogUiModel>>()
    val visitLogs: LiveData<List<VisitDetailUiModel.VisitLogUiModel>> get() = _visitLogs

    private val _isError = MutableSingleLiveData(false)
    val isError: SingleLiveData<Boolean> get() = _isError

    fun fetchVisitDetailData(visitId: Long) {
        fetchVisitData(visitId)
    }

    private fun fetchVisitData(visitId: Long) {
        viewModelScope.launch {
            repository.loadVisit(visitId = visitId).onSuccess { visitDetail ->
                _visitDefault.value = visitDetail.toVisitDefaultUiModel()
                _visitLogs.value = visitDetail.visitLogs.map { it.toVisitLogUiModel() }
            }.onFailure {
                _isError.postValue(true)
            }
        }
    }
}
