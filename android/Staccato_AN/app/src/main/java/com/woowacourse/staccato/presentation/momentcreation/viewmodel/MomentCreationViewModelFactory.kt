package com.woowacourse.staccato.presentation.momentcreation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.StaccatoClient.imageApiService
import com.woowacourse.staccato.data.StaccatoClient.momentApiService
import com.woowacourse.staccato.data.image.ImageDefaultRepository
import com.woowacourse.staccato.data.moment.MomentDefaultRepository
import com.woowacourse.staccato.data.moment.MomentRemoteDataSource

class MomentCreationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MomentCreationViewModel::class.java)) {
            return MomentCreationViewModel(
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
