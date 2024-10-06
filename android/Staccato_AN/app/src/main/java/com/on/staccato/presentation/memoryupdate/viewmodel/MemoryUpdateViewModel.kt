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

        private val _errorMessage = MutableLiveData<String>()
        val errorMessage: LiveData<String> get() = _errorMessage

        private val _isPosting = MutableLiveData<Boolean>()
        val isPosting: LiveData<Boolean> get() = _isPosting

        private val _isPhotoPosting = MutableLiveData<Boolean>(false)
        val isPhotoPosting: LiveData<Boolean> get() = _isPosting

        val isPeriodSettingOn = MutableLiveData<Boolean>()

        private var memoryId: Long = 0L

        fun fetchMemory(memoryId: Long) {
            fetchMemoryId(memoryId)
            viewModelScope.launch {
                val result = memoryRepository.getMemory(memoryId)
                result
                    .onSuccess(::initializeMemory)
                    .onServerError(::handleServerError)
                    .onException(::handelException)
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
                    .onServerError(::handleServerError)
                    .onException(::handelException)
            }
        }

        fun setThumbnailUri(thumbnailUri: Uri?) {
            _thumbnailUri.value = thumbnailUri
            _thumbnailUrl.value = null
        }

        fun setThumbnailUrl(imageResponse: ImageResponse?) {
            _thumbnailUrl.value = imageResponse?.imageUrl
        }

        fun updateMemory() {
            viewModelScope.launch {
                val newMemory: NewMemory = makeNewMemory()
                val result: ResponseResult<Unit> = memoryRepository.updateMemory(memoryId, newMemory)
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
            _thumbnailUrl.value = memory.memoryThumbnailUrl
            title.set(memory.memoryTitle)
            description.set(memory.description)
            _startDate.value = memory.startAt
            _endDate.value = memory.endAt
            checkMemoryHasPeriod(memory)
        }

        private fun checkMemoryHasPeriod(memory: Memory) {
            isPeriodSettingOn.value = memory.startAt != null && memory.endAt != null
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
            private const val MEMORY_FILE_NAME = "imageFile"
            private const val MEMORY_UPDATE_ERROR_MESSAGE = "추억 수정에 실패했습니다"
        }
    }
