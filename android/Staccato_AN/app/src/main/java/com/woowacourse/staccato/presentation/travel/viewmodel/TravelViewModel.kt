package com.woowacourse.staccato.presentation.travel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.staccato.presentation.travel.model.TravelUiModel
import com.woowacourse.staccato.presentation.travel.model.dummyTravel

class TravelViewModel : ViewModel() {
    private val _travel = MutableLiveData<TravelUiModel>()
    val travel: LiveData<TravelUiModel> get() = _travel

    fun loadTravel() {
        _travel.value = dummyTravel
    }
}
