package com.woowacourse.staccato.presentation.travel.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.Status
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.domain.repository.TravelRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toUiModel
import com.woowacourse.staccato.presentation.travel.model.TravelUiModel
import kotlinx.coroutines.launch

class TravelViewModel(
    private val travelRepository: TravelRepository,
) : ViewModel() {
    private val _travel = MutableLiveData<TravelUiModel>()
    val travel: LiveData<TravelUiModel> get() = _travel

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isDeleteSuccess = MutableSingleLiveData<Boolean>(false)
    val isDeleteSuccess: SingleLiveData<Boolean> get() = _isDeleteSuccess

    fun loadTravel(travelId: Long) {
        viewModelScope.launch {
            val result: ResponseResult<Travel> = travelRepository.getTravel(travelId)
            result
                .onSuccess(::setTravel)
                .onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    fun deleteTravel(travelId: Long) {
        viewModelScope.launch {
            val result: ResponseResult<Unit> = travelRepository.deleteTravel(travelId)
            result.onSuccess { updateIsDeleteSuccess() }
                .onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    private fun setTravel(travel: Travel) {
        _travel.value = travel.toUiModel()
    }

    private fun updateIsDeleteSuccess() {
        _isDeleteSuccess.setValue(true)
    }

    private fun handleServerError(
        status: Status,
        message: String,
    ) {
        _errorMessage.value = message
    }

    private fun handelException(
        e: Throwable,
        message: String,
    ) {
        Log.d("hye", e.message.toString())
        _errorMessage.value = TRAVEL_ERROR_MESSAGE
    }

    companion object {
        private const val TRAVEL_ERROR_MESSAGE = "여행을 조회할 수 없습니다"
    }
}
