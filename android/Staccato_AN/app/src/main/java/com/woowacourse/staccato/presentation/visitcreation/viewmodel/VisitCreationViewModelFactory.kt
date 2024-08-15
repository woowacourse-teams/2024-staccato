package com.woowacourse.staccato.presentation.visitcreation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.StaccatoClient.momentApiService
import com.woowacourse.staccato.data.moment.MomentRemoteDataSource
import com.woowacourse.staccato.data.moment.VisitDefaultRepository

class VisitCreationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitCreationViewModel::class.java)) {
            return VisitCreationViewModel(
                VisitDefaultRepository(
                    remoteDataSource = MomentRemoteDataSource(momentApiService),
                ),
            ) as T
        }
        throw IllegalArgumentException()
    }
}
