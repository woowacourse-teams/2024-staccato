package com.woowacourse.staccato.presentation.memorycreation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.domain.repository.MemoryRepository

class MemoryCreationViewModelFactory(
    private val memoryRepository: MemoryRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemoryCreationViewModel::class.java)) {
            return MemoryCreationViewModel(memoryRepository) as T
        }
        throw IllegalArgumentException()
    }
}
