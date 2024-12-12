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
import com.on.staccato.presentation.memorycreation.ThumbnailUiModel
import com.on.staccato.presentation.memoryupdate.MemoryUpdateError
import com.on.staccato.presentation.util.IMAGE_FORM_DATA_NAME
import com.on.staccato.presentation.util.convertMemoryUriToFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private typealias ThumbnailUri = Uri

@HiltViewModel
class MemoryUpdateViewModel
    @Inject
    constructor(
        private val memoryRepository: MemoryRepository,
        private val imageRepository: ImageRepository,
    ) : ViewModel() {
        private val _memory = MutableLiveData<NewMemory>()
        val memory: LiveData<NewMemory> get() = _memory

        private val _thumbnail = MutableLiveData<ThumbnailUiModel>(ThumbnailUiModel())
        val thumbnail: LiveData<ThumbnailUiModel> get() = _thumbnail

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

        private val thumbnailJobs = mutableMapOf<ThumbnailUri, Job>()

        fun fetchMemory(id: Long) {
            memoryId = id
            viewModelScope.launch {
                val result = memoryRepository.getMemory(memoryId)
                result
                    .onSuccess(::initializeMemory)
                    .onServerError(::handleInitializeMemoryError)
                    .onException(::handleInitializeMemoryException)
            }
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

        fun createThumbnailUrl(
            context: Context,
            uri: Uri,
        ) {
            _isPhotoPosting.value = true
            setThumbnailUri(uri)
            registerThumbnailJob(context, uri)
        }

        fun clearThumbnail() {
            _thumbnail.value = thumbnail.value?.clear()
        }

        private fun initializeMemory(memory: Memory) {
            _thumbnail.value = _thumbnail.value?.updateUrl(memory.memoryThumbnailUrl)
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
                memoryThumbnailUrl = _thumbnail.value?.url,
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

        private fun setThumbnailUri(uri: Uri?) {
            val currentJob = thumbnailJobs[_thumbnail.value?.uri]
            if (isNewUri(uri) && currentJob?.isActive == true) {
                currentJob.cancel()
            }
            _thumbnail.value = ThumbnailUiModel(uri = uri, url = null)
        }

        private fun isNewUri(uri: Uri?): Boolean = _thumbnail.value?.isEqualUri(uri) == false

        private fun registerThumbnailJob(
            context: Context,
            uri: Uri,
        ) {
            val thumbnailJob = createFetchingThumbnailJob(context, uri)
            thumbnailJob.invokeOnCompletion {
                thumbnailJobs.remove(uri)
            }
            thumbnailJobs[uri] = thumbnailJob
        }

        private fun createFetchingThumbnailJob(
            context: Context,
            uri: Uri,
        ): Job {
            val thumbnailFile = convertMemoryUriToFile(context, uri, IMAGE_FORM_DATA_NAME)
            return viewModelScope.launch {
                val result: ResponseResult<ImageResponse> =
                    imageRepository.convertImageFileToUrl(thumbnailFile)
                result.onSuccess(::setThumbnailUrl)
                    .onServerError(::handlePhotoError)
                    .onException { e, message ->
                        handlePhotoException(e, message, uri)
                    }
            }
        }

        private fun setThumbnailUrl(imageResponse: ImageResponse) {
            val newUrl = imageResponse.imageUrl
            _thumbnail.value = _thumbnail.value?.updateUrl(newUrl)
            _isPhotoPosting.value = false
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
            if (thumbnailJobs[uri]?.isActive == true) {
                _error.setValue(MemoryUpdateError.Thumbnail(message, uri))
            }
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
            _error.setValue(MemoryUpdateError.MemoryInitialization(message))
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
    }
