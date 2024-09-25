package com.on.staccato.presentation.visitupdate.viewmodel

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
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.MomentRepository
import com.on.staccato.presentation.common.AttachedPhotoHandler
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.momentcreation.model.AttachedPhotoUiModel
import com.on.staccato.presentation.momentcreation.model.AttachedPhotosUiModel
import com.on.staccato.presentation.momentcreation.model.AttachedPhotosUiModel.Companion.createPhotosByUrls
import com.on.staccato.presentation.momentcreation.viewmodel.MomentCreationViewModel
import com.on.staccato.presentation.util.convertExcretaFile
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class VisitUpdateViewModel(
    private val momentRepository: MomentRepository,
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

    private val _selectedVisitedAt = MutableLiveData<LocalDateTime?>(LocalDateTime.now())
    val selectedVisitedAt: LiveData<LocalDateTime?> get() = _selectedVisitedAt

    private val _memory = MutableLiveData<MemoryCandidate>()
    val memory: LiveData<MemoryCandidate> get() = _memory

    private val _isUpdateCompleted = MutableLiveData(false)
    val isUpdateCompleted: LiveData<Boolean> get() = _isUpdateCompleted

    private val _errorMessage = MutableSingleLiveData<String>()
    val errorMessage: SingleLiveData<String> get() = _errorMessage

    private val _isPosting = MutableLiveData<Boolean>(false)
    val isPosting: LiveData<Boolean> get() = _isPosting

    private val _isAddPhotoClicked = MutableSingleLiveData(false)
    val isAddPhotoClicked: SingleLiveData<Boolean> get() = _isAddPhotoClicked

    private val photoJobs = mutableMapOf<String, Job>()

    override fun onAddClicked() {
        if ((currentPhotos.value?.size ?: 0) == MomentCreationViewModel.MAX_PHOTO_NUMBER) {
            _errorMessage.postValue(MomentCreationViewModel.MAX_PHOTO_NUMBER_MESSAGE)
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

    fun initViewModelData(
        staccatoId: Long,
        memoryId: Long,
        memoryTitle: String,
    ) {
        fetchStaccato(staccatoId)
        initMemoryData(memoryId, memoryTitle)
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

    fun updateVisit(staccatoId: Long) {
        viewModelScope.launch {
            _isPosting.value = true
            val staccatoTitleValue = staccatoTitle.get() ?: return@launch handleFailure()
            val placeNameValue = placeName.value ?: return@launch handleFailure()
            val addressValue = address.value ?: return@launch handleFailure()
            val latitudeValue = latitude.value ?: 0.0
            val longitudeValue = longitude.value ?: 0.0
            val visitedAtValue = selectedVisitedAt.value ?: return@launch handleFailure()
            val memoryIdValue = memory.value?.memoryId ?: return@launch handleFailure()
            val momentImageUrlsValue =
                currentPhotos.value?.attachedPhotos?.map { it.imageUrl!! }
                    ?: return@launch handleFailure()

            momentRepository.updateMoment(
                momentId = staccatoId,
                staccatoTitle = staccatoTitleValue,
                placeName = placeNameValue,
                address = addressValue,
                latitude = latitudeValue,
                longitude = longitudeValue,
                visitedAt = visitedAtValue,
                memoryId = memoryIdValue,
                momentImageUrls = momentImageUrlsValue,
            ).onSuccess {
                _isUpdateCompleted.postValue(true)
            }.onFailure { e ->
                handleError(e.message)
            }
        }
    }

    private fun fetchStaccato(staccatoId: Long) {
        viewModelScope.launch {
            momentRepository.getMoment(momentId = staccatoId)
                .onSuccess { staccato ->
                    staccatoTitle.set(staccato.staccatoTitle)
                    _address.value = staccato.address
                    _selectedVisitedAt.value = staccato.visitedAt
                    _memory.value =
                        memory.value?.copy(
                            memoryId = staccato.memoryId,
                            memoryTitle = staccato.memoryTitle,
                        )
                    _placeName.value = staccato.placeName
                    _currentPhotos.value = createPhotosByUrls(staccato.momentImageUrls)
                }.onFailure { e ->
                    _errorMessage.postValue(e.message ?: "예외!!!!!")
                }
        }
    }

    private fun initMemoryData(
        memoryId: Long,
        memoryTitle: String,
    ) {
        _memory.value =
            MemoryCandidate(
                memoryId = memoryId,
                memoryTitle = memoryTitle,
            )
    }

    private fun createPhotoUploadJob(
        context: Context,
        photo: AttachedPhotoUiModel,
    ) = viewModelScope.async(buildCoroutineExceptionHandler()) {
        val multiPartBody =
            convertExcretaFile(
                context,
                photo.uri,
                MomentCreationViewModel.FORM_DATA_NAME,
            )
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

    private fun handleFailure() {
        _isPosting.value = false
    }

    private fun handleError(errorMessage: String?) {
        _isPosting.value = false
        _errorMessage.postValue(errorMessage ?: "야호 예외다잉")
    }
}
