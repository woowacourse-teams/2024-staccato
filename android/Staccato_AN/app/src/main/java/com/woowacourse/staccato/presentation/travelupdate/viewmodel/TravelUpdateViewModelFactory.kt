package com.woowacourse.staccato.presentation.travelupdate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TravelUpdateViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelUpdateViewModel::class.java)) {
            return TravelUpdateViewModel() as T
        }
        throw IllegalArgumentException()
    }
}
