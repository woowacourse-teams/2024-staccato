package com.woowacourse.staccato.presentation.memory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.domain.repository.TravelRepository

class TravelViewModelFactory(
    private val travelRepository: TravelRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelViewModel::class.java)) {
            return TravelViewModel(travelRepository) as T
        }
        throw IllegalArgumentException()
    }
}
