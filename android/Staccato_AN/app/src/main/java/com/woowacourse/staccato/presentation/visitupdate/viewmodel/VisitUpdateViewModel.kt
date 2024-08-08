package com.woowacourse.staccato.presentation.visitupdate.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.repository.VisitRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toVisitUpdateDefaultUiModel
import com.woowacourse.staccato.presentation.visitcreation.model.VisitTravelUiModel
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateUiModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class VisitUpdateViewModel(
    private val visitRepository: VisitRepository,
) : ViewModel() {
    private val _visitUpdateData = MutableLiveData<VisitUpdateUiModel>()
    val visitUpdateData: LiveData<VisitUpdateUiModel> get() = _visitUpdateData

    private val _travel = MutableLiveData<VisitTravelUiModel>()
    val travel: LiveData<VisitTravelUiModel> get() = _travel

    private val _visitedAt = MutableLiveData<LocalDateTime?>()
    val visitedAt: LiveData<LocalDateTime?> get() = _visitedAt

    private val _isError = MutableSingleLiveData(false)
    val isError: SingleLiveData<Boolean> get() = _isError

    private val _isUpdateCompleted = MutableSingleLiveData(false)
    val isUpdateCompleted: SingleLiveData<Boolean> get() = _isUpdateCompleted

    private val _selectedImages = MutableLiveData<List<Uri>>()
    val selectedImages: LiveData<List<Uri>> get() = _selectedImages

    fun fetchInitData(
        visitId: Long,
        travelId: Long,
    ) {
        fetchVisitUpdateData(visitId)
        fetchTravel(travelId)
    }

    private fun fetchVisitUpdateData(visitId: Long) {
        viewModelScope.launch {
            visitRepository.getVisit(visitId = visitId).onSuccess { visit ->
                _visitUpdateData.value =
                    visit.toVisitUpdateDefaultUiModel()
            }.onFailure {
                _isError.postValue(true)
            }
        }
    }

    private fun fetchTravel(travelId: Long) {
        viewModelScope.launch {
            _travel.value =
                VisitTravelUiModel(
                    id = travelId,
                    title = "praesent",
                )
        }
    }

    suspend fun updateVisit() {
        visitRepository.updateVisit(
            visitImages = listOf(""),
            visitedAt = visitedAt.value.toString(),
        ).onSuccess {
            _isUpdateCompleted.postValue(true)
        }
    }

    fun setImageUris(uris: List<Uri>) {
        _selectedImages.value = uris
    }
}
