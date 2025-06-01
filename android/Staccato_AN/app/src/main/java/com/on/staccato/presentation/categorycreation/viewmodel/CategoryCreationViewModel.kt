package com.on.staccato.presentation.categorycreation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.dto.image.ImageResponse
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.onException2
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.model.NewCategory
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.presentation.categorycreation.model.CategoryCreationError
import com.on.staccato.presentation.categorycreation.model.ThumbnailUiModel
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.common.photo.UploadFile
import com.on.staccato.presentation.util.CATEGORY_FILE_CHILD_NAME
import com.on.staccato.presentation.util.ExceptionState2
import com.on.staccato.presentation.util.IMAGE_FORM_DATA_NAME
import com.on.staccato.presentation.util.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
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

        private val _errorMessage = MutableLiveData<String>()
        val errorMessage: LiveData<String> get() = _errorMessage

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
                val result: ApiResult<CategoryCreationResponse> =
                    categoryRepository.createCategory(category)
                result
                    .onSuccess(::setCreatedCategoryId)
                    .onServerError(::handleCreateServerError)
                    .onException2(::handleCreateException)
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

        private fun createFormData(uploadFile: UploadFile): MultipartBody.Part {
            val mediaType: MediaType? = uploadFile.contentType?.toMediaTypeOrNull()
            val requestFile: RequestBody = uploadFile.file.asRequestBody(mediaType)

            return createFormData(
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

        private fun setCreatedCategoryId(categoryCreationResponse: CategoryCreationResponse) {
            _isPosting.value = false
            _createdCategoryId.value = categoryCreationResponse.categoryId
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

        private fun handlePhotoError(message: String) {
            _errorMessage.postValue(message)
        }

        private fun handlePhotoException(
            state: ExceptionState2,
            uri: Uri,
            uploadFile: UploadFile,
        ) {
            if (thumbnailJobs[uri]?.isActive == true) {
                _error.setValue(CategoryCreationError.Thumbnail(state, uri, uploadFile))
            }
        }

        private fun handleCreateServerError(message: String) {
            _isPosting.value = false
            _errorMessage.postValue(message)
        }

        private fun handleCreateException(state: ExceptionState2) {
            _isPosting.value = false
            _error.setValue(CategoryCreationError.CategoryCreation(state))
        }
    }
