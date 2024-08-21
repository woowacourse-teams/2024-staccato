package com.woowacourse.staccato.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.data.dto.Status
import com.woowacourse.staccato.domain.model.MomentLocation
import com.woowacourse.staccato.domain.repository.MomentRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import kotlinx.coroutines.launch

class MapsViewModel(
    private val momentRepository: MomentRepository,
) : ViewModel() {
    private val _momentLocations = MutableLiveData<List<MomentLocation>>()
    val momentLocations: LiveData<List<MomentLocation>> get() = _momentLocations

    private val _errorMessage = MutableSingleLiveData<String>()
    val errorMessage: SingleLiveData<String> get() = _errorMessage

    fun loadMoments() {
        viewModelScope.launch {
            val result = momentRepository.getMoments()
            result.onSuccess(::setMomentLocations).onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    private fun setMomentLocations(momentLocations: List<MomentLocation>) {
        _momentLocations.value = momentLocations
    }

    private fun handleServerError(
        status: Status,
        message: String,
    ) {
        _errorMessage.setValue(message)
    }

    private fun handelException(
        e: Throwable,
        message: String,
    ) {
        _errorMessage.setValue(STACCATO_LOCATIONS_ERROR_MESSAGE)
    }

    companion object {
        private const val STACCATO_LOCATIONS_ERROR_MESSAGE = "스타카토 기록을 조회할 수 없습니다"
    }
}
