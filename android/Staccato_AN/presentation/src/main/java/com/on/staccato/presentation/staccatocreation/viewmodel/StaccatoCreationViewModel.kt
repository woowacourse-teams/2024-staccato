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
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.common.categoryselection.CategorySelectionViewModel
import com.on.staccato.presentation.common.photo.AttachedPhotoHandler
import com.on.staccato.presentation.common.photo.PhotoUiModel
import com.on.staccato.presentation.common.photo.PhotosUiModel
import com.on.staccato.presentation.common.photo.PhotosUiModel.Companion.MAX_PHOTO_NUMBER
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.staccatocreation.AllCandidates
import com.on.staccato.presentation.staccatocreation.StaccatoCreate
import com.on.staccato.presentation.staccatocreation.StaccatoCreationActivity.Companion.DEFAULT_CATEGORY_ID
import com.on.staccato.presentation.staccatocreation.StaccatoCreationError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

        private val _allCategories = MutableStateFlow(emptyCategoryCandidates)
        val allCategories: StateFlow<CategoryCandidates> get() = _allCategories

        private val _selectableCategories = MutableStateFlow(emptyCategoryCandidates)
        override val selectableCategories: StateFlow<CategoryCandidates> get() = _selectableCategories

        private val _selectedCategory = MutableStateFlow<CategoryCandidate?>(null)
        override val selectedCategory: StateFlow<CategoryCandidate?> get() = _selectedCategory

        private val _selectedVisitedAt = MutableStateFlow<LocalDateTime?>(null)
        val selectedVisitedAt: StateFlow<LocalDateTime?> get() = _selectedVisitedAt

        private val _isCurrentLocationLoading = MutableLiveData(false)
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

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent> get() = _messageEvent

        private val _error = MutableSingleLiveData<StaccatoCreationError>()
        val error: SingleLiveData<StaccatoCreationError> get() = _error

        override fun onAddClicked() {
            if ((currentPhotos.value?.size ?: 0) == MAX_PHOTO_NUMBER) {
                handleMessageEvent(MessageEvent.from(MAX_PHOTO_NUMBER_MESSAGE))
            } else {
                _isAddPhotoClicked.setValue(true)
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
            _pendingPhotos.setValue(listOf(retryPhoto))
        }

        fun getCurrentLocation() {
            _isCurrentLocationLoading.value = true
            locationRepository.getCurrentLocation { latitude, longitude ->
                _currentLocation.value = LocationUiModel(latitude, longitude)
            }
        }

        fun selectedVisitedAt(visitedAt: LocalDateTime) {
            _selectedVisitedAt.value = visitedAt
        }

        override fun selectCategory(position: Int) {
            _selectedCategory.value = selectableCategories.value.categoryCandidates[position]
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
            _isCurrentLocationLoading.value = false
            _placeName.value = address
            _address.value = address
            _currentLocation.value?.let {
                _latitude.value = it.latitude
                _longitude.value = it.longitude
            }
        }

        fun fetchAllCategories() {
            viewModelScope.launch {
                categoryRepository.getCategoryCandidates()
                    .onSuccess { _allCategories.value = it }
                    .onException { updateError(AllCandidates(MessageEvent.from(it))) }
                    .onServerError { updateError(AllCandidates(MessageEvent.from(it))) }
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
            val filteredCategories = allCategories.value.filterBy(visitedAt.toLocalDate())
            _selectableCategories.value = filteredCategories
            _selectedCategory.value = filteredCategories.findByIdOrFirst(selectedCategory.value?.categoryId)
        }

        private fun updateCategorySelectionBy(categoryId: Long) {
            val selectedCategory = allCategories.value.findBy(categoryId)
            _selectedCategory.value = selectedCategory
            selectedCategory?.let { _selectableCategories.value = CategoryCandidates.from(it) }
        }

        private fun setClosestDateTimeAs(visitedAt: LocalDateTime) {
            _selectedVisitedAt.value = selectedCategory.value?.getClosestDateTime(visitedAt)
        }

        fun updateSelectedImageUris(newUris: Array<Uri>) {
            val updatedPhotos = currentPhotos.value!!.addPhotosByUris(newUris.toList())
            _currentPhotos.value = updatedPhotos
            _pendingPhotos.setValue(updatedPhotos.getLoadingPhotosWithoutUrls())
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
                    staccatoTitle = staccatoTitle.get() ?: return@launch requireValues(),
                    placeName = placeName.value ?: return@launch requireValues(),
                    latitude = latitude.value ?: return@launch requireValues(),
                    longitude = longitude.value ?: return@launch requireValues(),
                    address = address.value ?: return@launch requireValues(),
                    visitedAt = selectedVisitedAt.value ?: return@launch requireValues(),
                    categoryId = selectedCategory.value!!.categoryId,
                    staccatoImageUrls = currentPhotos.value?.imageUrls() ?: emptyList(),
                ).onSuccess { _createdStaccatoId.setValue(it) }
                    .onException { updateError(StaccatoCreate(MessageEvent.from(it))) }
                    .onServerError { updateError(StaccatoCreate(MessageEvent.from(it))) }
                _isPosting.value = false
            }

        private fun createPhotoUploadJob(
            file: UploadFile,
            photo: PhotoUiModel,
        ) = viewModelScope.launch(buildCoroutineExceptionHandler(photo)) {
            imageRepository.convertImageFileToUrl(file)
                .onSuccess { updatePhotoWithUrl(photo, it) }
                .onException { if (isActive) handlePhotoError(photo.toRetry(), MessageEvent.from(it)) }
                .onServerError { if (isActive) handlePhotoError(photo.toFail(), MessageEvent.from(it)) }
        }

        private fun buildCoroutineExceptionHandler(photo: PhotoUiModel): CoroutineExceptionHandler {
            return CoroutineExceptionHandler { _, throwable ->
                val message = throwable.message ?: FAIL_IMAGE_UPLOAD_MESSAGE
                handlePhotoError(photo.toRetry(), MessageEvent.from(message))
            }
        }

        private fun updatePhotoWithUrl(
            targetPhoto: PhotoUiModel,
            url: String,
        ) {
            val updatedPhoto = targetPhoto.toSuccessPhotoWith(url)
            _currentPhotos.value = currentPhotos.value?.updatePhoto(updatedPhoto)
        }

        private fun updateError(error: StaccatoCreationError) {
            _error.setValue(error)
        }

        private fun handlePhotoError(
            photo: PhotoUiModel,
            messageEvent: MessageEvent,
        ) {
            _currentPhotos.value = currentPhotos.value?.updatePhoto(photo)
            handleMessageEvent(messageEvent)
        }

        private fun requireValues() {
            _isPosting.value = false
            handleMessageEvent(MessageEvent.from(ExceptionType.REQUIRED_VALUES))
        }

        private fun handleMessageEvent(messageEvent: MessageEvent) {
            _messageEvent.setValue(messageEvent)
        }

        companion object {
            const val MAX_PHOTO_NUMBER_MESSAGE = "사진은 최대 ${MAX_PHOTO_NUMBER}장만 첨부할 수 있어요!"
            const val FAIL_IMAGE_UPLOAD_MESSAGE = "이미지 업로드에 실패했습니다."
        }
    }
