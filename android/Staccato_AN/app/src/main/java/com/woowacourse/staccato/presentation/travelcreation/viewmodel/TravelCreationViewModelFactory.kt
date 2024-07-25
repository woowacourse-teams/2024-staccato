package com.woowacourse.staccato.presentation.travelcreation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TravelCreationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelCreationViewModel::class.java)) {
            return TravelCreationViewModel() as T
        }
        throw IllegalArgumentException()
    }
}
