package com.on.staccato.presentation.memory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onServerError
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.Status
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.repository.MemoryRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toUiModel
import com.on.staccato.presentation.memory.model.MemoryUiModel
import kotlinx.coroutines.launch

class MemoryViewModel(private val memoryRepository: MemoryRepository) : ViewModel() {
    private val _memory = MutableLiveData<MemoryUiModel>()
    val memory: LiveData<MemoryUiModel> get() = _memory

    private val _errorMessage = MutableSingleLiveData<String>()
    val errorMessage: SingleLiveData<String> get() = _errorMessage

    private val _isDeleteSuccess = MutableSingleLiveData<Boolean>(false)
    val isDeleteSuccess: SingleLiveData<Boolean> get() = _isDeleteSuccess

    fun loadMemory(memoryId: Long) {
        viewModelScope.launch {
            val result: ResponseResult<Memory> = memoryRepository.getMemory(memoryId)
            result
                .onSuccess(::setMemory)
                .onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    fun deleteMemory(memoryId: Long) {
        viewModelScope.launch {
            val result: ResponseResult<Unit> = memoryRepository.deleteMemory(memoryId)
            result.onSuccess { updateIsDeleteSuccess() }
                .onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    private fun setMemory(memory: Memory) {
        _memory.value = memory.toUiModel()
    }

    private fun updateIsDeleteSuccess() {
        _isDeleteSuccess.setValue(true)
    }

    private fun handleServerError(
        status: Status,
        message: String,
    ) {
        _errorMessage.setValue(message)
    }

    private fun handelException(
        e: Throwable,
        message: String,
    ) {
        _errorMessage.setValue(MEMORY_ERROR_MESSAGE)
    }

    companion object {
        private const val MEMORY_ERROR_MESSAGE = "추억을 조회할 수 없습니다"
    }
}
