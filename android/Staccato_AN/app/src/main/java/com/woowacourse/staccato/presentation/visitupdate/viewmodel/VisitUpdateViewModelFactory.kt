package com.woowacourse.staccato.presentation.visitupdate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.StaccatoClient
import com.woowacourse.staccato.data.image.ImageDefaultRepository
import com.woowacourse.staccato.data.moment.MomentDefaultRepository
import com.woowacourse.staccato.data.moment.MomentRemoteDataSource

class VisitUpdateViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitUpdateViewModel::class.java)) {
            return VisitUpdateViewModel(
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
