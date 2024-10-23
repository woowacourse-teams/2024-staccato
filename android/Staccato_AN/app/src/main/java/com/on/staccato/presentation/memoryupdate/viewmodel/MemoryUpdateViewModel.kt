package com.on.staccato.presentation.memoryupdate.viewmodel

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onServerError
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.Status
import com.on.staccato.data.dto.image.ImageResponse
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.NewMemory
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.MemoryRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.memorycreation.DateConverter.convertLongToLocalDate
import com.on.staccato.presentation.memoryupdate.MemoryUpdateError
import com.on.staccato.presentation.util.convertMemoryUriToFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MemoryUpdateViewModel
    @Inject
    constructor(
        private val memoryRepository: MemoryRepository,
        private val imageRepository: ImageRepository,
    ) : ViewModel() {
        private val _memory = MutableLiveData<NewMemory>()
        val memory: LiveData<NewMemory> get() = _memory

        private val _thumbnailUri = MutableLiveData<Uri?>(null)
        val thumbnailUri: LiveData<Uri?> get() = _thumbnailUri

        private val _thumbnailUrl = MutableLiveData<String?>(null)
        val thumbnailUrl: LiveData<String?> get() = _thumbnailUrl

        val title = ObservableField<String>()
        val description = ObservableField<String>()

        private val _startDate = MutableLiveData<LocalDate?>(null)
        val startDate: LiveData<LocalDate?> get() = _startDate

        private val _endDate = MutableLiveData<LocalDate?>(null)
        val endDate: LiveData<LocalDate?> get() = _endDate

        private val _isUpdateSuccess = MutableSingleLiveData<Boolean>(false)
        val isUpdateSuccess: SingleLiveData<Boolean> get() = _isUpdateSuccess

        private val _isPosting = MutableLiveData<Boolean>()
        val isPosting: LiveData<Boolean> get() = _isPosting

        private val _isPhotoPosting = MutableLiveData<Boolean>(false)
        val isPhotoPosting: LiveData<Boolean> get() = _isPhotoPosting

        val isPeriodActive = MutableLiveData<Boolean>()

        private var memoryId: Long = 0L

        private val _errorMessage = MutableLiveData<String>()
        val errorMessage: LiveData<String> get() = _errorMessage

        private val _error = MutableSingleLiveData<MemoryUpdateError>()
        val error: SingleLiveData<MemoryUpdateError> get() = _error

        fun fetchMemory(memoryId: Long) {
            fetchMemoryId(memoryId)
            viewModelScope.launch {
                val result = memoryRepository.getMemory(memoryId)
                result
                    .onSuccess(::initializeMemory)
                    .onServerError(::handleInitializeMemoryError)
                    .onException(::handleInitializeMemoryException)
            }
        }

        private fun fetchMemoryId(id: Long) {
            memoryId = id
        }

        fun createThumbnailUrl(
            context: Context,
            thumbnailUri: Uri,
        ) {
            _thumbnailUrl.value = null
            _thumbnailUri.value = thumbnailUri
            _isPhotoPosting.value = true
            val thumbnailFile = convertMemoryUriToFile(context, thumbnailUri, name = MEMORY_FILE_NAME)
            viewModelScope.launch {
                val result: ResponseResult<ImageResponse> =
                    imageRepository.convertImageFileToUrl(thumbnailFile)
                result.onSuccess(::setThumbnailUrl)
                    .onServerError(::handlePhotoError)
                    .onException { e, message ->
                        handlePhotoException(e, message, thumbnailUri)
                    }
            }
        }

        fun setThumbnailUri(thumbnailUri: Uri?) {
            _thumbnailUri.value = thumbnailUri
            _thumbnailUrl.value = null
        }

        fun setThumbnailUrl(imageResponse: ImageResponse?) {
            _thumbnailUrl.value = imageResponse?.imageUrl
            _isPhotoPosting.value = false
        }

        fun updateMemory() {
            viewModelScope.launch {
                val newMemory: NewMemory = makeNewMemory()
                val result: ResponseResult<Unit> = memoryRepository.updateMemory(memoryId, newMemory)
                result
                    .onSuccess { updateSuccessStatus() }
                    .onServerError(::handleUpdateError)
                    .onException(::handleUpdateException)
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
            _thumbnailUrl.value = memory.memoryThumbnailUrl
            title.set(memory.memoryTitle)
            description.set(memory.description)
            _startDate.value = memory.startAt
            _endDate.value = memory.endAt
            checkMemoryHasPeriod(memory)
        }

        private fun checkMemoryHasPeriod(memory: Memory) {
            isPeriodActive.value = memory.startAt != null && memory.endAt != null
        }

        private fun makeNewMemory() =
            NewMemory(
                memoryThumbnailUrl = thumbnailUrl.value,
                memoryTitle = title.get() ?: throw IllegalArgumentException(),
                startAt = getDateByPeriodSetting(startDate),
                endAt = getDateByPeriodSetting(endDate),
                description = description.get(),
            )

        private fun getDateByPeriodSetting(date: LiveData<LocalDate?>): LocalDate? {
            return if (isPeriodActive.value == true) {
                date.value
            } else {
                null
            }
        }

        private fun updateSuccessStatus() {
            _isPosting.value = true
            _isUpdateSuccess.setValue(true)
        }

        private fun handlePhotoError(
            status: Status,
            message: String,
        ) {
            _errorMessage.value = message
        }

        private fun handlePhotoException(
            e: Throwable,
            message: String,
            uri: Uri,
        ) {
            _error.setValue(MemoryUpdateError.Photo(message, uri))
        }

        private fun handleInitializeMemoryError(
            status: Status,
            message: String,
        ) {
            _errorMessage.value = message
        }

        private fun handleInitializeMemoryException(
            e: Throwable,
            message: String,
        ) {
            _error.setValue(MemoryUpdateError.MemoryInitialize(message))
        }

        private fun handleUpdateError(
            status: Status,
            message: String,
        ) {
            _isPosting.value = false
            _errorMessage.value = message
        }

        private fun handleUpdateException(
            e: Throwable,
            message: String,
        ) {
            _isPosting.value = false
            _error.setValue(MemoryUpdateError.MemoryUpdate(message))
        }

        companion object {
            private const val MEMORY_FILE_NAME = "imageFile"
        }
    }
