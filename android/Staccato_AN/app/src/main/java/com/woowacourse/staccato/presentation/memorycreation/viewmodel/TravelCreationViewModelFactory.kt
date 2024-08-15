package com.woowacourse.staccato.presentation.memorycreation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.domain.repository.MemoryRepository

class TravelCreationViewModelFactory(
    private val memoryRepository: MemoryRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelCreationViewModel::class.java)) {
            return TravelCreationViewModel(memoryRepository) as T
        }
        throw IllegalArgumentException()
    }
}
