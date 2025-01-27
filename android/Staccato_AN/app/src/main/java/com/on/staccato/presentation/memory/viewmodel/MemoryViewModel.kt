package com.on.staccato.presentation.memory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResult
import com.on.staccato.data.onException
import com.on.staccato.data.onServerError
import com.on.staccato.data.onSuccess
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.repository.MemoryRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toUiModel
import com.on.staccato.presentation.memory.model.MemoryUiModel
import com.on.staccato.presentation.util.ExceptionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryViewModel
    @Inject
    constructor(
        private val memoryRepository: MemoryRepository,
    ) : ViewModel() {
        private val _memory = MutableLiveData<MemoryUiModel>()
        val memory: LiveData<MemoryUiModel> get() = _memory

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _exceptionMessage = MutableSingleLiveData<String>()
        val exceptionMessage: SingleLiveData<String> get() = _exceptionMessage

        private val _isDeleteSuccess = MutableSingleLiveData<Boolean>(false)
        val isDeleteSuccess: SingleLiveData<Boolean> get() = _isDeleteSuccess

        fun loadMemory(memoryId: Long) {
            viewModelScope.launch {
                val result: ApiResult<Memory> = memoryRepository.getMemory(memoryId)
                result
                    .onSuccess(::setMemory)
                    .onServerError(::handleServerError)
                    .onException(::handelException)
            }
        }

        fun deleteMemory(memoryId: Long) {
            viewModelScope.launch {
                val result: ApiResult<Unit> = memoryRepository.deleteMemory(memoryId)
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

        private fun handleServerError(message: String) {
            _errorMessage.postValue(message)
        }

        private fun handelException(state: ExceptionState) {
            _exceptionMessage.postValue(state.message)
        }
    }
