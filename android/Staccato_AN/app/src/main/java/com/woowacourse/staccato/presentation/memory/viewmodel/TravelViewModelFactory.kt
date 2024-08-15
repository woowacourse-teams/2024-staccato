package com.woowacourse.staccato.presentation.memory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.domain.repository.MemoryRepository

class TravelViewModelFactory(
    private val memoryRepository: MemoryRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelViewModel::class.java)) {
            return TravelViewModel(memoryRepository) as T
        }
        throw IllegalArgumentException()
    }
}
