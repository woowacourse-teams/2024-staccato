package com.woowacourse.staccato.presentation.visitcreation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.repository.VisitRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.visitcreation.model.VisitCreationUiModel
import com.woowacourse.staccato.presentation.visitcreation.model.VisitTravelUiModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class VisitCreationViewModel(private val visitRepository: VisitRepository) : ViewModel() {
    private val _visitCreationData = MutableLiveData<VisitCreationUiModel>()
    val visitCreationData: LiveData<VisitCreationUiModel> get() = _visitCreationData

    private val _selectedTravel = MutableLiveData<VisitTravelUiModel>()
    val selectedTravel: LiveData<VisitTravelUiModel> get() = _selectedTravel

    private val _selectedVisitedAt = MutableLiveData<LocalDate?>()
    val selectedVisitedAt: LiveData<LocalDate?> get() = _selectedVisitedAt

    private val _createdVisitId = MutableSingleLiveData<Long>()
    val createdVisitId: SingleLiveData<Long> get() = _createdVisitId

    private val dummyTravels =
        listOf(
            VisitTravelUiModel(
                id = 1,
                title = "Trip to Paris",
                startAt = LocalDate.of(2024, 1, 1),
                endAt = LocalDate.of(2024, 12, 31),
            ),
            VisitTravelUiModel(
                id = 3,
                title = "2024 봄 휴가",
                startAt = LocalDate.of(2024, 3, 1),
                endAt = LocalDate.of(2024, 3, 10),
            ),
            VisitTravelUiModel(
                id = 4,
                title = "Journey through Japan",
                startAt = LocalDate.of(2024, 8, 20),
                endAt = LocalDate.of(2024, 8, 30),
            ),
        )

    fun fetchInitData(pinId: Long) {
        val travels = fetchTravels()
        fetchVisitCreationData(pinId, travels)
    }

    private fun fetchTravels(): List<VisitTravelUiModel> {
        return dummyTravels // TODO : travelRepository 연결해서 모든 여행 불러오기
    }

    private fun fetchVisitCreationData(
        pinId: Long,
        travels: List<VisitTravelUiModel>,
    ) {
        _visitCreationData.value =
            VisitCreationUiModel(
                pinId = pinId,
                placeName = "집에 보내줘요",
                address = "서울특별시 강남구 선릉로 123길 성담빌딩 13층",
                travels = travels,
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
                ).onSuccess {
                    _createdVisitId.postValue(1L) // TODO: 헤더에서 생성된 VisitId 꺼내서 전달하기
                }
            }
        }
}
