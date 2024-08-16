package com.woowacourse.staccato.presentation.momentcreation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.repository.MomentRepository
import com.woowacourse.staccato.presentation.common.AttachedPhotoHandler
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.momentcreation.model.MomentMemoryUiModel
import com.woowacourse.staccato.presentation.util.convertExcretaFile
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.time.LocalDateTime

class VisitCreationViewModel(
    private val momentRepository: MomentRepository,
) : AttachedPhotoHandler, ViewModel() {
    val placeName = ObservableField<String>()

    private val _address = MutableLiveData<String>("서울특별시 강남구 테헤란로 411")
    val address: LiveData<String> get() = _address

    private val _memory = MutableLiveData<MomentMemoryUiModel>()
    val memory: LiveData<MomentMemoryUiModel> get() = _memory

    private val _selectedImages = MutableLiveData<Set<Uri>>()
    val selectedImages: LiveData<Set<Uri>> get() = _selectedImages

    private val _latitude = MutableLiveData<String>("32.123456")
    private val latitude: LiveData<String> get() = _latitude

    private val _longitude = MutableLiveData<String>("32.123456")
    private val longitude: LiveData<String> get() = _longitude

    val nowDateTime: LocalDateTime = LocalDateTime.now()

    private val _createdVisitId = MutableSingleLiveData<Long>()
    val createdVisitId: SingleLiveData<Long> get() = _createdVisitId

    private val _errorMessage = MutableSingleLiveData<String>()
    val errorMessage: SingleLiveData<String> get() = _errorMessage

    private val _isPosting = MutableLiveData<Boolean>(false)
    val isPosting: LiveData<Boolean> get() = _isPosting

    private val _isAddPhotoClicked = MutableSingleLiveData(false)
    val isAddPhotoClicked: SingleLiveData<Boolean> get() = _isAddPhotoClicked

    override fun onAddClicked() {
        if (selectedImages.value.orEmpty().size == MAX_PHOTO_NUMBER) {
            _errorMessage.postValue(MAX_PHOTO_NUMBER_MESSAGE)
        } else {
            _isAddPhotoClicked.postValue(true)
        }
    }

    override fun onDeleteClicked(deletedUri: Uri) {
        val currentUris = _selectedImages.value.orEmpty()
        if (currentUris.contains(deletedUri)) _selectedImages.value = currentUris - deletedUri
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
        val currentUris = selectedImages.value.orEmpty().toSet()
        val combinedUris = currentUris + newUris
        _selectedImages.value =
            if (combinedUris.size > MAX_PHOTO_NUMBER) {
                _errorMessage.postValue(MAX_PHOTO_NUMBER_MESSAGE)
                combinedUris.take(MAX_PHOTO_NUMBER).toSet()
            } else {
                combinedUris
            }
    }

    fun createVisit(
        memoryId: Long,
        context: Context,
    ) = viewModelScope.launch {
        _isPosting.value = true
        momentRepository.createMoment(
            memoryId = memoryId,
            placeName = placeName.get() ?: "",
            latitude = latitude.value ?: "",
            longitude = longitude.value ?: "",
            address = address.value ?: "",
            visitedAt = nowDateTime,
            momentImageMultiParts = convertUrisToMultiParts(context),
        ).onSuccess { response ->
            _createdVisitId.postValue(response.momentId)
        }.onFailure {
            _isPosting.value = false
            _errorMessage.postValue(it.message ?: "방문을 생성할 수 없어요!")
        }
    }

    private fun convertUrisToMultiParts(context: Context): List<MultipartBody.Part> {
        return selectedImages.value?.map { uri ->
            convertExcretaFile(context = context, uri = uri, name = FORM_DATA_NAME)
        } ?: emptyList()
    }

    companion object {
        private const val MAX_PHOTO_NUMBER = 5
        const val MAX_PHOTO_NUMBER_MESSAGE = "사진은 최대 ${MAX_PHOTO_NUMBER}장만 첨부할 수 있어요!"
        const val FORM_DATA_NAME = "momentImageFiles"
    }
}
