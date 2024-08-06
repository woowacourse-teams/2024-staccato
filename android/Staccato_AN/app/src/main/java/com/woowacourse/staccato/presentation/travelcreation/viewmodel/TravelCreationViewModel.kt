package com.woowacourse.staccato.presentation.travelcreation.viewmodel

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.domain.model.NewTravel
import com.woowacourse.staccato.domain.repository.TravelRepository
import com.woowacourse.staccato.presentation.travelcreation.DateConverter.convertLongToLocalDate
import kotlinx.coroutines.launch
import java.time.LocalDate

class TravelCreationViewModel(
    private val travelRepository: TravelRepository,
) : ViewModel() {
    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl

    val title = ObservableField<String>()
    val description = ObservableField<String>()

    private val _startDate = MutableLiveData<LocalDate>(null)
    val startDate: LiveData<LocalDate> get() = _startDate

    private val _endDate = MutableLiveData<LocalDate>(null)
    val endDate: LiveData<LocalDate> get() = _endDate

    private val _createdTravelId = MutableLiveData<Long>()
    val createdTravelId: LiveData<Long> get() = _createdTravelId

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun setTravelPeriod(
        startAt: Long,
        endAt: Long,
    ) {
        _startDate.value = convertLongToLocalDate(startAt)
        _endDate.value = convertLongToLocalDate(endAt)
    }

    fun createTravel() {
        viewModelScope.launch {
            val travel = makeNewTravel()
            val result: ResponseResult<String> = travelRepository.createTravel(travel)
            result
                .onSuccess(::setCreatedTravelId)
                .onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    private fun setCreatedTravelId(it: String) {
        _createdTravelId.value = it.split("/").last().toLong()
    }

    private fun makeNewTravel(): NewTravel =
        NewTravel(
            travelThumbnail = imageUrl.value,
            travelTitle = title.get() ?: throw IllegalArgumentException(),
            startAt = startDate.value ?: throw IllegalArgumentException(),
            endAt = endDate.value ?: throw IllegalArgumentException(),
            description = description.get() ?: throw IllegalArgumentException(),
        )

    private fun handleServerError(
        code: Int,
        message: String,
    ) {
        _errorMessage.value = "$code : $TRAVEL_CREATION_ERROR_MESSAGE"
        Log.d("hye: 여행 생성 실패", "$code : $message $TRAVEL_CREATION_ERROR_MESSAGE")
    }

    private fun handelException(
        e: Throwable,
        message: String,
    ) {
        _errorMessage.value = TRAVEL_CREATION_ERROR_MESSAGE
        Log.d("hye: 여행 생성 실패 - 예외", "${e.message}")
    }

    companion object {
        private const val TRAVEL_CREATION_ERROR_MESSAGE = "여행 생성에 실패했습니다"
    }
}
