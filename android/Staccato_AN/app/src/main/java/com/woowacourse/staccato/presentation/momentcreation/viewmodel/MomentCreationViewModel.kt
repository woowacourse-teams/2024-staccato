package com.woowacourse.staccato.presentation.momentcreation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.image.ImageResponse
import com.woowacourse.staccato.data.image.ImageDefaultRepository
import com.woowacourse.staccato.domain.repository.MomentRepository
import com.woowacourse.staccato.presentation.common.AttachedPhotoHandler
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.momentcreation.model.AttachedPhotoUiModel
import com.woowacourse.staccato.presentation.momentcreation.model.AttachedPhotoUiModels
import com.woowacourse.staccato.presentation.momentcreation.model.MomentMemoryUiModel
import com.woowacourse.staccato.presentation.util.convertExcretaFile
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MomentCreationViewModel(
    private val momentRepository: MomentRepository,
    private val imageDefaultRepository: ImageDefaultRepository,
) : AttachedPhotoHandler, ViewModel() {
    val placeName = ObservableField<String>()

    private val _address = MutableLiveData<String>("서울특별시 강남구 테헤란로 411")
    val address: LiveData<String> get() = _address

    private val _currentPhotos =
        MutableLiveData<AttachedPhotoUiModels>(AttachedPhotoUiModels(emptyList()))
    val currentPhotos: LiveData<AttachedPhotoUiModels> get() = _currentPhotos

    private val _pendingPhotos = MutableSingleLiveData<List<AttachedPhotoUiModel>>()
    val pendingPhotos: SingleLiveData<List<AttachedPhotoUiModel>> get() = _pendingPhotos

    private val _memory = MutableLiveData<MomentMemoryUiModel>()
    val memory: LiveData<MomentMemoryUiModel> get() = _memory

    private val _latitude = MutableLiveData<String>("32.123456")
    private val latitude: LiveData<String> get() = _latitude

    private val _longitude = MutableLiveData<String>("32.123456")
    private val longitude: LiveData<String> get() = _longitude

    val nowDateTime: LocalDateTime = LocalDateTime.now()

    private val _createdMomentId = MutableSingleLiveData<Long>()

    val createdMomentId: SingleLiveData<Long> get() = _createdMomentId

    private val _errorMessage = MutableSingleLiveData<String>()

    val errorMessage: SingleLiveData<String> get() = _errorMessage

    private val _isPosting = MutableLiveData<Boolean>(false)

    val isPosting: LiveData<Boolean> get() = _isPosting

    private val _isAddPhotoClicked = MutableSingleLiveData(false)
    val isAddPhotoClicked: SingleLiveData<Boolean> get() = _isAddPhotoClicked

    override fun onAddClicked() {
        if ((currentPhotos.value?.size ?: 0) == MAX_PHOTO_NUMBER) {
            _errorMessage.postValue(MAX_PHOTO_NUMBER_MESSAGE)
        } else {
            _isAddPhotoClicked.postValue(true)
        }
    }

    override fun onDeleteClicked(deletedPhoto: AttachedPhotoUiModel) {
        _currentPhotos.value = currentPhotos.value?.removePhoto(deletedPhoto)
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
        _currentPhotos.value = AttachedPhotoUiModels(list)
    }

    fun fetchPhotosUrlsByUris(context: Context) {
        pendingPhotos.getValue()?.forEach { photo ->
            viewModelScope.launch {
                val multiPartBody = convertExcretaFile(context, photo.uri, FORM_DATA_NAME)
                val result = imageDefaultRepository.convertImageFileToUrl(multiPartBody)
                handleConvertResult(result, photo)
            }
        }
    }

    private suspend fun handleConvertResult(
        result: ResponseResult<ImageResponse>,
        targetPhoto: AttachedPhotoUiModel,
    ) {
        result.onSuccess {
            updatePhotoWithUrl(targetPhoto, it.imageUrl)
        }.onException { e, message ->
            Log.d("ㅌㅅㅌ 실패", "${e.message} | $message")
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
                placeName = placeName.get() ?: "",
                latitude = latitude.value ?: "",
                longitude = longitude.value ?: "",
                address = address.value ?: "",
                visitedAt = nowDateTime,
                momentImageUrls = currentPhotos.value!!.attachedPhotos.map { it.imageUrl!! },
            ).onSuccess { response ->
                _createdMomentId.postValue(response.momentId)
            }.onFailure {
                _isPosting.value = false
                _errorMessage.postValue(it.message ?: "방문을 생성할 수 없어요!")
            }
        }

    companion object {
        private const val MAX_PHOTO_NUMBER = 5
        const val MAX_PHOTO_NUMBER_MESSAGE = "사진은 최대 ${MAX_PHOTO_NUMBER}장만 첨부할 수 있어요!"
        const val FORM_DATA_NAME = "imageFile"
    }
}
