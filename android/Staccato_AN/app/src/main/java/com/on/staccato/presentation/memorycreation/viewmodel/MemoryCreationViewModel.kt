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
import com.on.staccato.presentation.memorycreation.DateConverter.convertLongToLocalDate
import com.on.staccato.presentation.util.convertMemoryUriToFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MemoryCreationViewModel
    @Inject
    constructor(
        private val memoryRepository: MemoryRepository,
        private val imageRepository: ImageRepository,
    ) : ViewModel() {
        val title = ObservableField<String>()
        val description = ObservableField<String>()

        private val _startDate = MutableLiveData<LocalDate?>(null)
        val startDate: LiveData<LocalDate?> get() = _startDate

        private val _endDate = MutableLiveData<LocalDate?>(null)
        val endDate: LiveData<LocalDate?> get() = _endDate

        private val _createdMemoryId = MutableLiveData<Long>()
        val createdMemoryId: LiveData<Long> get() = _createdMemoryId

        private val _errorMessage = MutableLiveData<String>()
        val errorMessage: LiveData<String> get() = _errorMessage

        private val _thumbnailUri = MutableLiveData<Uri?>(null)
        val thumbnailUri: LiveData<Uri?> get() = _thumbnailUri

        private val _thumbnailUrl = MutableLiveData<String?>(null)
        val thumbnailUrl: LiveData<String?> get() = _thumbnailUrl

        private val _isPosting = MutableLiveData<Boolean>(false)
        val isPosting: LiveData<Boolean> get() = _isPosting

        private val _isPhotoPosting = MutableLiveData<Boolean>(false)
        val isPhotoPosting: LiveData<Boolean> get() = _isPhotoPosting

        val isPeriodSettingOn = MutableLiveData<Boolean>(false)

        fun createThumbnailUrl(
            context: Context,
            thumbnailUri: Uri,
        ) {
            _thumbnailUri.value = thumbnailUri
            _isPhotoPosting.value = true
            val thumbnailFile = convertMemoryUriToFile(context, thumbnailUri, name = MEMORY_FILE_NAME)
            viewModelScope.launch {
                val result: ResponseResult<ImageResponse> =
                    imageRepository.convertImageFileToUrl(thumbnailFile)
                result.onSuccess(::setThumbnailUrl)
                    .onServerError(::handleServerError)
                    .onException(::handelException)
            }
        }

        fun setThumbnailUri(thumbnailUri: Uri?) {
            _thumbnailUri.value = thumbnailUri
        }

        fun setThumbnailUrl(imageResponse: ImageResponse?) {
            _thumbnailUrl.value = imageResponse?.imageUrl
            _isPhotoPosting.value = false
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
                    .onServerError(::handleServerError)
                    .onException(::handelException)
            }
        }

        private fun setCreatedMemoryId(memoryCreationResponse: MemoryCreationResponse) {
            _createdMemoryId.value = memoryCreationResponse.memoryId
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
            return if (isPeriodSettingOn.value == true) {
                date.value
            } else {
                null
            }
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
            _errorMessage.value = MEMORY_CREATION_ERROR_MESSAGE
        }

        companion object {
            private const val MEMORY_FILE_NAME = "imageFile"
            private const val MEMORY_CREATION_ERROR_MESSAGE = "추억 생성에 실패했습니다"
        }
    }
