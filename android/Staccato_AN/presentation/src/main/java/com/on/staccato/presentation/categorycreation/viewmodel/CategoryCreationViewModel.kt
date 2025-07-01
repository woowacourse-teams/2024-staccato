package com.on.staccato.presentation.categorycreation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.ExceptionType
import com.on.staccato.domain.UploadFile
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.presentation.categorycreation.model.CategoryCreationError
import com.on.staccato.presentation.categorycreation.model.ThumbnailUiModel
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.util.toLocalDate
import com.on.staccato.toMessageId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CategoryCreationViewModel
    @Inject
    constructor(
        private val categoryRepository: CategoryRepository,
        private val imageRepository: ImageRepository,
    ) : ViewModel() {
        val title = MutableLiveData<String>()
        val description = MutableLiveData<String>()

        private val _isPeriodActive = MutableStateFlow<Boolean>(false)
        val isPeriodActive: StateFlow<Boolean> = _isPeriodActive.asStateFlow()

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

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent> get() = _messageEvent

        private val _error = MutableSingleLiveData<CategoryCreationError>()
        val error: SingleLiveData<CategoryCreationError> get() = _error

        private val thumbnailJobs = mutableMapOf<Uri, Job>()

        private val _color = MutableLiveData(CategoryColor.GRAY)
        val color: LiveData<CategoryColor> get() = _color

        private val _isShared = MutableStateFlow<Boolean>(false)
        val isShared: StateFlow<Boolean> = _isShared.asStateFlow()

        fun createThumbnailUrl(
            uri: Uri,
            file: UploadFile,
        ) {
            _isPhotoPosting.value = true
            setThumbnailUri(uri)
            registerThumbnailJob(uri, file)
        }

        fun clearThumbnail() {
            _thumbnail.value = thumbnail.value?.clear()
        }

        fun setCategoryPeriod(
            startAt: Long,
            endAt: Long,
        ) {
            _startDate.value = startAt.toLocalDate()
            _endDate.value = endAt.toLocalDate()
        }

        fun createCategory() {
            _isPosting.value = true
            viewModelScope.launch {
                val category: NewCategory = makeNewCategory()
                val result: ApiResult<Long> =
                    categoryRepository.createCategory(category)
                result
                    .onSuccess(::setCreatedCategoryId)
                    .onServerError(::updateCreateServerError)
                    .onException(::handleCreateException)
            }
        }

        fun updateCategoryColor(color: CategoryColor) {
            _color.value = color
        }

        fun updateIsPeriodActive(value: Boolean) {
            _isPeriodActive.value = value
        }

        fun updateIsShared(value: Boolean) {
            _isShared.value = value
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
            uri: Uri,
            file: UploadFile,
        ) {
            val job = createFetchingThumbnailJob(uri, file)
            job.invokeOnCompletion {
                thumbnailJobs.remove(uri)
            }
            thumbnailJobs[uri] = job
        }

        private fun createFetchingThumbnailJob(
            uri: Uri,
            file: UploadFile,
        ): Job {
            return viewModelScope.launch {
                val result: ApiResult<String> =
                    imageRepository.convertImageFileToUrl(file)
                result
                    .onSuccess(::setThumbnailUrl)
                    .onServerError(::updateMessageEvent)
                    .onException { type ->
                        handlePhotoException(type, uri, file)
                    }
            }
        }

        private fun setThumbnailUrl(newUrl: String) {
            _thumbnail.value = _thumbnail.value?.updateUrl(newUrl)
            _isPhotoPosting.value = false
        }

        private fun setCreatedCategoryId(categoryId: Long) {
            _isPosting.value = false
            _createdCategoryId.value = categoryId
        }

        private fun makeNewCategory() =
            NewCategory(
                categoryThumbnailUrl = _thumbnail.value?.url,
                categoryTitle = title.value ?: throw IllegalArgumentException(),
                description = description.value,
                color = color.value?.label ?: CategoryColor.GRAY.label,
                startAt = getDateByPeriodSetting(startDate),
                endAt = getDateByPeriodSetting(endDate),
                isShared = _isShared.value,
            )

        private fun getDateByPeriodSetting(date: LiveData<LocalDate?>): LocalDate? {
            return if (isPeriodActive.value) {
                date.value
            } else {
                null
            }
        }

        private fun updateMessageEvent(message: String) {
            _messageEvent.setValue(MessageEvent.from(message))
        }

        private fun handlePhotoException(
            type: ExceptionType,
            uri: Uri,
            uploadFile: UploadFile,
        ) {
            if (thumbnailJobs[uri]?.isActive == true) {
                _error.setValue(CategoryCreationError.Thumbnail(type.toMessageId(), uri, uploadFile))
            }
        }

        private fun updateCreateServerError(message: String) {
            _isPosting.value = false
            updateMessageEvent(message)
        }

        private fun handleCreateException(type: ExceptionType) {
            _isPosting.value = false
            _error.setValue(CategoryCreationError.CategoryCreation(type.toMessageId()))
        }
    }
