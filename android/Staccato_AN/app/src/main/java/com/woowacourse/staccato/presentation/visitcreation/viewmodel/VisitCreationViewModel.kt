package com.woowacourse.staccato.presentation.visitcreation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.repository.VisitRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SelectedPhotoHandler
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.util.convertExcretaFile
import com.woowacourse.staccato.presentation.visitcreation.model.VisitTravelUiModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.time.LocalDateTime

class VisitCreationViewModel(
    private val visitRepository: VisitRepository,
) : SelectedPhotoHandler, ViewModel() {
    val placeName = ObservableField<String>()

    private val _address = MutableLiveData<String>("서울특별시 강남구 테헤란로 411")
    val address: LiveData<String> get() = _address

    private val _travel = MutableLiveData<VisitTravelUiModel>()
    val travel: LiveData<VisitTravelUiModel> get() = _travel

    private val _selectedImages = MutableLiveData<Array<Uri>>()
    val selectedImages: LiveData<Array<Uri>> get() = _selectedImages

    private val _latitude = MutableLiveData<String>("32.123456")
    private val latitude: LiveData<String> get() = _latitude

    private val _longitude = MutableLiveData<String>("32.123456")
    private val longitude: LiveData<String> get() = _longitude

    val nowDateTime: LocalDateTime = LocalDateTime.now()

    private val _createdVisitId = MutableSingleLiveData<Long>()
    val createdVisitId: SingleLiveData<Long> get() = _createdVisitId

    private val _errorMessage = MutableSingleLiveData<String>()
    val errorMessage: SingleLiveData<String> get() = _errorMessage

    fun initTravelInfo(
        travelId: Long,
        travelTitle: String,
    ) {
        _travel.value =
            VisitTravelUiModel(
                id = travelId,
                title = travelTitle,
            )
    }

    fun createVisit(
        travelId: Long,
        context: Context,
    ) = viewModelScope.launch {
        visitRepository.createVisit(
            travelId = travelId,
            placeName = placeName.get() ?: "",
            latitude = latitude.value ?: "",
            longitude = longitude.value ?: "",
            address = address.value ?: "",
            visitedAt = nowDateTime,
            visitImageMultiParts = convertUrisToMultiParts(context),
        ).onSuccess { response ->
            _createdVisitId.postValue(response.visitId)
        }.onFailure {
            _errorMessage.postValue(it.message ?: "방문을 생성할 수 없어요!")
        }
    }

    private fun convertUrisToMultiParts(context: Context): List<MultipartBody.Part> {
        return selectedImages.value?.map { uri ->
            convertExcretaFile(context = context, uri = uri, name = "visitImageFiles")
        } ?: emptyList()
    }

    fun setImageUris(uris: Array<Uri>) {
        _selectedImages.value = uris
    }

    override fun onDeleteClicked(deletedUri: Uri) {
        _selectedImages.value =
            _selectedImages.value?.toMutableList()?.filterNot { it == deletedUri }?.toTypedArray()
                ?: emptyArray()
    }
}
