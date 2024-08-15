package com.woowacourse.staccato.presentation.memoryupdate.viewmodel

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
import com.woowacourse.staccato.data.dto.Status
import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.NewMemory
import com.woowacourse.staccato.domain.repository.MemoryRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.memorycreation.DateConverter.convertLongToLocalDate
import com.woowacourse.staccato.presentation.util.convertTravelUriToFile
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.time.LocalDate

class MemoryUpdateViewModel(
    private val memoryId: Long,
    private val memoryRepository: MemoryRepository,
) : ViewModel() {
    private val _memory = MutableLiveData<NewMemory>()
    val memory: LiveData<NewMemory> get() = _memory

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> get() = _imageUrl

    val title = ObservableField<String>()
    val description = ObservableField<String>()

    private val _startDate = MutableLiveData<LocalDate>(null)
    val startDate: LiveData<LocalDate> get() = _startDate

    private val _endDate = MutableLiveData<LocalDate>(null)
    val endDate: LiveData<LocalDate> get() = _endDate

    private val _isUpdateSuccess = MutableSingleLiveData<Boolean>(false)
    val isUpdateSuccess: SingleLiveData<Boolean> get() = _isUpdateSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get() = _imageUri

    private val _isPosting = MutableLiveData<Boolean>()
    val isPosting: LiveData<Boolean> get() = _isPosting

    fun fetchMemory() {
        viewModelScope.launch {
            val result = memoryRepository.getMemory(memoryId)
            result
                .onSuccess(::initializeMemory)
                .onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    fun setImage(uri: Uri) {
        _imageUri.value = uri
        _imageUrl.value = null
    }

    fun updateMemory(context: Context) {
        _isPosting.value = true
        viewModelScope.launch {
            val newMemory: NewMemory = makeNewMemory()
            val thumbnailFile: MultipartBody.Part? = convertTravelUriToFile(context, _imageUri.value, MEMORY_FILE_NAME)
            val result = memoryRepository.updateMemory(memoryId, newMemory, thumbnailFile)
            result
                .onSuccess { updateSuccessStatus() }
                .onServerError(::handleServerError)
                .onException(::handelException)
        }
    }

    fun setMemoryPeriod(
        startAt: Long,
        endAt: Long,
    ) {
        _startDate.value = convertLongToLocalDate(startAt)
        _endDate.value = convertLongToLocalDate(endAt)
    }

    private fun initializeMemory(memory: Memory) {
        _imageUrl.value = memory.memoryThumbnailUrl
        title.set(memory.memoryTitle)
        description.set(memory.description)
        _startDate.value = memory.startAt
        _endDate.value = memory.endAt
    }

    private fun makeNewMemory() =
        NewMemory(
            memoryThumbnail = imageUrl.value,
            memoryTitle = title.get() ?: throw IllegalArgumentException(),
            startAt = startDate.value ?: throw IllegalArgumentException(),
            endAt = endDate.value ?: throw IllegalArgumentException(),
            description = description.get(),
        )

    private fun updateSuccessStatus() {
        _isUpdateSuccess.setValue(true)
    }

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
        _errorMessage.value = MEMORY_UPDATE_ERROR_MESSAGE
    }

    companion object {
        private const val MEMORY_FILE_NAME = "memoryThumbnailFile"
        private const val MEMORY_UPDATE_ERROR_MESSAGE = "추억 수정에 실패했습니다"
    }
}
