package com.woowacourse.staccato.presentation.visitupdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.staccato.presentation.visitcreation.model.TravelUiModel
import com.woowacourse.staccato.presentation.visitcreation.model.VisitCreationUiModel

class VisitUpdateViewModel : ViewModel() {
    private val _visitUpdateData = MutableLiveData<VisitCreationUiModel>()
    val visitUpdateData: LiveData<VisitCreationUiModel> get() = _visitUpdateData

    private val _selectedTravel = MutableLiveData<TravelUiModel>()
    val selectedTravel: LiveData<TravelUiModel> get() = _selectedTravel

    private val _selectedVisitedAt = MutableLiveData<String?>()
    val selectedVisitedAt: LiveData<String?> get() = _selectedVisitedAt

    private val dummyTravels =
        listOf(
            TravelUiModel(
                travelId = 0,
                travelTitle = "제주도",
                startAt = "2024-03-01",
                endAt = "2024-03-05",
            ),
            TravelUiModel(
                travelId = 1,
                travelTitle = "평택",
                startAt = "2024-04-11",
                endAt = "2024-04-14",
            ),
            TravelUiModel(
                travelId = 2,
                travelTitle = "강릉",
                startAt = "2024-08-13",
                endAt = "2024-08-16",
            ),
            TravelUiModel(
                travelId = 3,
                travelTitle = "부산",
                startAt = "2024-11-28",
                endAt = "2024-12-01",
            ),
        )

    fun fetchVisitUpdate() {
        _visitUpdateData.value =
            VisitCreationUiModel(
                pinId = 0,
                placeName = "제주도 감귤 농장",
                address = "제주도 남서기 서귀포시",
                visitedImages = listOf(""),
                travels = dummyTravels,
            )
    }

    fun updateSelectedTravel(newSelectedTravel: TravelUiModel) {
        _selectedTravel.value = newSelectedTravel
    }

    fun updateVisitedAt(newSelectedVisitedAt: String?) {
        _selectedVisitedAt.value = newSelectedVisitedAt
    }
}
