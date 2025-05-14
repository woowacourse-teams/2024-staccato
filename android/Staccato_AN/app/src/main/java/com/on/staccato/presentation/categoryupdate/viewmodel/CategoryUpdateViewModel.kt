package com.on.staccato.presentation.categoryupdate.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.dto.image.ImageResponse
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.onException2
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.presentation.categorycreation.model.ThumbnailUiModel
import com.on.staccato.presentation.categoryupdate.CategoryUpdateError
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.common.photo.FileUiModel
import com.on.staccato.presentation.util.CATEGORY_FILE_CHILD_NAME
import com.on.staccato.presentation.util.ExceptionState2
import com.on.staccato.presentation.util.IMAGE_FORM_DATA_NAME
import com.on.staccato.presentation.util.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
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

        val isPeriodActive = MutableLiveData<Boolean>()

        private var categoryId: Long = 0L

        private val _errorMessage = MutableLiveData<String>()
        val errorMessage: LiveData<String> get() = _errorMessage

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
                    .onServerError(::handleInitializeCategoryError)
                    .onException2(::handleInitializeCategoryException)
            }
        }

        fun updateCategory() {
            viewModelScope.launch {
                val newCategory: NewCategory = makeNewCategory()
                val result: ApiResult<Unit> = categoryRepository.updateCategory(categoryId, newCategory)
                result
                    .onSuccess { updateSuccessStatus() }
                    .onServerError(::handleUpdateError)
                    .onException2(::handleUpdateException)
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

        fun createThumbnailUrl(
            uri: Uri,
            file: FileUiModel,
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
            _color.value = CategoryColor.getColorBy(category.color)
            checkCategoryHasPeriod(category)
        }

        private fun checkCategoryHasPeriod(category: Category) {
            isPeriodActive.value = category.startAt != null && category.endAt != null
        }

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
            uri: Uri,
            file: FileUiModel,
        ) {
            val thumbnailJob = createFetchingThumbnailJob(uri, file)
            thumbnailJob.invokeOnCompletion {
                thumbnailJobs.remove(uri)
            }
            thumbnailJobs[uri] = thumbnailJob
        }

        private fun createFetchingThumbnailJob(
            uri: Uri,
            file: FileUiModel,
        ): Job {
            val formData = createFormData(file)

            return viewModelScope.launch {
                val result: ApiResult<ImageResponse> =
                    imageRepository.convertImageFileToUrl(formData)
                result
                    .onSuccess(::setThumbnailUrl)
                    .onServerError(::handlePhotoError)
                    .onException2 { state ->
                        handlePhotoException(state, uri, file)
                    }
            }
        }

        private fun createFormData(fileUiModel: FileUiModel): MultipartBody.Part {
            val mediaType: MediaType? = fileUiModel.contentType?.toMediaTypeOrNull()
            val requestFile: RequestBody = fileUiModel.file.asRequestBody(mediaType)

            return MultipartBody.Part.createFormData(
                IMAGE_FORM_DATA_NAME,
                CATEGORY_FILE_CHILD_NAME,
                requestFile,
            )
        }

        private fun setThumbnailUrl(imageResponse: ImageResponse) {
            val newUrl = imageResponse.imageUrl
            _thumbnail.value = _thumbnail.value?.updateUrl(newUrl)
            _isPhotoPosting.value = false
        }

        private fun handlePhotoError(message: String) {
            _errorMessage.value = message
        }

        private fun handlePhotoException(
            state: ExceptionState2,
            uri: Uri,
            fileUiModel: FileUiModel,
        ) {
            if (thumbnailJobs[uri]?.isActive == true) {
                _error.setValue(CategoryUpdateError.Thumbnail(state, uri, fileUiModel))
            }
        }

        private fun handleInitializeCategoryError(message: String) {
            _errorMessage.value = message
        }

        private fun handleInitializeCategoryException(state: ExceptionState2) {
            _error.setValue(CategoryUpdateError.CategoryInitialization(state))
        }

        private fun handleUpdateError(message: String) {
            _isPosting.value = false
            _errorMessage.value = message
        }

        private fun handleUpdateException(state: ExceptionState2) {
            _isPosting.value = false
            _error.setValue(CategoryUpdateError.CategoryUpdate(state))
        }
    }
