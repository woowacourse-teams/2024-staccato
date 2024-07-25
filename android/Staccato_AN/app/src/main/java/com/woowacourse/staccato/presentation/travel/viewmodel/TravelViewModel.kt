package com.woowacourse.staccato.presentation.travel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.travel.TravelHandler
import com.woowacourse.staccato.presentation.travel.model.TravelUiModel
import com.woowacourse.staccato.presentation.travel.model.dummyTravel

class TravelViewModel : ViewModel(), TravelHandler {
    private val _travel = MutableLiveData<TravelUiModel>()
    val travel: LiveData<TravelUiModel> get() = _travel

    private val _visitId = MutableSingleLiveData<Long>()
    val visitId: SingleLiveData<Long> = _visitId

    override fun onVisitClicked(visitId: Long) {
        _visitId.setValue(visitId)
    }

    fun loadTravel() {
        _travel.value = dummyTravel
    }
}
