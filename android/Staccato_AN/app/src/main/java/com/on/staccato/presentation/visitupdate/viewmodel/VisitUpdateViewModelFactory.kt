package com.on.staccato.presentation.visitupdate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.on.staccato.data.StaccatoClient
import com.on.staccato.data.image.ImageDefaultRepository
import com.on.staccato.data.moment.MomentDefaultRepository
import com.on.staccato.data.moment.MomentRemoteDataSource
import com.on.staccato.data.timeline.TimelineDefaultRepository
import com.on.staccato.data.timeline.TimelineRemoteDataSource

class VisitUpdateViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitUpdateViewModel::class.java)) {
            return VisitUpdateViewModel(
                TimelineDefaultRepository(
                    timelineDataSource = TimelineRemoteDataSource(StaccatoClient.timelineService),
                ),
                MomentDefaultRepository(
                    remoteDataSource = MomentRemoteDataSource(StaccatoClient.momentApiService),
                ),
                ImageDefaultRepository(
                    imageApiService = StaccatoClient.imageApiService,
                ),
            ) as T
        }
        throw IllegalArgumentException()
    }
}
