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
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.CategoryRepository
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
        private val categoryRepository: CategoryRepository,
        private val imageRepository: ImageRepository,
    ) : ViewModel() {
        val title = ObservableField<String>()
        val description = ObservableField<String>()
        val isPeriodActive = MutableLiveData<Boolean>(false)

        private val _startDate = MutableLiveData<LocalDate?>(null)
        val startDate: LiveData<LocalDate?> get() = _startDate

        private val _endDate = MutableLiveData<LocalDate?>(null)
        val endDate: LiveData<LocalDate?> get() = _endDate

        private val _createdCategoryId = MutableLiveData<Long>()
        val createdCategoryId: LiveData<Long> get() = _createdCategoryId

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

        fun clearThumbnail() {
            _thumbnail.value = thumbnail.value?.clear()
        }

        fun setCategoryPeriod(
            startAt: Long,
            endAt: Long,
        ) {
            _startDate.value = convertLongToLocalDate(startAt)
            _endDate.value = convertLongToLocalDate(endAt)
        }

        fun createCategory() {
            _isPosting.value = true
            viewModelScope.launch {
                val category: NewCategory = makeNewMemory()
                val result: ResponseResult<CategoryCreationResponse> =
                    categoryRepository.createCategory(category)
                result
                    .onSuccess(::setCreatedCategoryId)
                    .onServerError(::handleCreateServerError)
                    .onException(::handleCreateException)
            }
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
            val job = createFetchingThumbnailJob(context, uri)
            job.invokeOnCompletion {
                thumbnailJobs.remove(uri)
            }
            thumbnailJobs[uri] = job
        }

        private fun createFetchingThumbnailJob(
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

        private fun setCreatedCategoryId(categoryCreationResponse: CategoryCreationResponse) {
            _createdCategoryId.value = categoryCreationResponse.categoryId
        }

        private fun makeNewMemory() =
            NewCategory(
                categoryThumbnailUrl = _thumbnail.value?.url,
                categoryTitle = title.get() ?: throw IllegalArgumentException(),
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
