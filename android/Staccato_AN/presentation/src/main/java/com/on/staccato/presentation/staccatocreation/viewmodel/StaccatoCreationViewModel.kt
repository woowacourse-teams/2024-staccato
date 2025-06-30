package com.on.staccato.presentation.staccatocreation.viewmodel

import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.ExceptionType
import com.on.staccato.domain.UploadFile
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.CategoryCandidates.Companion.emptyCategoryCandidates
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.LocationRepository
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.common.categoryselection.CategorySelectionViewModel
import com.on.staccato.presentation.common.photo.AttachedPhotoHandler
import com.on.staccato.presentation.common.photo.PhotoUiModel
import com.on.staccato.presentation.common.photo.PhotosUiModel
import com.on.staccato.presentation.common.photo.PhotosUiModel.Companion.MAX_PHOTO_NUMBER
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.staccatocreation.StaccatoCreationActivity.Companion.DEFAULT_CATEGORY_ID
import com.on.staccato.presentation.staccatocreation.StaccatoCreationError
import com.on.staccato.presentation.util.ExceptionState
import com.on.staccato.toMessageId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class StaccatoCreationViewModel
    @Inject
    constructor(
        private val categoryRepository: CategoryRepository,
        private val staccatoRepository: StaccatoRepository,
        private val imageRepository: ImageRepository,
        private val locationRepository: LocationRepository,
    ) : ViewModel(), CategorySelectionViewModel, AttachedPhotoHandler {
        val staccatoTitle = ObservableField<String>()

        private val _placeName = MutableLiveData<String?>(null)
        val placeName: LiveData<String?> get() = _placeName

        private val _address = MutableLiveData<String?>(null)
        val address: LiveData<String?> get() = _address

        private val _currentPhotos =
            MutableLiveData(PhotosUiModel(emptyList()))
        val currentPhotos: LiveData<PhotosUiModel> get() = _currentPhotos

        private val _pendingPhotos = MutableSingleLiveData<List<PhotoUiModel>>()
        val pendingPhotos: SingleLiveData<List<PhotoUiModel>> get() = _pendingPhotos

        private val _currentLocation = MutableLiveData<LocationUiModel>()
        val currentLocation: LiveData<LocationUiModel> get() = _currentLocation

        private val _latitude = MutableLiveData<Double?>()
        private val latitude: LiveData<Double?> get() = _latitude

        private val _longitude = MutableLiveData<Double?>()
        private val longitude: LiveData<Double?> get() = _longitude

        private val _selectedCategory = MutableLiveData<CategoryCandidate>()
        override val selectedCategory: LiveData<CategoryCandidate> get() = _selectedCategory

        private val _selectableCategories = MutableLiveData<CategoryCandidates>()
        override val selectableCategories: LiveData<CategoryCandidates> get() = _selectableCategories

        private val _categoryCandidates = MutableLiveData<CategoryCandidates>()
        val categoryCandidates: LiveData<CategoryCandidates> get() = _categoryCandidates

        private val _selectedVisitedAt = MutableLiveData<LocalDateTime?>()
        val selectedVisitedAt: LiveData<LocalDateTime?> get() = _selectedVisitedAt

        private val _isCurrentLocationLoading = MutableLiveData<Boolean>(false)
        val isCurrentLocationLoading: LiveData<Boolean> get() = _isCurrentLocationLoading

        private val _createdStaccatoId = MutableSingleLiveData<Long>()
        val createdStaccatoId: SingleLiveData<Long> get() = _createdStaccatoId

        private val _isPosting = MutableLiveData<Boolean>(false)
        val isPosting: LiveData<Boolean> get() = _isPosting

        private val _isAddPhotoClicked = MutableSingleLiveData(false)
        val isAddPhotoClicked: SingleLiveData<Boolean> get() = _isAddPhotoClicked

        private val photoJobs = mutableMapOf<String, Job>()

        private val _isFromPlaceSearch = MutableSingleLiveData(false)
        val isFromPlaceSearch: SingleLiveData<Boolean> get() = _isFromPlaceSearch

        private val _warningMessage = MutableSingleLiveData<String>()
        val warningMessage: SingleLiveData<String> get() = _warningMessage

        private val _exceptionMessage = MutableSingleLiveData<Int>()
        val exceptionMessage: SingleLiveData<Int> get() = _exceptionMessage

        private val _error = MutableSingleLiveData<StaccatoCreationError>()
        val error: SingleLiveData<StaccatoCreationError> get() = _error

        override fun onAddClicked() {
            if ((currentPhotos.value?.size ?: 0) == MAX_PHOTO_NUMBER) {
                _warningMessage.postValue(MAX_PHOTO_NUMBER_MESSAGE)
            } else {
                _isAddPhotoClicked.postValue(true)
            }
        }

        override fun onDeleteClicked(deletedPhoto: PhotoUiModel) {
            _currentPhotos.value = currentPhotos.value?.removePhoto(deletedPhoto)
            if (photoJobs[deletedPhoto.uri.toString()]?.isActive == true) {
                photoJobs[deletedPhoto.uri.toString()]?.cancel()
            }
        }

        override fun onRetryClicked(retryPhoto: PhotoUiModel) {
            _currentPhotos.value = currentPhotos.value?.toLoading(retryPhoto)
            _pendingPhotos.postValue(listOf(retryPhoto))
        }

        fun getCurrentLocation() {
            _isCurrentLocationLoading.value = true
            locationRepository.getCurrentLocation { latitude, longitude ->
                _currentLocation.value = LocationUiModel(latitude, longitude)
                _isCurrentLocationLoading.value = false
            }
        }

        fun selectedVisitedAt(visitedAt: LocalDateTime) {
            _selectedVisitedAt.value = visitedAt
        }

        override fun selectCategory(position: Int) {
            _selectedCategory.value =
                selectableCategories.value?.categoryCandidates?.get(position) ?: return
        }

        fun selectNewPlace(
            placeId: String,
            name: String,
            address: String,
            longitude: Double,
            latitude: Double,
        ) {
            _isFromPlaceSearch.setValue(true)
            _placeName.value = name
            _address.value = address
            _longitude.value = longitude
            _latitude.value = latitude
        }

        fun clearPlace() {
            _placeName.value = null
            _address.value = null
            _longitude.value = null
            _latitude.value = null
        }

        fun setPlaceByCurrentAddress(address: String?) {
            _isCurrentLocationLoading.postValue(false)
            _placeName.postValue(address)
            _address.postValue(address)
            _currentLocation.value?.let {
                _latitude.postValue(it.latitude)
                _longitude.postValue(it.longitude)
            }
        }

        fun fetchCategoryCandidates() {
            viewModelScope.launch {
                categoryRepository.getCategoryCandidates()
                    .onSuccess {
                        _categoryCandidates.value = it
                    }
                    .onException(::handleCategoryCandidatesException)
                    .onServerError(::handleServerError)
            }
        }

        fun initCategoryAndVisitedAt(
            categoryId: Long,
            currentDateTime: LocalDateTime,
        ) {
            if (categoryId == DEFAULT_CATEGORY_ID) {
                updateCategorySelectionBy(currentDateTime)
                setCurrentDateTimeAs(currentDateTime)
            } else {
                updateCategorySelectionBy(categoryId)
                setClosestDateTimeAs(currentDateTime)
            }
        }

        private fun setCurrentDateTimeAs(visitedAt: LocalDateTime) {
            _selectedVisitedAt.value = visitedAt
        }

        fun updateCategorySelectionBy(visitedAt: LocalDateTime) {
            val filteredCategories =
                categoryCandidates.value?.filterBy(visitedAt.toLocalDate()) ?: emptyCategoryCandidates
            _selectableCategories.value = filteredCategories
            // TODO: 리팩터링
            _selectedCategory.value =
                filteredCategories.findByIdOrFirst(selectedCategory.value?.categoryId) ?: return
        }

        private fun updateCategorySelectionBy(categoryId: Long) {
            val selectedCategory =
                categoryCandidates.value?.findBy(categoryId) ?: throw IllegalArgumentException()
            _selectableCategories.value = CategoryCandidates.from(selectedCategory)
            _selectedCategory.value = selectedCategory
        }

        private fun setClosestDateTimeAs(visitedAt: LocalDateTime) {
            _selectedVisitedAt.value = selectedCategory.value?.getClosestDateTime(visitedAt)
        }

        fun updateSelectedImageUris(newUris: Array<Uri>) {
            val updatedPhotos = currentPhotos.value!!.addPhotosByUris(newUris.toList())
            _currentPhotos.value = updatedPhotos
            _pendingPhotos.postValue(updatedPhotos.getLoadingPhotosWithoutUrls())
        }

        fun setUrisWithNewOrder(list: List<PhotoUiModel>) {
            _currentPhotos.value = PhotosUiModel(list)
        }

        fun launchPhotoUploadJob(
            file: UploadFile,
            photo: PhotoUiModel,
        ) {
            val job = createPhotoUploadJob(file, photo)
            job.invokeOnCompletion { photoJobs.remove(photo.uri.toString()) }
            photoJobs[photo.uri.toString()] = job
        }

        fun createStaccato() =
            viewModelScope.launch {
                _isPosting.value = true
                staccatoRepository.createStaccato(
                    staccatoTitle = staccatoTitle.get() ?: return@launch handleException(),
                    placeName = placeName.value ?: return@launch handleException(),
                    latitude = latitude.value ?: return@launch handleException(),
                    longitude = longitude.value ?: return@launch handleException(),
                    address = address.value ?: return@launch handleException(),
                    visitedAt = selectedVisitedAt.value ?: return@launch handleException(),
                    categoryId = selectedCategory.value!!.categoryId,
                    staccatoImageUrls = currentPhotos.value?.imageUrls() ?: emptyList(),
                ).onSuccess { response ->
                    _createdStaccatoId.postValue(response)
                }.onException(::handleCreateException)
                    .onServerError(::handleServerError)
            }

        private fun createPhotoUploadJob(
            file: UploadFile,
            photo: PhotoUiModel,
        ) = viewModelScope.launch(buildCoroutineExceptionHandler()) {
            imageRepository.convertImageFileToUrl(file)
                .onSuccess { url ->
                    updatePhotoWithUrl(photo, url)
                }.onException { state ->
                    if (isActive) handlePhotoException(photo.toRetry(), state.toMessageId())
                }
                .onServerError { message ->
                    if (isActive) handlePhotoServerError(photo.toFail(), message)
                }
        }

        private fun buildCoroutineExceptionHandler(): CoroutineExceptionHandler {
            return CoroutineExceptionHandler { _, throwable ->
                _warningMessage.postValue(throwable.message ?: FAIL_IMAGE_UPLOAD_MESSAGE)
            }
        }

        private fun updatePhotoWithUrl(
            targetPhoto: PhotoUiModel,
            url: String,
        ) {
            val updatedPhoto = targetPhoto.toSuccessPhotoWith(url)
            _currentPhotos.value = currentPhotos.value?.updatePhoto(updatedPhoto)
        }

        private fun handleServerError(errorMessage: String) {
            _isPosting.value = false
            _warningMessage.postValue(errorMessage)
        }

        private fun handleException(exceptionState: ExceptionState = ExceptionState.RequiredValuesMissing) {
            _isPosting.value = false
            _warningMessage.setValue(exceptionState.message)
        }

        private fun handlePhotoServerError(
            photo: PhotoUiModel,
            message: String,
        ) {
            _currentPhotos.value = currentPhotos.value?.updatePhoto(photo)
            _warningMessage.setValue(message)
        }

        private fun handlePhotoException(
            photo: PhotoUiModel,
            messageId: Int,
        ) {
            _currentPhotos.value = currentPhotos.value?.updatePhoto(photo)
            _exceptionMessage.setValue(messageId)
        }

        private fun handleCategoryCandidatesException(exceptionState: ExceptionType) {
            _error.setValue(StaccatoCreationError.CategoryCandidates(exceptionState.toMessageId()))
        }

        private fun handleCreateException(state: ExceptionType) {
            _isPosting.value = false
            _error.setValue(StaccatoCreationError.StaccatoCreation(state.toMessageId()))
        }

        companion object {
            const val MAX_PHOTO_NUMBER_MESSAGE = "사진은 최대 ${MAX_PHOTO_NUMBER}장만 첨부할 수 있어요!"
            const val FAIL_IMAGE_UPLOAD_MESSAGE = "이미지 업로드에 실패했습니다."
        }
    }
