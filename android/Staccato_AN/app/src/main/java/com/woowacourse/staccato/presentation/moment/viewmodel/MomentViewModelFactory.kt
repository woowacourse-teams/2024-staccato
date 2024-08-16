package com.woowacourse.staccato.presentation.moment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.StaccatoClient.momentApiService
import com.woowacourse.staccato.data.moment.MomentDefaultRepository
import com.woowacourse.staccato.data.moment.MomentRemoteDataSource

class MomentViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MomentViewModel::class.java)) {
            return MomentViewModel(
                MomentDefaultRepository(
                    remoteDataSource = MomentRemoteDataSource(momentApiService),
                ),
            ) as T
        }
        throw IllegalArgumentException()
    }
}
