package com.woowacourse.staccato.presentation.momentcreation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.data.image.ImageDefaultRepository
import com.woowacourse.staccato.domain.repository.MomentRepository
import com.woowacourse.staccato.presentation.common.AttachedPhotoHandler
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.momentcreation.model.AttachedPhotoUiModel
import com.woowacourse.staccato.presentation.momentcreation.model.AttachedPhotosUiModel
import com.woowacourse.staccato.presentation.momentcreation.model.MomentMemoryUiModel
import com.woowacourse.staccato.presentation.util.convertExcretaFile
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MomentCreationViewModel(
    private val momentRepository: MomentRepository,
    private val imageRepository: ImageDefaultRepository,
) : AttachedPhotoHandler, ViewModel() {
    val title = ObservableField<String>()

    private val _address = MutableLiveData<String>("서울특별시 강남구 테헤란로 411")
    val address: LiveData<String> get() = _address

    private val _currentPhotos =
        MutableLiveData<AttachedPhotosUiModel>(AttachedPhotosUiModel(emptyList()))
    val currentPhotos: LiveData<AttachedPhotosUiModel> get() = _currentPhotos

    private val _pendingPhotos = MutableSingleLiveData<List<AttachedPhotoUiModel>>()
    val pendingPhotos: SingleLiveData<List<AttachedPhotoUiModel>> get() = _pendingPhotos

    private val _memory = MutableLiveData<MomentMemoryUiModel>()
    val memory: LiveData<MomentMemoryUiModel> get() = _memory

    private val _latitude = MutableLiveData("32.123456")
    private val latitude: LiveData<String> get() = _latitude

    private val _longitude = MutableLiveData<String>("32.123456")
    private val longitude: LiveData<String> get() = _longitude

    val nowDateTime = MutableLiveData<LocalDateTime>(LocalDateTime.now())

    private val _createdMomentId = MutableSingleLiveData<Long>()
    val createdMomentId: SingleLiveData<Long> get() = _createdMomentId

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

    fun initMemoryInfo(
        memoryId: Long,
        memoryTitle: String,
    ) {
        _memory.value =
            MomentMemoryUiModel(
                id = memoryId,
                title = memoryTitle,
            )
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

    fun createMoment(memoryId: Long) =
        viewModelScope.launch {
            _isPosting.value = true
            momentRepository.createMoment(
                memoryId = memoryId,
                placeName = title.get() ?: "",
                latitude = latitude.value ?: "",
                longitude = longitude.value ?: "",
                address = address.value ?: "",
                visitedAt = nowDateTime.value ?: LocalDateTime.now(),
                momentImageUrls = currentPhotos.value!!.attachedPhotos.map { it.imageUrl!! },
            ).onSuccess { response ->
                _createdMomentId.postValue(response.momentId)
            }.onFailure {
                _isPosting.value = false
                _errorMessage.postValue(it.message ?: "방문을 생성할 수 없어요!")
            }
        }

    companion object {
        const val MAX_PHOTO_NUMBER = 5
        const val MAX_PHOTO_NUMBER_MESSAGE = "사진은 최대 ${MAX_PHOTO_NUMBER}장만 첨부할 수 있어요!"
        const val FORM_DATA_NAME = "imageFile"
    }
}