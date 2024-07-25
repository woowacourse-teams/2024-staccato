package com.woowacourse.staccato.presentation.visitupdate.viewmodel

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
import java.time.LocalDate

class VisitUpdateViewModel(
    private val visitRepository: VisitRepository,
//    private val travelRepository: TravelRepository,
) : ViewModel() {
    private val _visitUpdateData = MutableLiveData<VisitUpdateUiModel>()
    val visitUpdateData: LiveData<VisitUpdateUiModel> get() = _visitUpdateData

    private val _travel = MutableLiveData<VisitTravelUiModel>()
    val travel: LiveData<VisitTravelUiModel> get() = _travel

    private val _selectedVisitedAt = MutableLiveData<LocalDate?>()
    val selectedVisitedAt: LiveData<LocalDate?> get() = _selectedVisitedAt

    private val _isError = MutableSingleLiveData(false)
    val isError: SingleLiveData<Boolean> get() = _isError

    fun fetchInitData(
        visitId: Long,
        travelId: Long,
    ) {
        fetchVisitUpdateData(visitId)
        fetchTravel(travelId)
    }

    private fun fetchVisitUpdateData(visitId: Long) {
        viewModelScope.launch {
            visitRepository.loadVisit(visitId = visitId).onSuccess { visit ->
                _visitUpdateData.value =
                    visit.toVisitUpdateDefaultUiModel()
            }.onFailure {
                _isError.postValue(true)
            }
        }
    }

    private fun fetchTravel(travelId: Long) {
        viewModelScope.launch {
            // travelRepository.loadTravel(travelId).onSuccess { travel ->
            _travel.value =
                VisitTravelUiModel(
                    id = travelId,
                    title = "praesent",
                    startAt = LocalDate.now(),
                    endAt = LocalDate.now().plusDays(2),
                )
            // }
        }
    }

    suspend fun updateVisit() {
        visitRepository.updateVisit(
            visitImages = listOf(""),
            visitedAt = selectedVisitedAt.value.toString(),
        )
    }

    fun updateVisitedAt(newSelectedVisitedAt: LocalDate?) {
        _selectedVisitedAt.value = newSelectedVisitedAt
    }
}
