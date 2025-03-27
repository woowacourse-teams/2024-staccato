package com.woowacourse.staccato.presentation.visitupdate.viewmodel

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.repository.MomentRepository
import com.woowacourse.staccato.presentation.common.MutableSingleLiveData
import com.woowacourse.staccato.presentation.common.SingleLiveData
import com.woowacourse.staccato.presentation.mapper.toVisitUpdateDefaultUiModel
import com.woowacourse.staccato.presentation.momentcreation.model.MomentMemoryUiModel
import com.woowacourse.staccato.presentation.util.convertExcretaFile
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateDefaultUiModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class VisitUpdateViewModel(
    private val momentRepository: MomentRepository,
) : ViewModel() {
    val placeName = ObservableField<String>()

    private val _existVisitImageUrls = MutableLiveData<List<String>>()
    private val existVisitImageUrls: LiveData<List<String>> get() = _existVisitImageUrls

    private val _newVisitImageUrls = MutableLiveData<Array<Uri>>()
    val newVisitImageUrls: LiveData<Array<Uri>> get() = _newVisitImageUrls

    private val _visitUpdateDefault = MutableLiveData<VisitUpdateDefaultUiModel>()
    val visitUpdateDefault: LiveData<VisitUpdateDefaultUiModel> get() = _visitUpdateDefault

    private val _memory = MutableLiveData<MomentMemoryUiModel>()
    val memory: LiveData<MomentMemoryUiModel> get() = _memory

    private val _isError = MutableSingleLiveData<Boolean>()
    val isError: SingleLiveData<Boolean> get() = _isError

    private val _isUpdateCompleted = MutableLiveData(false)
    val isUpdateCompleted: LiveData<Boolean> get() = _isUpdateCompleted

    private val _isPosting = MutableLiveData<Boolean>(false)
    val isPosting: LiveData<Boolean> get() = _isPosting

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
                    _existVisitImageUrls.value = visit.momentImageUrls
                    placeName.set(visit.placeName)
                }.onFailure {
                    _isError.postValue(true)
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

    suspend fun updateVisit(context: Context) {
        if (placeName.get() != null && visitUpdateDefault.value != null) {
            _isPosting.value = true
            momentRepository.updateMoment(
                momentId = visitUpdateDefault.value!!.id,
                placeName = placeName.get()!!,
                momentImageUrls = existVisitImageUrls.value ?: emptyList(),
                momentImageMultiParts = convertUrisToMultiParts(context),
            ).onSuccess {
                _isUpdateCompleted.postValue(true)
            }.onFailure {
                _isPosting.value = false
                _isError.postValue(true)
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
