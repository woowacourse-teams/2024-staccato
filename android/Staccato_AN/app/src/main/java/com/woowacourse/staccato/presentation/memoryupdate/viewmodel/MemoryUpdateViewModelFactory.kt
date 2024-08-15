package com.woowacourse.staccato.presentation.memoryupdate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.domain.repository.MemoryRepository

class MemoryUpdateViewModelFactory(
    private val memoryId: Long,
    private val memoryRepository: MemoryRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemoryUpdateViewModel::class.java)) {
            return MemoryUpdateViewModel(memoryId, memoryRepository) as T
        }
        throw IllegalArgumentException()
    }
}
