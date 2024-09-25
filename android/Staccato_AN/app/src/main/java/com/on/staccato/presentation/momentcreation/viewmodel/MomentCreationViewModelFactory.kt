package com.on.staccato.presentation.momentcreation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.on.staccato.data.StaccatoClient.imageApiService
import com.on.staccato.data.StaccatoClient.momentApiService
import com.on.staccato.data.StaccatoClient.timelineService
import com.on.staccato.data.image.ImageDefaultRepository
import com.on.staccato.data.moment.MomentDefaultRepository
import com.on.staccato.data.moment.MomentRemoteDataSource
import com.on.staccato.data.timeline.TimelineDefaultRepository
import com.on.staccato.data.timeline.TimelineRemoteDataSource

class MomentCreationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MomentCreationViewModel::class.java)) {
            return MomentCreationViewModel(
                TimelineDefaultRepository(
                    timelineDataSource = TimelineRemoteDataSource(timelineService),
                ),
                MomentDefaultRepository(
                    remoteDataSource = MomentRemoteDataSource(momentApiService),
                ),
                ImageDefaultRepository(
                    imageApiService = imageApiService,
                ),
            ) as T
        }
        throw IllegalArgumentException()
    }
}
