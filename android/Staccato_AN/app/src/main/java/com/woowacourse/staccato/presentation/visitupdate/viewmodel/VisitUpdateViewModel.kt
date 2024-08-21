package com.woowacourse.staccato.presentation.visitupdate.viewmodel

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.data.ApiResponseHandler.onException
import com.woowacourse.staccato.data.ApiResponseHandler.onSuccess
import com.woowacourse.staccato.domain.repository.ImageRepository
import com.woowacourse.staccato.domain.repository.MomentRepository
import com.woowacourse.staccato.presentation.common.AttachedPhotoHandler
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toVisitUpdateDefaultUiModel
import com.woowacourse.staccato.presentation.momentcreation.model.AttachedPhotoUiModel
import com.woowacourse.staccato.presentation.momentcreation.model.AttachedPhotosUiModel
import com.woowacourse.staccato.presentation.momentcreation.model.AttachedPhotosUiModel.Companion.createPhotosByUrls
import com.woowacourse.staccato.presentation.momentcreation.model.MomentMemoryUiModel
import com.woowacourse.staccato.presentation.momentcreation.viewmodel.MomentCreationViewModel
import com.woowacourse.staccato.presentation.util.convertExcretaFile
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateDefaultUiModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class VisitUpdateViewModel(
    private val momentRepository: MomentRepository,
    private val imageRepository: ImageRepository,
) : AttachedPhotoHandler, ViewModel() {
    val title = ObservableField<String>()

    private val _currentPhotos =
        MutableLiveData<AttachedPhotosUiModel>(AttachedPhotosUiModel(emptyList()))
    val currentPhotos: LiveData<AttachedPhotosUiModel> get() = _currentPhotos

    private val _pendingPhotos = MutableSingleLiveData<List<AttachedPhotoUiModel>>()
    val pendingPhotos: SingleLiveData<List<AttachedPhotoUiModel>> get() = _pendingPhotos

    private val _visitUpdateDefault = MutableLiveData<VisitUpdateDefaultUiModel>()
    val visitUpdateDefault: LiveData<VisitUpdateDefaultUiModel> get() = _visitUpdateDefault

    private val _memory = MutableLiveData<MomentMemoryUiModel>()
    val memory: LiveData<MomentMemoryUiModel> get() = _memory

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
        visitId: Long,
        memoryId: Long,
        memoryTitle: String,
    ) {
        fetchVisitData(visitId)
        initMemoryData(memoryId, memoryTitle)
    }

    private fun fetchVisitData(visitId: Long) {
        viewModelScope.launch {
            momentRepository.getMoment(momentId = visitId)
                .onSuccess { visit ->
                    _visitUpdateDefault.value = visit.toVisitUpdateDefaultUiModel()
                    _currentPhotos.value = createPhotosByUrls(visit.momentImageUrls)
                    title.set(visit.placeName)
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
            MomentMemoryUiModel(
                id = memoryId,
                title = memoryTitle,
            )
    }

    fun updateVisit() {
        if (title.get() != null && visitUpdateDefault.value != null) {
            viewModelScope.launch {
                _isPosting.value = true
                momentRepository.updateMoment(
                    momentId = visitUpdateDefault.value!!.id,
                    placeName = title.get()!!,
                    momentImageUrls = currentPhotos.value!!.attachedPhotos.map { it.imageUrl!! },
                ).onSuccess {
                    _isUpdateCompleted.postValue(true)
                }.onFailure { e ->
                    _isPosting.value = false
                    _errorMessage.postValue(e.message ?: "야호 예외다잉")
                }
            }
        }
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

    fun updateSelectedImageUris(newUris: Array<Uri>) {
        val updatedPhotos = currentPhotos.value!!.addPhotosByUris(newUris.toList())
        _currentPhotos.value = updatedPhotos
        _pendingPhotos.postValue(updatedPhotos.getPhotosWithoutUrls())
    }

    fun setUrisWithNewOrder(list: List<AttachedPhotoUiModel>) {
        _currentPhotos.value = AttachedPhotosUiModel(list)
    }
}
