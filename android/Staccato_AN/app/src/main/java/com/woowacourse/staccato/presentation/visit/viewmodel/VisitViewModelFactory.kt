package com.woowacourse.staccato.presentation.visit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.StaccatoClient.visitApiService
import com.woowacourse.staccato.data.visit.VisitDefaultRepository
import com.woowacourse.staccato.data.visit.VisitRemoteDataSource

class VisitViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitViewModel::class.java)) {
            return VisitViewModel(
                VisitDefaultRepository(
                    remoteDataSource = VisitRemoteDataSource(visitApiService),
                ),
            ) as T
        }
        throw IllegalArgumentException()
    }
}
