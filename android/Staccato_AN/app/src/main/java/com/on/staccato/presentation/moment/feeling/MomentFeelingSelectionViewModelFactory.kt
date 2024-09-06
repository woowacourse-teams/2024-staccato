package com.on.staccato.presentation.moment.feeling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.on.staccato.data.StaccatoClient.momentApiService
import com.on.staccato.data.moment.MomentDefaultRepository
import com.on.staccato.data.moment.MomentRemoteDataSource

class MomentFeelingSelectionViewModelFactory(private val momentId: Long) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MomentFeelingSelectionViewModel::class.java)) {
            return MomentFeelingSelectionViewModel(
                momentId,
                MomentDefaultRepository(
                    remoteDataSource = MomentRemoteDataSource(momentApiService),
                ),
            ) as T
        }
        throw IllegalArgumentException()
    }
}
