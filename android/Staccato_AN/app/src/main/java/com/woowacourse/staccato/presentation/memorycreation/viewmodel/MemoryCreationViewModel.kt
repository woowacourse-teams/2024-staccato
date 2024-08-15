package com.woowacourse.staccato.presentation.memorycreation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onServerError
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.Status
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.domain.model.NewMemory
import com.woowacourse.staccato.domain.repository.MemoryRepository
import com.woowacourse.staccato.presentation.memorycreation.DateConverter.convertLongToLocalDate
import com.woowacourse.staccato.presentation.util.convertTravelUriToFile
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.time.LocalDate

class MemoryCreationViewModel(
    private val memoryRepository: MemoryRepository,
) : ViewModel() {
    val title = ObservableField<String>()
    val description = ObservableField<String>()

    private val _startDate = MutableLiveData<LocalDate>(null)
    val startDate: LiveData<LocalDate> get() = _startDate

    private val _endDate = MutableLiveData<LocalDate>(null)
    val endDate: LiveData<LocalDate> get() = _endDate

    private val _createdMemoryId = MutableLiveData<Long>()
    val createdMemoryId: LiveData<Long> get() = _createdMemoryId

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> get() = _imageUri

    private val _isPosting = MutableLiveData<Boolean>(false)
    val isPosting: LiveData<Boolean> get() = _isPosting

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun setMemoryPeriod(
        startAt: Long,
        endAt: Long,
    ) {
        _startDate.value = convertLongToLocalDate(startAt)
        _endDate.value = convertLongToLocalDate(endAt)
    }

    fun createMemory(context: Context) {
        _isPosting.value = true
        viewModelScope.launch {
            val memory: NewMemory = makeNewMemory()
            val thumbnailFile: MultipartBody.Part? =
                convertTravelUriToFile(context, _imageUri.value, name = MEMORY_FILE_NAME)
            val result: ResponseResult<MemoryCreationResponse> =
                memoryRepository.createMemory(memory, thumbnailFile)
            result
                .onSuccess(::setCreatedMemoryId)
                .onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    private fun setCreatedMemoryId(memoryCreationResponse: MemoryCreationResponse) {
        _createdMemoryId.value = memoryCreationResponse.memoryId
    }

    private fun makeNewMemory(): NewMemory =
        NewMemory(
            memoryTitle = title.get() ?: throw IllegalArgumentException(),
            startAt = startDate.value ?: throw IllegalArgumentException(),
            endAt = endDate.value ?: throw IllegalArgumentException(),
            description = description.get(),
        )

    private fun handleServerError(
        status: Status,
        message: String,
    ) {
        _isPosting.value = false
        _errorMessage.value = message
    }

    private fun handelException(
        e: Throwable,
        message: String,
    ) {
        _isPosting.value = false
        _errorMessage.value = MEMORY_CREATION_ERROR_MESSAGE
    }

    companion object {
        private const val MEMORY_FILE_NAME = "memoryThumbnailFile"
        private const val MEMORY_CREATION_ERROR_MESSAGE = "추억 생성에 실패했습니다"
    }
}
