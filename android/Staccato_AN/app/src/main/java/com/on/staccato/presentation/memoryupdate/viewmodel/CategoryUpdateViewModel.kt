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
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.categorycreation.DateConverter.convertLongToLocalDate
import com.on.staccato.presentation.categorycreation.ThumbnailUiModel
import com.on.staccato.presentation.memoryupdate.CategoryUpdateError
import com.on.staccato.presentation.util.convertMemoryUriToFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private typealias ThumbnailUri = Uri

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

        private var categoryId: Long = 0L

        private val _errorMessage = MutableLiveData<String>()
        val errorMessage: LiveData<String> get() = _errorMessage

        private val _error = MutableSingleLiveData<CategoryUpdateError>()
        val error: SingleLiveData<CategoryUpdateError> get() = _error

        private val thumbnailJobs = mutableMapOf<ThumbnailUri, Job>()

        fun fetchCategory(id: Long) {
            categoryId = id
            viewModelScope.launch {
                val result = categoryRepository.getCategory(categoryId)
                result
                    .onSuccess(::initializeCategory)
                    .onServerError(::handleInitializeCategoryError)
                    .onException(::handleInitializeCategoryException)
            }
        }

        fun updateCategory() {
            viewModelScope.launch {
                val newCategory: NewCategory = makeNewCategory()
                val result: ResponseResult<Unit> = categoryRepository.updateCategory(categoryId, newCategory)
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

        private fun initializeCategory(category: Category) {
            _thumbnail.value = _thumbnail.value?.updateUrl(category.categoryThumbnailUrl)
            title.set(category.categoryTitle)
            description.set(category.description)
            _startDate.value = category.startAt
            _endDate.value = category.endAt
            checkCategoryHasPeriod(category)
        }

        private fun checkCategoryHasPeriod(category: Category) {
            isPeriodActive.value = category.startAt != null && category.endAt != null
        }

        private fun makeNewCategory() =
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
            val thumbnailFile = convertMemoryUriToFile(context, uri, name = CATEGORY_FILE_NAME)
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
                _error.setValue(CategoryUpdateError.Thumbnail(message, uri))
            }
        }

        private fun handleInitializeCategoryError(
            status: Status,
            message: String,
        ) {
            _errorMessage.value = message
        }

        private fun handleInitializeCategoryException(
            e: Throwable,
            message: String,
        ) {
            _error.setValue(CategoryUpdateError.CategoryInitialization(message))
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
            _error.setValue(CategoryUpdateError.CategoryUpdate(message))
        }

        companion object {
            private const val CATEGORY_FILE_NAME = "imageFile"
        }
    }
