package com.woowacourse.staccato.presentation.visitupdate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.StaccatoClient
import com.woowacourse.staccato.data.moment.MomentRemoteDataSource
import com.woowacourse.staccato.data.moment.VisitDefaultRepository

class VisitUpdateViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitUpdateViewModel::class.java)) {
            return VisitUpdateViewModel(
                VisitDefaultRepository(
                    remoteDataSource = MomentRemoteDataSource(StaccatoClient.momentApiService),
                ),
            ) as T
        }
        throw IllegalArgumentException()
    }
}
