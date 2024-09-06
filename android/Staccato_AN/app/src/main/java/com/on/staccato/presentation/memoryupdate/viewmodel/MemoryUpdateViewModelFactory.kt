package com.on.staccato.presentation.memoryupdate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.MemoryRepository

class MemoryUpdateViewModelFactory(
    private val memoryId: Long,
    private val memoryRepository: MemoryRepository,
    private val imageRepository: ImageRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemoryUpdateViewModel::class.java)) {
            return MemoryUpdateViewModel(memoryId, memoryRepository, imageRepository) as T
        }
        throw IllegalArgumentException()
    }
}
