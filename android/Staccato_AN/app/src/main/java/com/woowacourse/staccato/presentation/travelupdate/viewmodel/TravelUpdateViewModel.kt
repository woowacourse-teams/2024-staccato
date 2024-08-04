package com.woowacourse.staccato.presentation.travelupdate.viewmodel

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.domain.repository.TravelRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.travelcreation.DateConverter.convertLongToLocalDate
import com.woowacourse.staccato.presentation.travelcreation.TravelCreationUiModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class TravelUpdateViewModel(
    private val travelId: Long,
    private val travelRepository: TravelRepository,
) : ViewModel() {
    private val _travel = MutableLiveData<TravelCreationUiModel>()
    val travel: LiveData<TravelCreationUiModel> get() = _travel

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> get() = _imageUrl

    val title = ObservableField<String>()
    val description = ObservableField<String>()

    private val _startDate = MutableLiveData<LocalDate>(null)
    val startDate: LiveData<LocalDate> get() = _startDate

    private val _endDate = MutableLiveData<LocalDate>(null)
    val endDate: LiveData<LocalDate> get() = _endDate

    private val _isUpdateSuccess = MutableSingleLiveData<Boolean>(false)
    val isUpdateSuccess: SingleLiveData<Boolean> get() = _isUpdateSuccess

    fun fetchTravel() {
        viewModelScope.launch {
            val result = travelRepository.getTravel(travelId)
            result
                .onSuccess(::initializeTravel)
                .onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    fun updateTravel() {
        viewModelScope.launch {
            val newTravel = makeNewTravel()
            val result = travelRepository.updateTravel(travelId, newTravel)
            result
                .onSuccess { updateSuccessStatus() }
                .onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    fun setTravelPeriod(
        startAt: Long,
        endAt: Long,
    ) {
        _startDate.value = convertLongToLocalDate(startAt)
        _endDate.value = convertLongToLocalDate(endAt)
    }

    private fun initializeTravel(travel: Travel) {
        _imageUrl.value = travel.travelThumbnail
        title.set(travel.travelTitle)
        description.set(travel.description)
        _startDate.value = travel.startAt
        _endDate.value = travel.endAt
    }

    private fun makeNewTravel() =
        TravelCreationUiModel(
            travelThumbnail = imageUrl.value,
            travelTitle = title.get() ?: throw IllegalArgumentException(),
            startAt = startDate.value ?: throw IllegalArgumentException(),
            endAt = endDate.value ?: throw IllegalArgumentException(),
            description = description.get(),
        )

    private fun updateSuccessStatus() {
        _isUpdateSuccess.setValue(true)
    }

    private fun handleServerError(
        code: Int,
        message: String,
    ) {
        // TODO: Error 핸들링
        Log.d("hye: 여행 수정 실패", "$code : $message $TRAVEL_UPDATE_ERROR_MESSAGE")
    }

    private fun handelException(
        e: Throwable,
        message: String,
    ) {
        // TODO: Exception 핸들링
        Log.d("hye: 여행 수정 실패 - 예외", "${e.message}")
    }

    companion object {
        private const val TRAVEL_UPDATE_ERROR_MESSAGE = "여행 수정에 실패했습니다"
    }
}
