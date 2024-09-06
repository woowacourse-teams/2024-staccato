package com.on.staccato.presentation.memory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.on.staccato.domain.repository.MemoryRepository

class MemoryViewModelFactory(
    private val memoryRepository: MemoryRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemoryViewModel::class.java)) {
            return MemoryViewModel(memoryRepository) as T
        }
        throw IllegalArgumentException()
    }
}
