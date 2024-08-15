package com.woowacourse.staccato.presentation.memorycreation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.domain.repository.TravelRepository

class TravelCreationViewModelFactory(
    private val travelRepository: TravelRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelCreationViewModel::class.java)) {
            return TravelCreationViewModel(travelRepository) as T
        }
        throw IllegalArgumentException()
    }
}
