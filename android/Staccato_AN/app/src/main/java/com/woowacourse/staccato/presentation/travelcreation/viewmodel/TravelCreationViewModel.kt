package com.woowacourse.staccato.presentation.travelcreation.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.staccato.presentation.travelcreation.DateConverter.convertLongToLocalDate
import java.time.LocalDate

class TravelCreationViewModel : ViewModel() {
    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl

    val title = ObservableField<String>()
    val description = ObservableField<String>()

    private val _startDate = MutableLiveData<LocalDate>(null)
    val startDate: LiveData<LocalDate> get() = _startDate

    private val _endDate = MutableLiveData<LocalDate>(null)
    val endDate: LiveData<LocalDate> get() = _endDate

    fun setTravelPeriod(
        startAt: Long,
        endAt: Long,
    ) {
        _startDate.value = convertLongToLocalDate(startAt)
        _endDate.value = convertLongToLocalDate(endAt)
    }
}
