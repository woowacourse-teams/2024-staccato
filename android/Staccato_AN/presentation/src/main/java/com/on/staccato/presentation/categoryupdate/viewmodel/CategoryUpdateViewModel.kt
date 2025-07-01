package com.on.staccato.presentation.categoryupdate.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.ExceptionType
import com.on.staccato.domain.UploadFile
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.presentation.categorycreation.model.ThumbnailUiModel
import com.on.staccato.presentation.categoryupdate.CategoryUpdateError
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
class CategoryUpdateViewModel
    @Inject
    constructor(
        private val categoryRepository: CategoryRepository,
        private val imageRepository: ImageRepository,
    ) : ViewModel() {
        private val _category = MutableLiveData<NewCategory>()
        val category: LiveData<NewCategory> get() = _category

        private val _thumbnail = MutableLiveData<ThumbnailUiModel>(ThumbnailUiModel())
        val thumbnail: LiveData<ThumbnailUiModel> get() = _thumbnail

        val title = MutableLiveData<String>()
        val description = MutableLiveData<String?>()

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

        private val _isPeriodActive = MutableStateFlow<Boolean>(false)
        val isPeriodActive: StateFlow<Boolean> = _isPeriodActive.asStateFlow()

        private var categoryId: Long = 0L

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent> get() = _messageEvent

        private val _color = MutableLiveData(CategoryColor.GRAY)
        val color: LiveData<CategoryColor> get() = _color

        private val _error = MutableSingleLiveData<CategoryUpdateError>()
        val error: SingleLiveData<CategoryUpdateError> get() = _error

        private val thumbnailJobs = mutableMapOf<Uri, Job>()

        fun fetchCategory(id: Long) {
            categoryId = id
            viewModelScope.launch {
                val result = categoryRepository.getCategory(categoryId)
                result
                    .onSuccess(::initializeCategory)
                    .onServerError(::emitMessageEvent)
                    .onException(::handleInitializeCategoryException)
            }
        }

        fun updateCategory() {
            viewModelScope.launch {
                val newCategory: NewCategory = makeNewCategory()
                val result: ApiResult<Unit> = categoryRepository.updateCategory(categoryId, newCategory)
                result
                    .onSuccess { updateSuccessStatus() }
                    .onServerError(::handleUpdateError)
                    .onException(::handleUpdateException)
            }
        }

        fun setCategoryPeriod(
            startAt: Long,
            endAt: Long,
        ) {
            _startDate.value = startAt.toLocalDate()
            _endDate.value = endAt.toLocalDate()
        }

        fun updateCategoryColor(color: CategoryColor) {
            _color.value = color
        }

        fun updateIsPeriodActive(value: Boolean) {
            _isPeriodActive.value = value
        }

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

        private fun initializeCategory(category: Category) {
            _thumbnail.value = _thumbnail.value?.updateUrl(category.categoryThumbnailUrl)
            title.value = category.categoryTitle
            description.value = category.description
            _startDate.value = category.startAt
            _endDate.value = category.endAt
            _color.value = CategoryColor.getCategoryColorBy(category.color)
            updateIsPeriodActive(hasPeriod(category))
        }

        private fun hasPeriod(category: Category): Boolean = category.startAt != null && category.endAt != null

        private fun makeNewCategory() =
            NewCategory(
                categoryThumbnailUrl = _thumbnail.value?.url,
                categoryTitle = title.value ?: throw IllegalArgumentException(),
                startAt = getDateByPeriodSetting(startDate),
                endAt = getDateByPeriodSetting(endDate),
                description = description.value,
                color = color.value?.label ?: CategoryColor.GRAY.label,
            )

        private fun getDateByPeriodSetting(date: LiveData<LocalDate?>): LocalDate? {
            return if (isPeriodActive.value) {
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
            uri: Uri,
            file: UploadFile,
        ) {
            val thumbnailJob = createFetchingThumbnailJob(uri, file)
            thumbnailJob.invokeOnCompletion {
                thumbnailJobs.remove(uri)
            }
            thumbnailJobs[uri] = thumbnailJob
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
                    .onServerError(::emitMessageEvent)
                    .onException { state ->
                        handlePhotoException(state, uri, file)
                    }
            }
        }

        private fun setThumbnailUrl(imageUrl: String) {
            _thumbnail.value = _thumbnail.value?.updateUrl(imageUrl)
            _isPhotoPosting.value = false
        }

        private fun emitMessageEvent(message: String) {
            _messageEvent.setValue(MessageEvent.from(message))
        }

        private fun handlePhotoException(
            type: ExceptionType,
            uri: Uri,
            uploadFile: UploadFile,
        ) {
            if (thumbnailJobs[uri]?.isActive == true) {
                _error.setValue(CategoryUpdateError.Thumbnail(type.toMessageId(), uri, uploadFile))
            }
        }

        private fun handleInitializeCategoryException(type: ExceptionType) {
            _error.setValue(CategoryUpdateError.CategoryInitialization(type.toMessageId()))
        }

        private fun handleUpdateError(message: String) {
            _isPosting.value = false
            emitMessageEvent(message)
        }

        private fun handleUpdateException(type: ExceptionType) {
            _isPosting.value = false
            _error.setValue(CategoryUpdateError.CategoryUpdate(type.toMessageId()))
        }
    }
