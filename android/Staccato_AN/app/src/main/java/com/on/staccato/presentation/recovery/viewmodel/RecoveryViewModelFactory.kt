package com.on.staccato.presentation.recovery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.on.staccato.data.member.MemberDefaultRepository
import com.on.staccato.domain.repository.MemberRepository

class RecoveryViewModelFactory(
    private val repository: MemberRepository = MemberDefaultRepository(),
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecoveryViewModel::class.java)) {
            return RecoveryViewModel(repository) as T
        } else {
            throw IllegalArgumentException("확인되지 않은 ViewModel 클래스입니다.")
        }
    }
}
