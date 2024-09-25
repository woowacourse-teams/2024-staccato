package com.on.staccato.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.on.staccato.domain.repository.MomentRepository

class MapsViewModelFactory(
    private val momentRepository: MomentRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(momentRepository) as T
        }
        throw IllegalArgumentException()
    }
}
