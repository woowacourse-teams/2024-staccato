package com.woowacourse.staccato.presentation.travel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.domain.repository.TravelRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toUiModel
import com.woowacourse.staccato.presentation.travel.TravelHandler
import com.woowacourse.staccato.presentation.travel.model.TravelUiModel
import kotlinx.coroutines.launch

class TravelViewModel(
    private val travelRepository: TravelRepository,
) : ViewModel(), TravelHandler {
    private val _travel = MutableLiveData<TravelUiModel>()
    val travel: LiveData<TravelUiModel> get() = _travel

    private val _visitId = MutableSingleLiveData<Long>()
    val visitId: SingleLiveData<Long> = _visitId

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> get() = _errorMessage

    override fun onVisitClicked(visitId: Long) {
        _visitId.setValue(visitId)
    }

    fun loadTravel(travelId: Long) {
        viewModelScope.launch {
            travelRepository.loadTravel(travelId).onSuccess { travel ->
                _travel.value = travel.toUiModel()
            }.onServerError { code, message ->
                _errorMessage.value = "$code : $TRAVEL_ERROR_MESSAGE"
            }.onException { e, message ->
                _errorMessage.value = TRAVEL_ERROR_MESSAGE
            }
        }
    }

    companion object {
        const val TRAVEL_ERROR_MESSAGE = "여행을 조회할 수 없습니다"
    }
}
