package com.on.staccato.presentation.memorycreation.viewmodel

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
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.domain.model.NewMemory
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.MemoryRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.memorycreation.DateConverter.convertLongToLocalDate
import com.on.staccato.presentation.memorycreation.MemoryCreationError
import com.on.staccato.presentation.memorycreation.ThumbnailUiModel
import com.on.staccato.presentation.util.convertMemoryUriToFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private typealias ThumbnailUri = Uri

@HiltViewModel
class MemoryCreationViewModel
    @Inject
    constructor(
        private val memoryRepository: MemoryRepository,
        private val imageRepository: ImageRepository,
    ) : ViewModel() {
        val title = ObservableField<String>()
        val description = ObservableField<String>()
        val isPeriodActive = MutableLiveData<Boolean>(false)

        private val _startDate = MutableLiveData<LocalDate?>(null)
        val startDate: LiveData<LocalDate?> get() = _startDate

        private val _endDate = MutableLiveData<LocalDate?>(null)
        val endDate: LiveData<LocalDate?> get() = _endDate

        private val _createdMemoryId = MutableLiveData<Long>()
        val createdMemoryId: LiveData<Long> get() = _createdMemoryId

        private val _thumbnail = MutableLiveData<ThumbnailUiModel>(ThumbnailUiModel())
        val thumbnail: LiveData<ThumbnailUiModel> get() = _thumbnail

        private val _isPosting = MutableLiveData<Boolean>(false)
        val isPosting: LiveData<Boolean> get() = _isPosting

        private val _isPhotoPosting = MutableLiveData<Boolean>(false)
        val isPhotoPosting: LiveData<Boolean> get() = _isPhotoPosting

        private val _errorMessage = MutableLiveData<String>()
        val errorMessage: LiveData<String> get() = _errorMessage

        private val _error = MutableSingleLiveData<MemoryCreationError>()
        val error: SingleLiveData<MemoryCreationError> get() = _error

        private val thumbnailJobs = mutableMapOf<ThumbnailUri, Job>()

        fun createThumbnailUrl(
            context: Context,
            uri: Uri,
        ) {
            _isPhotoPosting.value = true
            setThumbnailUri(uri)
            registerThumbnailJob(context, uri)
        }

        fun deleteThumbnail() {
            _thumbnail.value = thumbnail.value?.delete()
        }

        fun setMemoryPeriod(
            startAt: Long,
            endAt: Long,
        ) {
            _startDate.value = convertLongToLocalDate(startAt)
            _endDate.value = convertLongToLocalDate(endAt)
        }

        fun createMemory() {
            _isPosting.value = true
            viewModelScope.launch {
                val memory: NewMemory = makeNewMemory()
                val result: ResponseResult<MemoryCreationResponse> =
                    memoryRepository.createMemory(memory)
                result
                    .onSuccess(::setCreatedMemoryId)
                    .onServerError(::handleCreateServerError)
                    .onException(::handleCreateException)
            }
        }

        private fun setThumbnailUri(
            uri: Uri?,
            url: String? = null,
        ) {
            if (isNewUri(uri)) {
                thumbnailJobs[_thumbnail.value?.uri]?.cancel()
                _thumbnail.value = ThumbnailUiModel(uri = uri, url = url)
            }
        }

        private fun isNewUri(uri: Uri?): Boolean = _thumbnail.value?.isEqualUri(uri) == false

        private fun registerThumbnailJob(
            context: Context,
            uri: Uri,
        ) {
            val job = fetchThumbnail(context, uri)
            job.invokeOnCompletion {
                thumbnailJobs.remove(uri)
            }
            thumbnailJobs[uri] = job
        }

        private fun fetchThumbnail(
            context: Context,
            uri: Uri,
        ): Job {
            val thumbnailFile = convertMemoryUriToFile(context, uri, name = MEMORY_FILE_NAME)
            return viewModelScope.launch {
                val result: ResponseResult<ImageResponse> =
                    imageRepository.convertImageFileToUrl(thumbnailFile)
                result
                    .onSuccess(::setThumbnailUrl)
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

        private fun setCreatedMemoryId(memoryCreationResponse: MemoryCreationResponse) {
            _createdMemoryId.value = memoryCreationResponse.memoryId
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

        private fun handlePhotoError(
            status: Status,
            message: String,
        ) {
            _errorMessage.postValue(message)
        }

        private fun handlePhotoException(
            e: Throwable,
            message: String,
            uri: Uri,
        ) {
            if (thumbnailJobs[uri]?.isActive == true) {
                _error.setValue(MemoryCreationError.Thumbnail(message, uri))
            }
        }

        private fun handleCreateServerError(
            status: Status,
            message: String,
        ) {
            _isPosting.value = false
            _errorMessage.postValue(message)
        }

        private fun handleCreateException(
            e: Throwable,
            message: String,
        ) {
            _isPosting.value = false
            _error.setValue(MemoryCreationError.MemoryCreation(message))
        }

        companion object {
            private const val MEMORY_FILE_NAME = "imageFile"
        }
    }
