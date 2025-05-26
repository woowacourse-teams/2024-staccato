package com.on.staccato.presentation.invitation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.network.onException2
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.repository.InvitationRepository
import com.on.staccato.presentation.invitation.model.ReceivedInvitationUiModel
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel
import com.on.staccato.presentation.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel
    @Inject
    constructor(
        private val invitationRepository: InvitationRepository
    ) : ViewModel() {
        private val _receivedInvitations: MutableStateFlow<List<ReceivedInvitationUiModel>> = MutableStateFlow((emptyList()))
        val receivedInvitations: StateFlow<List<ReceivedInvitationUiModel>> = _receivedInvitations.asStateFlow()

        private val _sentInvitations: MutableStateFlow<List<SentInvitationUiModel>> = MutableStateFlow((emptyList()))
        val sentInvitations: StateFlow<List<SentInvitationUiModel>> = _sentInvitations.asStateFlow()

        init {
            getReceivedInvitations()
        }

        fun getReceivedInvitations() {
            viewModelScope.launch {
                val result = invitationRepository.getReceivedInvitations()
                result.onSuccess { invitations ->
                    _receivedInvitations.value = invitations.map { it.toUiModel() }
                }.onServerError {
                }.onException2 {
                }
            }
        }

        fun getSentInvitations() {
            viewModelScope.launch {
                val result = invitationRepository.getSentInvitations()
                result.onSuccess { invitations ->
                    _sentInvitations.value = invitations.map { it.toUiModel() }
                }.onServerError {
                }.onException2 {
                }
            }
        }
    }
