package com.on.staccato.presentation.staccatoupdate.viewmodel

import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onServerError
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.dto.Status
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.domain.repository.TimelineRepository
import com.on.staccato.presentation.common.AttachedPhotoHandler
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.staccatocreation.model.AttachedPhotoUiModel
import com.on.staccato.presentation.staccatocreation.model.AttachedPhotosUiModel
import com.on.staccato.presentation.staccatocreation.model.AttachedPhotosUiModel.Companion.createPhotosByUrls
import com.on.staccato.presentation.staccatocreation.viewmodel.StaccatoCreationViewModel
import com.on.staccato.presentation.staccatocreation.viewmodel.StaccatoCreationViewModel.Companion.FAIL_IMAGE_UPLOAD_MESSAGE
import com.on.staccato.presentation.staccatoupdate.StaccatoUpdateError
import com.on.staccato.presentation.util.convertExcretaFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class StaccatoUpdateViewModel
    @Inject
    constructor(
        private val timelineRepository: TimelineRepository,
        private val staccatoRepository: StaccatoRepository,
        private val imageRepository: ImageRepository,
    ) : AttachedPhotoHandler, ViewModel() {
        val staccatoTitle = ObservableField<String>()

        private val _placeName = MutableLiveData<String?>(null)
        val placeName: LiveData<String?> get() = _placeName

        private val _address = MutableLiveData<String?>(null)
        val address: LiveData<String?> get() = _address

        private val _currentPhotos =
            MutableLiveData(AttachedPhotosUiModel(emptyList()))
        val currentPhotos: LiveData<AttachedPhotosUiModel> get() = _currentPhotos

        private val _pendingPhotos = MutableSingleLiveData<List<AttachedPhotoUiModel>>()
        val pendingPhotos: SingleLiveData<List<AttachedPhotoUiModel>> get() = _pendingPhotos

        private val _latitude = MutableLiveData<Double?>()
        private val latitude: LiveData<Double?> get() = _latitude

        private val _longitude = MutableLiveData<Double?>()
        private val longitude: LiveData<Double?> get() = _longitude

        private val _memoryCandidates = MutableLiveData<MemoryCandidates>()
        val memoryCandidates: LiveData<MemoryCandidates> get() = _memoryCandidates

        private val _selectedVisitedAt = MutableLiveData<LocalDateTime?>()
        val selectedVisitedAt: LiveData<LocalDateTime?> get() = _selectedVisitedAt

        private val _isCurrentLocationLoading = MutableLiveData(false)
        val isCurrentLocationLoading: LiveData<Boolean> get() = _isCurrentLocationLoading

        private var targetMemoryId: Long = 0

        private val _selectedMemory = MutableLiveData<MemoryCandidate>()
        val selectedMemory: LiveData<MemoryCandidate> get() = _selectedMemory

        private val _isUpdateCompleted = MutableLiveData(false)
        val isUpdateCompleted: LiveData<Boolean> get() = _isUpdateCompleted

        private val _isPosting = MutableLiveData<Boolean>(false)
        val isPosting: LiveData<Boolean> get() = _isPosting

        private val _isAddPhotoClicked = MutableSingleLiveData(false)
        val isAddPhotoClicked: SingleLiveData<Boolean> get() = _isAddPhotoClicked

        private val photoJobs = mutableMapOf<String, Job>()

        private val _warningMessage = MutableSingleLiveData<String>()
        val warningMessage: SingleLiveData<String> get() = _warningMessage

        private val _error = MutableSingleLiveData<StaccatoUpdateError>()
        val error: SingleLiveData<StaccatoUpdateError> get() = _error

        override fun onAddClicked() {
            if ((currentPhotos.value?.size ?: 0) == StaccatoCreationViewModel.MAX_PHOTO_NUMBER) {
                _warningMessage.postValue(StaccatoCreationViewModel.MAX_PHOTO_NUMBER_MESSAGE)
            } else {
                _isAddPhotoClicked.postValue(true)
            }
        }

        override fun onDeleteClicked(deletedPhoto: AttachedPhotoUiModel) {
            _currentPhotos.value = currentPhotos.value?.removePhoto(deletedPhoto)
            if (photoJobs[deletedPhoto.uri.toString()]?.isActive == true) {
                photoJobs[deletedPhoto.uri.toString()]?.cancel()
            }
        }

        fun selectMemory(memory: MemoryCandidate) {
            _selectedMemory.value = memory
        }

        fun selectedVisitedAt(visitedAt: LocalDateTime) {
            _selectedVisitedAt.value = visitedAt
        }

        fun fetchTargetData(
            staccatoId: Long,
            memoryId: Long,
            memoryTitle: String,
        ) {
            targetMemoryId = memoryId
            fetchMemoryCandidates()
            fetchStaccatoBy(staccatoId)
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

        fun setPlaceByCurrentAddress(
            address: String?,
            location: Location,
        ) {
            setCurrentLocationLoading(false)
            _placeName.postValue(address)
            _address.postValue(address)
            _latitude.postValue(location.latitude)
            _longitude.postValue(location.longitude)
        }

        fun setCurrentLocationLoading(newValue: Boolean) {
            _isCurrentLocationLoading.postValue(newValue)
        }

        fun updateSelectedImageUris(newUris: Array<Uri>) {
            val updatedPhotos = currentPhotos.value!!.addPhotosByUris(newUris.toList())
            _currentPhotos.value = updatedPhotos
            _pendingPhotos.postValue(updatedPhotos.getPhotosWithoutUrls())
        }

        fun setUrisWithNewOrder(list: List<AttachedPhotoUiModel>) {
            _currentPhotos.value = AttachedPhotosUiModel(list)
        }

        fun fetchPhotosUrlsByUris(context: Context) {
            pendingPhotos.getValue()?.forEach { photo ->
                val job = createPhotoUploadJob(context, photo)
                job.invokeOnCompletion { _ ->
                    photoJobs.remove(photo.uri.toString())
                }
                photoJobs[photo.uri.toString()] = job
            }
        }

        fun updateStaccato(staccatoId: Long) {
            viewModelScope.launch {
                val staccatoTitleValue = staccatoTitle.get() ?: return@launch handleException()
                val placeNameValue = placeName.value ?: return@launch handleException()
                val addressValue = address.value ?: return@launch handleException()
                val latitudeValue = latitude.value ?: return@launch handleException()
                val longitudeValue = longitude.value ?: return@launch handleException()
                val visitedAtValue = selectedVisitedAt.value ?: return@launch handleException()
                val memoryIdValue = selectedMemory.value?.memoryId ?: return@launch handleException()
                val staccatoImageUrlsValue =
                    currentPhotos.value?.attachedPhotos?.map { it.imageUrl!! }
                        ?: emptyList()
                _isPosting.value = true
                staccatoRepository.updateStaccato(
                    staccatoId = staccatoId,
                    staccatoTitle = staccatoTitleValue,
                    placeName = placeNameValue,
                    address = addressValue,
                    latitude = latitudeValue,
                    longitude = longitudeValue,
                    visitedAt = visitedAtValue,
                    memoryId = memoryIdValue,
                    staccatoImageUrls = staccatoImageUrlsValue,
                ).onSuccess {
                    _isUpdateCompleted.postValue(true)
                }.onException(::handleUpdateException)
                    .onServerError(::handleServerError)
            }
        }

        private fun fetchStaccatoBy(staccatoId: Long) {
            viewModelScope.launch {
                staccatoRepository.getStaccato(staccatoId = staccatoId)
                    .onSuccess { staccato ->
                        staccatoTitle.set(staccato.staccatoTitle)
                        _address.value = staccato.address
                        _latitude.value = staccato.latitude
                        _longitude.value = staccato.longitude
                        _selectedVisitedAt.value = staccato.visitedAt
                        _placeName.value = staccato.placeName
                        _currentPhotos.value = createPhotosByUrls(staccato.staccatoImageUrls)
                        _selectedMemory.value =
                            MemoryCandidate(
                                staccato.memoryId,
                                staccato.memoryTitle,
                                staccato.startAt,
                                staccato.endAt,
                            )
                    }.onException(::handleInitializeException)
                    .onServerError(::handleServerError)
            }
        }

        private fun fetchMemoryCandidates() {
            viewModelScope.launch {
                timelineRepository.getMemoryCandidates()
                    .onSuccess { memoryCandidates ->
                        _memoryCandidates.value = memoryCandidates
                    }.onException(::handleMemoryCandidatesException)
                    .onServerError(::handleServerError)
            }
        }

        private fun createPhotoUploadJob(
            context: Context,
            photo: AttachedPhotoUiModel,
        ) = viewModelScope.async(buildCoroutineExceptionHandler()) {
            val multiPartBody =
                convertExcretaFile(
                    context,
                    photo.uri,
                    StaccatoCreationViewModel.FORM_DATA_NAME,
                )
            imageRepository.convertImageFileToUrl(multiPartBody)
                .onSuccess {
                    updatePhotoWithUrl(photo, it.imageUrl)
                }.onException { e, message ->
                    if (this.isActive) handleUpdatePhotoException(e, message)
                }
                .onServerError(::handleServerError)
        }

        private fun buildCoroutineExceptionHandler(): CoroutineExceptionHandler {
            return CoroutineExceptionHandler { _, throwable ->
                _warningMessage.postValue(throwable.message ?: FAIL_IMAGE_UPLOAD_MESSAGE)
            }
        }

        private fun updatePhotoWithUrl(
            targetPhoto: AttachedPhotoUiModel,
            url: String,
        ) {
            val updatedPhoto = targetPhoto.updateUrl(url)
            _currentPhotos.value = currentPhotos.value?.updateOrAppendPhoto(updatedPhoto)
        }

        private fun handleServerError(
            status: Status,
            message: String,
        ) {
            _isPosting.value = false
            _warningMessage.setValue(message)
        }

        private fun handleUpdatePhotoException(
            e: Throwable = IllegalArgumentException(),
            errorMessage: String = IMAGE_UPLOAD_ERROR_MESSAGE,
        ) {
            _warningMessage.setValue(errorMessage)
        }

        private fun handleException(
            e: Throwable = IllegalArgumentException(),
            errorMessage: String = REQUIRED_VALUES_ERROR_MESSAGE,
        ) {
            _isPosting.value = false
            _warningMessage.setValue(errorMessage)
        }

        private fun handleMemoryCandidatesException(
            e: Throwable,
            message: String,
        ) {
            _error.setValue(StaccatoUpdateError.MemoryCandidates(message))
        }

        private fun handleInitializeException(
            e: Throwable,
            message: String,
        ) {
            _error.setValue(StaccatoUpdateError.StaccatoInitialize(message))
        }

        private fun handleUpdateException(
            e: Throwable = IllegalArgumentException(),
            message: String = REQUIRED_VALUES_ERROR_MESSAGE,
        ) {
            _isPosting.value = false
            _error.setValue(StaccatoUpdateError.StaccatoUpdate(message))
        }

        companion object {
            private const val IMAGE_UPLOAD_ERROR_MESSAGE = "이미지 업로드에 실패했습니다."
            private const val REQUIRED_VALUES_ERROR_MESSAGE = "필수 값을 모두 입력해 주세요."
        }
    }
