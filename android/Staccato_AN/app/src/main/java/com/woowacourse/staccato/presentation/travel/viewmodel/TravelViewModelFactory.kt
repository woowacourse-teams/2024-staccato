package com.woowacourse.staccato.presentation.travel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.domain.repository.TravelRepository

class TravelViewModelFactory(
    private val travelRepository: TravelRepository,
    private val travelId: Long,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelViewModel::class.java)) {
            return TravelViewModel(travelRepository, travelId) as T
        }
        throw IllegalArgumentException()
    }
}
