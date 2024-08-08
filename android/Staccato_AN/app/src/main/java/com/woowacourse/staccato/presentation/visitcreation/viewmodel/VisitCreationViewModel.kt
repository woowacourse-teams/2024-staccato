package com.woowacourse.staccato.presentation.visitcreation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.data.dto.Status
import com.woowacourse.staccato.domain.model.Timeline
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

    private val _selectedImages = MutableLiveData<List<Uri>>()

    val selectedImages: LiveData<List<Uri>> get() = _selectedImages

    fun fetchInitData(pinId: Long) =
        viewModelScope.launch {
            loadAllTravels()
            fetchVisitCreationData(pinId)
        }

    private suspend fun loadAllTravels() {
        timelineRepository.getTimeline()
            .onSuccess(::setVisitTravelUiModels)
            .onServerError(::handleServerError)
            .onException(::handleException)
    }

    private fun setVisitTravelUiModels(timeline: Timeline) {
        _travels.value = timeline.toTravels()
    }

    private fun handleServerError(
        status: Status,
        errorMessage: String,
    ) {
        when (status) {
            is Status.Code -> Log.e("VisitCreationViewModel", "${status.code}, $errorMessage")
            is Status.Message -> Log.e("VisitCreationViewModel", "${status.message}, $errorMessage")
        }
    }

    private fun handleException(
        e: Throwable,
        errorMessage: String,
    ) {
        Log.e("VisitCreationViewModel", "$e, $errorMessage")
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

    fun updateSelectedTravel(newSelectedTravel: VisitTravelUiModel) {
        _selectedTravel.value = newSelectedTravel
    }

    fun updateSelectedVisitedAt(newSelectedVisitedAt: LocalDate?) {
        _selectedVisitedAt.value = newSelectedVisitedAt
    }

    fun setImageUris(uris: List<Uri>) {
        _selectedImages.value = uris
    }
}
