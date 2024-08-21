package com.woowacourse.staccato.presentation.moment.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MomentDetailViewModel : ViewModel() {
    private val _momentDetail = MutableLiveData<MomentDetailUiModel>()
    val momentDetail: LiveData<MomentDetailUiModel> get() = _momentDetail

    fun setMomentDetail(momentDetail: MomentDetailUiModel) {
        _momentDetail.value = momentDetail
    }
}
