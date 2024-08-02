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
import com.woowacourse.staccato.domain.repository.TravelRepository
import com.woowacourse.staccato.presentation.travel.viewmodel.TravelViewModel.Companion.TRAVEL_ERROR_MESSAGE
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

    fun loadTravel() {
        viewModelScope.launch {
            travelRepository.getTravel(travelId)
                .onSuccess { travel ->
                    _imageUrl.value = travel.travelThumbnail
                    title.set(travel.travelTitle)
                    description.set(travel.description)
                    _startDate.value = travel.startAt
                    _endDate.value = travel.endAt
                }.onServerError { code, message ->
                    // TODO: Error 핸들링
                    Log.d("hye: 여행 조회 실패", "$code : $message $TRAVEL_ERROR_MESSAGE")
                }.onException { e, message ->
                    // TODO: Exception 핸들링
                    Log.d("hye: 여행 조회 실패 - 예외", "${e.message}")
                }
        }
    }

    fun setTravelPeriod(
        startAt: Long,
        endAt: Long,
    ) {
        _startDate.value = convertLongToLocalDate(startAt)
        _endDate.value = convertLongToLocalDate(endAt)
    }
}
