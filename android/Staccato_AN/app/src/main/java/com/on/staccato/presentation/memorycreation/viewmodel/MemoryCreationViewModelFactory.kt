package com.on.staccato.presentation.memorycreation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.MemoryRepository

class MemoryCreationViewModelFactory(
    private val memoryRepository: MemoryRepository,
    private val imageRepository: ImageRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemoryCreationViewModel::class.java)) {
            return MemoryCreationViewModel(memoryRepository, imageRepository) as T
        }
        throw IllegalArgumentException()
    }
}