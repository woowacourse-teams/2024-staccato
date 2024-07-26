package com.woowacourse.staccato.presentation.visitcreation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.repository.TimelineRepository
import com.woowacourse.staccato.domain.repository.VisitRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toTravels
import com.woowacourse.staccato.presentation.visitcreation.model.VisitCreationUiModel
import com.woowacourse.staccato.presentation.visitcreation.model.VisitTravelUiModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class VisitCreationViewModel(
    private val visitRepository: VisitRepository,
    private val timelineRepository: TimelineRepository,
) : ViewModel() {
    private val _visitCreationData = MutableLiveData<VisitCreationUiModel>()
    val visitCreationData: LiveData<VisitCreationUiModel> get() = _visitCreationData

    private val _travels = MutableLiveData<List<VisitTravelUiModel>>()
    val travels: LiveData<List<VisitTravelUiModel>> get() = _travels

    private val _selectedTravel = MutableLiveData<VisitTravelUiModel>()
    val selectedTravel: LiveData<VisitTravelUiModel> get() = _selectedTravel

    private val _selectedVisitedAt = MutableLiveData<LocalDate?>()
    val selectedVisitedAt: LiveData<LocalDate?> get() = _selectedVisitedAt

    private val _createdVisitId = MutableSingleLiveData<Long>()
    val createdVisitId: SingleLiveData<Long> get() = _createdVisitId

    fun fetchInitData(pinId: Long) =
        viewModelScope.launch {
            loadAllTravels()
            fetchVisitCreationData(pinId)
        }

    private suspend fun loadAllTravels() {
        _travels.value = timelineRepository.loadTravels().toTravels()
    }

    // TODO : 핀 정보들이 없어 임시 값을 넣었습니다
    private fun fetchVisitCreationData(pinId: Long) {
        _visitCreationData.value =
            VisitCreationUiModel(
                pinId = pinId,
                placeName = "성담빌딩",
                address = "서울특별시 강남구 테헤란로 411",
            )
    }

    fun updateSelectedTravel(newSelectedTravel: VisitTravelUiModel) {
        _selectedTravel.value = newSelectedTravel
    }

    fun updateSelectedVisitedAt(newSelectedVisitedAt: LocalDate?) {
        _selectedVisitedAt.value = newSelectedVisitedAt
    }

    fun createVisit(pinId: Long) =
        viewModelScope.launch {
            if (selectedVisitedAt.value != null && selectedTravel.value != null) {
                visitRepository.createVisit(
                    pinId = pinId,
                    visitImages = listOf(),
                    visitedAt = selectedVisitedAt.value!!.toString(),
                    travelId = selectedTravel.value!!.id,
                ).onSuccess { response ->
                    val createdId =
                        response.headers()["Location"]?.split("/")?.last()?.toLong()
                            ?: 0L // TODO:null처리
                    _createdVisitId.postValue(createdId)
                }
            }
        }
}
