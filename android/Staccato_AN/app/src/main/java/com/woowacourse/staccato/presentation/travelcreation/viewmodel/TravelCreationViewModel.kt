package com.woowacourse.staccato.presentation.travelcreation.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
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

    fun setTravelPeriod(
        startAt: Long,
        endAt: Long,
    ) {
        _startDate.value = convertLongToLocalDate(startAt)
        _endDate.value = convertLongToLocalDate(endAt)
    }

    fun createTravel() {
        viewModelScope.launch {
            val travel =
                NewTravel(
                    travelThumbnail = imageUrl.value,
                    travelTitle = title.get() ?: return@launch,
                    startAt = startDate.value ?: return@launch,
                    endAt = endDate.value ?: return@launch,
                    description = description.get() ?: return@launch,
                )
            travelRepository.createTravel(travel).onSuccess {
                _createdTravelId.value = it.split("/").last().toLong()
            }.onServerError { code, message ->
                // TODO: Error 핸들링
            }.onException { e, message ->
                // TODO: Exception 핸들링
            }
        }
    }
}
