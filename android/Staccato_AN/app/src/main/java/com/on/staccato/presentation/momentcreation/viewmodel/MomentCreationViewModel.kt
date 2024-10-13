package com.on.staccato.presentation.momentcreation.viewmodel

import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.image.ImageDefaultRepository
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.domain.repository.TimelineRepository
import com.on.staccato.presentation.common.AttachedPhotoHandler
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.momentcreation.model.AttachedPhotoUiModel
import com.on.staccato.presentation.momentcreation.model.AttachedPhotosUiModel
import com.on.staccato.presentation.util.convertExcretaFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MomentCreationViewModel
    @Inject
    constructor(
        private val timelineRepository: TimelineRepository,
        private val staccatoRepository: StaccatoRepository,
        private val imageRepository: ImageDefaultRepository,
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

        private val _selectedMemory = MutableLiveData<MemoryCandidate>()
        val selectedMemory: LiveData<MemoryCandidate> get() = _selectedMemory

        private val _memoryCandidates = MutableLiveData<MemoryCandidates>()
        val memoryCandidates: LiveData<MemoryCandidates> get() = _memoryCandidates

        private val _selectedVisitedAt = MutableLiveData<LocalDateTime?>()
        val selectedVisitedAt: LiveData<LocalDateTime?> get() = _selectedVisitedAt

        private val _isCurrentLocationLoading = MutableLiveData<Boolean>()
        val isCurrentLocationLoading: LiveData<Boolean> get() = _isCurrentLocationLoading

        private val _createdStaccatoId = MutableSingleLiveData<Long>()
        val createdStaccatoId: SingleLiveData<Long> get() = _createdStaccatoId

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _isPosting = MutableLiveData<Boolean>(false)
        val isPosting: LiveData<Boolean> get() = _isPosting

        private val _isAddPhotoClicked = MutableSingleLiveData(false)
        val isAddPhotoClicked: SingleLiveData<Boolean> get() = _isAddPhotoClicked

        private val photoJobs = mutableMapOf<String, Job>()

        private val _isPlaceSearchClicked = MutableLiveData(false)
        val isPlaceSearchClicked: LiveData<Boolean> get() = _isPlaceSearchClicked

        override fun onAddClicked() {
            if ((currentPhotos.value?.size ?: 0) == MAX_PHOTO_NUMBER) {
                _errorMessage.postValue(MAX_PHOTO_NUMBER_MESSAGE)
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

        fun selectedVisitedAt(visitedAt: LocalDateTime) {
            _selectedVisitedAt.value = visitedAt
        }

        fun selectMemory(memory: MemoryCandidate) {
            _selectedMemory.value = memory
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

        fun fetchMemoryCandidates(memoryId: Long) {
            viewModelScope.launch {
                timelineRepository.getMemoryCandidates()
                    .onSuccess { memoryCandidates ->
                        _memoryCandidates.value = memoryCandidates
                        if (memoryCandidates.memoryCandidate.isNotEmpty()) {
                            initSelectedMemoryAndVisitedAt(memoryId, memoryCandidates.memoryCandidate)
                        }
                    }
            }
        }

        fun setIsPlaceSearchClicked(value: Boolean) {
            _isPlaceSearchClicked.value = value
        }

        private fun initSelectedMemoryAndVisitedAt(
            memoryId: Long,
            memoryCandidates: List<MemoryCandidate>,
        ) {
            val targetMemory =
                if (memoryId == 0L) {
                    memoryCandidates.first()
                } else {
                    memoryCandidates.first { memoryId == it.memoryId }
                }
            _selectedMemory.value = targetMemory
            _selectedVisitedAt.value = getClosestDateTime(targetMemory.startAt, targetMemory.endAt)
        }

        private fun getClosestDateTime(
            startAt: LocalDate?,
            endAt: LocalDate?,
        ): LocalDateTime {
            val now = LocalDateTime.now()

            if (startAt == null || endAt == null) return now

            val startDateTime = startAt.atStartOfDay()
            val endDateTime = endAt.atStartOfDay()

            return when {
                now.isBefore(startDateTime) -> startDateTime // 현재 시간이 startAt 이전일 때 startAt 반환
                now.isAfter(endDateTime) -> endDateTime // 현재 시간이 endAt 이후일 때 endAt 반환
                else -> now // 현재 시간이 범위 내에 있을 때 now 반환
            }
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

        fun createMoment() =
            viewModelScope.launch {
                _isPosting.value = true
                staccatoRepository.createStaccato(
                    memoryId = selectedMemory.value!!.memoryId,
                    staccatoTitle = staccatoTitle.get() ?: return@launch,
                    placeName = placeName.value ?: return@launch,
                    latitude = latitude.value ?: return@launch,
                    longitude = longitude.value ?: return@launch,
                    address = address.value ?: return@launch,
                    visitedAt = selectedVisitedAt.value ?: return@launch,
                    staccatoImageUrls = currentPhotos.value!!.attachedPhotos.map { it.imageUrl!! },
                ).onSuccess { response ->
                    _createdStaccatoId.postValue(response.momentId)
                }.onFailure {
                    _isPosting.value = false
                    _errorMessage.postValue(it.message ?: "방문을 생성할 수 없어요!")
                }
            }

        private fun createPhotoUploadJob(
            context: Context,
            photo: AttachedPhotoUiModel,
        ) = viewModelScope.launch(buildCoroutineExceptionHandler()) {
            val multiPartBody = convertExcretaFile(context, photo.uri, FORM_DATA_NAME)
            imageRepository.convertImageFileToUrl(multiPartBody)
                .onSuccess {
                    updatePhotoWithUrl(photo, it.imageUrl)
                }
                .onException { _, message ->
                    _errorMessage.postValue(message)
                }
        }

        private fun buildCoroutineExceptionHandler(): CoroutineExceptionHandler {
            return CoroutineExceptionHandler { _, throwable ->
                _errorMessage.postValue(throwable.message ?: "이미지 업로드에 실패했습니다.")
            }
        }

        private fun updatePhotoWithUrl(
            targetPhoto: AttachedPhotoUiModel,
            url: String,
        ) {
            val updatedPhoto = targetPhoto.updateUrl(url)
            _currentPhotos.value = currentPhotos.value?.updateOrAppendPhoto(updatedPhoto)
        }

        companion object {
            const val MAX_PHOTO_NUMBER = 5
            const val MAX_PHOTO_NUMBER_MESSAGE = "사진은 최대 ${MAX_PHOTO_NUMBER}장만 첨부할 수 있어요!"
            const val FORM_DATA_NAME = "imageFile"
        }
    }
