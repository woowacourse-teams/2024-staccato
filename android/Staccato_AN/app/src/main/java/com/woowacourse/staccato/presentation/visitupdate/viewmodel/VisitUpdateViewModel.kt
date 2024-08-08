package com.woowacourse.staccato.presentation.visitupdate.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.repository.VisitRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toVisitUpdateDefaultUiModel
import com.woowacourse.staccato.presentation.util.convertExcretaFile
import com.woowacourse.staccato.presentation.visitcreation.model.VisitTravelUiModel
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateDefaultUiModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class VisitUpdateViewModel(
    private val visitRepository: VisitRepository,
) : ViewModel() {
    private val _placeName = MutableLiveData<String>()
    val placeName: LiveData<String> get() = _placeName

    private val _existVisitImageUrls = MutableLiveData<List<String>>()
    private val existVisitImageUrls: LiveData<List<String>> get() = _existVisitImageUrls

    private val _newVisitImageUrls = MutableLiveData<Array<Uri>>()
    val newVisitImageUrls: LiveData<Array<Uri>> get() = _newVisitImageUrls

    private val _visitUpdateDefault = MutableLiveData<VisitUpdateDefaultUiModel>()
    val visitUpdateDefault: LiveData<VisitUpdateDefaultUiModel> get() = _visitUpdateDefault

    private val _travel = MutableLiveData<VisitTravelUiModel>()
    val travel: LiveData<VisitTravelUiModel> get() = _travel

    private val _isError = MutableSingleLiveData(false)
    val isError: SingleLiveData<Boolean> get() = _isError

    private val _isUpdateCompleted = MutableLiveData(false)
    val isUpdateCompleted: LiveData<Boolean> get() = _isUpdateCompleted

    fun initViewModelData(
        visitId: Long,
        travelId: Long,
        travelTitle: String,
    ) {
        fetchVisitData(visitId)
        initTravelData(travelId, travelTitle)
    }

    private fun fetchVisitData(visitId: Long) {
        viewModelScope.launch {
            visitRepository.getVisit(visitId = visitId)
                .onSuccess { visit ->
                    _visitUpdateDefault.value = visit.toVisitUpdateDefaultUiModel()
                    _existVisitImageUrls.value = visit.visitImageUrls
                    _placeName.value = visit.placeName
                }.onFailure {
                    _isError.postValue(true)
                }
        }
    }

    private fun initTravelData(
        travelId: Long,
        travelTitle: String,
    ) {
        _travel.value =
            VisitTravelUiModel(
                id = travelId,
                title = travelTitle,
            )
    }

    suspend fun updateVisit(context: Context) {
        if (placeName.value != null && visitUpdateDefault.value != null) {
            visitRepository.updateVisit(
                visitId = visitUpdateDefault.value!!.id,
                placeName = placeName.value!!,
                visitImageUrls = existVisitImageUrls.value ?: emptyList(),
                visitImageMultiParts = convertUrisToMultiParts(context),
            ).onSuccess {
                _isUpdateCompleted.postValue(true)
            }.onFailure {
                _isUpdateCompleted.postValue(false)
            }
        }
    }

    private fun convertUrisToMultiParts(context: Context): List<MultipartBody.Part> =
        newVisitImageUrls.value?.map { uri ->
            convertExcretaFile(context = context, uri = uri, name = "visitImageFiles")
        } ?: emptyList()

    fun setImageUris(uris: Array<Uri>) {
        _newVisitImageUrls.value = uris
    }
}
