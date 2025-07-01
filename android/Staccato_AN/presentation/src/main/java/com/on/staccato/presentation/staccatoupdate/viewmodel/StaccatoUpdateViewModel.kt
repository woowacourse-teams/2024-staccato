package com.on.staccato.presentation.staccatoupdate.viewmodel

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
import com.on.staccato.domain.model.Staccato
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
import com.on.staccato.presentation.common.photo.PhotosUiModel.Companion.toSuccessPhotos
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.staccatocreation.viewmodel.StaccatoCreationViewModel
import com.on.staccato.presentation.staccatocreation.viewmodel.StaccatoCreationViewModel.Companion.FAIL_IMAGE_UPLOAD_MESSAGE
import com.on.staccato.presentation.staccatoupdate.AllCandidates
import com.on.staccato.presentation.staccatoupdate.StaccatoInitialize
import com.on.staccato.presentation.staccatoupdate.StaccatoUpdate
import com.on.staccato.presentation.staccatoupdate.StaccatoUpdateError
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
class StaccatoUpdateViewModel
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

        private val _isUpdateCompleted = MutableLiveData(false)
        val isUpdateCompleted: LiveData<Boolean> get() = _isUpdateCompleted

        private val _isPosting = MutableLiveData<Boolean>(false)
        val isPosting: LiveData<Boolean> get() = _isPosting

        private val _isAddPhotoClicked = MutableSingleLiveData(false)
        val isAddPhotoClicked: SingleLiveData<Boolean> get() = _isAddPhotoClicked

        private val photoJobs = mutableMapOf<String, Job>()

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent> get() = _messageEvent

        private val _error = MutableSingleLiveData<StaccatoUpdateError>()
        val error: SingleLiveData<StaccatoUpdateError> get() = _error

        override fun onAddClicked() {
            if ((currentPhotos.value?.size ?: 0) == MAX_PHOTO_NUMBER) {
                updateMessageEvent(MessageEvent.from(StaccatoCreationViewModel.MAX_PHOTO_NUMBER_MESSAGE))
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

        override fun selectCategory(position: Int) {
            selectableCategories.value.categoryCandidates[position].let {
                _selectedCategory.value = it
            }
        }

        fun fetchTargetData(staccatoId: Long) {
            viewModelScope.launch {
                val staccatoJob = launch { fetchStaccatoBy(staccatoId) }
                val categoryCandidatesJob = launch { fetchCategoryCandidates() }
                staccatoJob.join()
                categoryCandidatesJob.join()
                initializeSelectableCategories()
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

        fun selectVisitedAt(visitedAt: LocalDateTime) {
            _selectedVisitedAt.value = visitedAt
        }

        fun selectNewPlace(
            placeId: String,
            name: String,
            address: String,
            longitude: Double,
            latitude: Double,
        ) {
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

        fun updateCategorySelectionBy(visitedAt: LocalDateTime) {
            val filteredCategories = allCategories.value.filterBy(visitedAt.toLocalDate())
            _selectableCategories.value = filteredCategories
            _selectedCategory.value =
                filteredCategories.findByIdOrFirst(selectedCategory.value?.categoryId)
        }

        fun updateStaccato(staccatoId: Long) {
            viewModelScope.launch {
                _isPosting.value = true
                staccatoRepository.updateStaccato(
                    staccatoId = staccatoId,
                    staccatoTitle = staccatoTitle.get() ?: return@launch requireValues(),
                    placeName = placeName.value ?: return@launch requireValues(),
                    address = address.value ?: return@launch requireValues(),
                    latitude = latitude.value ?: return@launch requireValues(),
                    longitude = longitude.value ?: return@launch requireValues(),
                    visitedAt = selectedVisitedAt.value ?: return@launch requireValues(),
                    categoryId = selectedCategory.value?.categoryId ?: return@launch requireValues(),
                    staccatoImageUrls = currentPhotos.value?.imageUrls() ?: emptyList(),
                ).onSuccess { _isUpdateCompleted.value = true }
                    .onException { updateError(StaccatoUpdate(MessageEvent.from(it))) }
                    .onServerError { updateError(StaccatoUpdate(MessageEvent.from(it))) }
                _isPosting.value = false
            }
        }

        private suspend fun fetchCategoryCandidates() {
            categoryRepository.getCategoryCandidates()
                .onSuccess { _allCategories.value = it }
                .onException { updateError(AllCandidates(MessageEvent.from(it))) }
                .onServerError { updateError(AllCandidates(MessageEvent.from(it))) }
        }

        private suspend fun fetchStaccatoBy(staccatoId: Long) {
            staccatoRepository.getStaccato(staccatoId = staccatoId)
                .onSuccess { staccato ->
                    staccatoTitle.set(staccato.staccatoTitle)
                    _currentPhotos.value = staccato.staccatoImageUrls.toSuccessPhotos()
                    selectVisitedAt(staccato.visitedAt)
                    initializePlaceBy(staccato)
                    initializeSelectedCategory(staccato)
                }.onException { updateError(StaccatoInitialize(MessageEvent.from(it))) }
                .onServerError { updateError(StaccatoInitialize(MessageEvent.from(it))) }
        }

        private fun initializePlaceBy(staccato: Staccato) {
            selectNewPlace(
                placeId = "필요 없는 파라미터",
                name = staccato.placeName,
                address = staccato.address,
                longitude = staccato.longitude,
                latitude = staccato.latitude,
            )
        }

        private fun initializeSelectedCategory(staccato: Staccato) {
            _selectedCategory.value =
                CategoryCandidate(
                    staccato.categoryId,
                    staccato.categoryTitle,
                    staccato.startAt,
                    staccato.endAt,
                )
        }

        private fun initializeSelectableCategories() {
            selectedVisitedAt.value?.toLocalDate()?.let { visitedAt ->
                _selectableCategories.value = allCategories.value.filterBy(visitedAt)
            }
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

        private fun updateError(error: StaccatoUpdateError) {
            _error.setValue(error)
        }

        private fun handlePhotoError(
            photo: PhotoUiModel,
            messageEvent: MessageEvent,
        ) {
            _currentPhotos.value = currentPhotos.value?.updatePhoto(photo)
            updateMessageEvent(messageEvent)
        }

        private fun requireValues() {
            _isPosting.value = false
            updateMessageEvent(MessageEvent.from(ExceptionType.REQUIRED_VALUES))
        }

        private fun updateMessageEvent(messageEvent: MessageEvent) {
            _messageEvent.setValue(messageEvent)
        }
    }
