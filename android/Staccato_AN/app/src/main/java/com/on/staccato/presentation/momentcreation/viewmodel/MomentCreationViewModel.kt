package com.on.staccato.presentation.momentcreation.viewmodel

import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.image.ImageDefaultRepository
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.repository.MomentRepository
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MomentCreationViewModel
    @Inject
    constructor(
        private val timelineRepository: TimelineRepository,
        private val momentRepository: MomentRepository,
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

        val memoryAndVisitedAt =
            MediatorLiveData<Triple<MemoryCandidates, MemoryCandidate, LocalDateTime?>>().apply {
                var latestMemoryCandidates: MemoryCandidates? = null
                var latestSelectedMemory: MemoryCandidate? = null
                var latestVisitedAt: LocalDateTime? = null

                addSource(memoryCandidates) { memories ->
                    latestMemoryCandidates = memories
                    if (latestMemoryCandidates != null && latestSelectedMemory != null && latestVisitedAt != null) {
                        value =
                            Triple(latestMemoryCandidates!!, latestSelectedMemory!!, latestVisitedAt!!)
                    }
                }

                addSource(selectedMemory) { selectedMemory ->
                    latestSelectedMemory = selectedMemory
                    if (latestMemoryCandidates != null && latestSelectedMemory != null && latestVisitedAt != null) {
                        value =
                            Triple(latestMemoryCandidates!!, latestSelectedMemory!!, latestVisitedAt!!)
                    }
                }

                addSource(selectedVisitedAt) { visitedAt ->
                    latestVisitedAt = visitedAt
                    if (latestMemoryCandidates != null && latestSelectedMemory != null && latestVisitedAt != null) {
                        value =
                            Triple(latestMemoryCandidates!!, latestSelectedMemory!!, latestVisitedAt!!)
                    }
                }
            }

        private val _createdStaccatoId = MutableSingleLiveData<Long>()
        val createdStaccatoId: SingleLiveData<Long> get() = _createdStaccatoId

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _isPosting = MutableLiveData<Boolean>(false)
        val isPosting: LiveData<Boolean> get() = _isPosting

        private val _isAddPhotoClicked = MutableSingleLiveData(false)
        val isAddPhotoClicked: SingleLiveData<Boolean> get() = _isAddPhotoClicked

        private val photoJobs = mutableMapOf<String, Job>()

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

        fun selectMemoryVisitedAt(
            memory: MemoryCandidate,
            visitedAt: LocalDateTime,
        ) {
            _selectedMemory.value = memory
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

        fun setPlaceByCurrentAddress(
            address: String?,
            location: Location,
        ) {
            _placeName.postValue(address)
            _address.postValue(address)
            _latitude.postValue(location.latitude)
            _longitude.postValue(location.longitude)
        }

        fun fetchMemoryCandidates(memoryId: Long) {
            viewModelScope.launch {
                timelineRepository.getMemoryCandidates()
                    .onSuccess { memoryCandidates ->
                        _memoryCandidates.value = memoryCandidates
                        if (memoryCandidates.memoryCandidate.isNotEmpty()) {
                            if (memoryId == 0L) {
                                _selectedMemory.value = memoryCandidates.memoryCandidate[0]
                                _selectedVisitedAt.value =
                                    memoryCandidates.memoryCandidate[0].endAt?.atStartOfDay()
                                        ?: LocalDateTime.now()
                                Log.d(
                                    "ㅌㅅㅌ 생성 뷰모델",
                                    "메인에서 selectedMemory = ${selectedMemory.value} 설정 완",
                                )
                            } else {
                                _selectedMemory.value =
                                    memoryCandidates.memoryCandidate.first { memory ->
                                        memoryId == memory.memoryId
                                    }
                                _selectedVisitedAt.value =
                                    selectedMemory.value?.endAt?.atStartOfDay() ?: LocalDateTime.now()
                                Log.d(
                                    "ㅌㅅㅌ 생성 뷰모델",
                                    "추억에서 selectedMemory = ${selectedMemory.value} 설정 완",
                                )
                            }
                        }
                    }
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

        fun updateSelectedVisitedAt(newSelectedVisitedAt: LocalDateTime) {
            _selectedVisitedAt.value = newSelectedVisitedAt
        }

        fun createMoment() =
            viewModelScope.launch {
                _isPosting.value = true
                momentRepository.createMoment(
                    memoryId = selectedMemory.value!!.memoryId,
                    staccatoTitle = staccatoTitle.get() ?: return@launch,
                    placeName = placeName.value ?: return@launch,
                    latitude = latitude.value ?: return@launch,
                    longitude = longitude.value ?: return@launch,
                    address = address.value ?: return@launch,
                    visitedAt = selectedVisitedAt.value ?: return@launch,
                    momentImageUrls = currentPhotos.value!!.attachedPhotos.map { it.imageUrl!! },
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
        ) = viewModelScope.async(buildCoroutineExceptionHandler()) {
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
