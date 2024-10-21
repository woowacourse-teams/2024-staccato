package com.on.staccato.presentation.memory.viewmodel

import android.util.Log
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
            _errorMessage.postValue(message)
            when (status) {
                is Status.Code ->
                    Log.e(
                        "MemoryViewModel",
                        "An error occurred\ncode : ${status.code}\nmessage : $errorMessage",
                    )

                is Status.Message ->
                    Log.e(
                        "MemoryViewModel",
                        "An error occurred\nmessage : ${status.message}\nmessage : $errorMessage",
                    )
            }
        }

        private fun handelException(
            e: Throwable,
            message: String,
        ) {
            _exceptionMessage.postValue(message)
            Log.e("MemoryViewModel", "An exception occurred\n$e\n$message")
        }
    }
