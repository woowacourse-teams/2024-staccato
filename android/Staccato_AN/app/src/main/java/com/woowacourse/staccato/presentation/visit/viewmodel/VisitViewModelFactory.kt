package com.woowacourse.staccato.presentation.visit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.staccato.data.StaccatoClient.visitApiService
import com.woowacourse.staccato.data.repository.VisitDefaultRepository
import com.woowacourse.staccato.data.visit.RemoteVisitDataSource

class VisitViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitViewModel::class.java)) {
            return VisitViewModel(
                VisitDefaultRepository(
                    remoteDataSource = RemoteVisitDataSource(visitApiService),
                ),
            ) as T
        }
        throw IllegalArgumentException()
    }
}
