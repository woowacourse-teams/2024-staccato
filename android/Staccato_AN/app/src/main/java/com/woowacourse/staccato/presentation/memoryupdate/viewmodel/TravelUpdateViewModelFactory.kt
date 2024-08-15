package com.woowacourse.staccato.presentation.memoryupdate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.domain.repository.TravelRepository

class TravelUpdateViewModelFactory(
    private val travelId: Long,
    private val travelRepository: TravelRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelUpdateViewModel::class.java)) {
            return TravelUpdateViewModel(travelId, travelRepository) as T
        }
        throw IllegalArgumentException()
    }
}
