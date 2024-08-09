package com.woowacourse.staccato.presentation.visitcreation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.StaccatoClient.visitApiService
import com.woowacourse.staccato.data.visit.VisitDefaultRepository
import com.woowacourse.staccato.data.visit.VisitRemoteDataSource

class VisitCreationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitCreationViewModel::class.java)) {
            return VisitCreationViewModel(
                VisitDefaultRepository(
                    remoteDataSource = VisitRemoteDataSource(visitApiService),
                ),
            ) as T
        }
        throw IllegalArgumentException()
    }
}
