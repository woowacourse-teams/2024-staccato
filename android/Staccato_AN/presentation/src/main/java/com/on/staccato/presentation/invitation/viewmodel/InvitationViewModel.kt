package com.on.staccato.presentation.invitation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.model.invitation.ReceivedInvitation
import com.on.staccato.domain.model.invitation.SentInvitation
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.InvitationRepository
import com.on.staccato.presentation.R
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.invitation.model.InvitationDialogState
import com.on.staccato.presentation.invitation.model.InvitationDialogState.Cancel
import com.on.staccato.presentation.invitation.model.InvitationDialogState.None
import com.on.staccato.presentation.invitation.model.InvitationDialogState.Reject
import com.on.staccato.presentation.invitation.model.ReceivedInvitationUiModel
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel
import com.on.staccato.presentation.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel
    @Inject
    constructor(
        private val invitationRepository: InvitationRepository,
    ) : ViewModel() {
        private val _receivedInvitations: MutableStateFlow<List<ReceivedInvitationUiModel>> = MutableStateFlow(emptyList())
        val receivedInvitations: StateFlow<List<ReceivedInvitationUiModel>> = _receivedInvitations.asStateFlow()

        private val _sentInvitations: MutableStateFlow<List<SentInvitationUiModel>> = MutableStateFlow(emptyList())
        val sentInvitations: StateFlow<List<SentInvitationUiModel>> = _sentInvitations.asStateFlow()

        private val _hasInvitationAccepted: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)
        val hasInvitationAccepted: SharedFlow<Boolean> = _hasInvitationAccepted.asSharedFlow()

        private val _dialogState: MutableState<InvitationDialogState> = mutableStateOf(None)
        val dialogState: State<InvitationDialogState> = _dialogState

        private val _messageEvent = MutableSharedFlow<MessageEvent>()
        val messageEvent: SharedFlow<MessageEvent> get() = _messageEvent.asSharedFlow()

        init {
            fetchReceivedInvitations()
            fetchSentInvitations()
        }

        fun fetchReceivedInvitations() {
            viewModelScope.launch {
                val result = invitationRepository.getReceivedInvitations()
                result
                    .onSuccess(::updateReceivedInvitations)
                    .onServerError { changeMessageEvent(MessageEvent.from(message = it)) }
                    .onException { changeMessageEvent(MessageEvent.from(exceptionType = it)) }
            }
        }

        fun acceptInvitation(invitationId: Long) {
            viewModelScope.launch {
                val result = invitationRepository.acceptInvitation(invitationId)
                result
                    .onSuccess {
                        fetchReceivedInvitations()
                        updateHasInvitationAccepted()
                        _messageEvent.emit(MessageEvent.FromResource(R.string.invitation_management_accept_success))
                    }
                    .onServerError { changeMessageEvent(MessageEvent.from(message = it)) }
                    .onException { changeMessageEvent(MessageEvent.from(exceptionType = it)) }
            }
        }

        fun showRejectDialog(invitationId: Long) {
            _dialogState.value =
                Reject(
                    invitationId = invitationId,
                    onConfirm = { rejectInvitation(invitationId) },
                )
        }

        fun fetchSentInvitations() {
            viewModelScope.launch {
                val result = invitationRepository.getSentInvitations()
                result
                    .onSuccess(::updateSentInvitations)
                    .onServerError { changeMessageEvent(MessageEvent.from(message = it)) }
                    .onException { changeMessageEvent(MessageEvent.from(exceptionType = it)) }
            }
        }

        fun showCancelDialog(invitationId: Long) {
            _dialogState.value =
                Cancel(
                    invitationId = invitationId,
                    onConfirm = { cancelInvitation(invitationId) },
                )
        }

        fun dismissDialog() {
            _dialogState.value = None
        }

        private fun updateReceivedInvitations(invitations: List<ReceivedInvitation>) {
            _receivedInvitations.value = invitations.map { it.toUiModel() }
        }

        private suspend fun updateHasInvitationAccepted() {
            _hasInvitationAccepted.emit(true)
        }

        private fun rejectInvitation(invitationId: Long) {
            dismissDialog()
            viewModelScope.launch {
                val result = invitationRepository.rejectInvitation(invitationId)
                result
                    .onSuccess { fetchReceivedInvitations() }
                    .onServerError { changeMessageEvent(MessageEvent.from(message = it)) }
                    .onException { changeMessageEvent(MessageEvent.from(exceptionType = it)) }
            }
        }

        private fun updateSentInvitations(invitations: List<SentInvitation>) {
            _sentInvitations.value = invitations.map { it.toUiModel() }
        }

        private fun cancelInvitation(invitationId: Long) {
            dismissDialog()
            viewModelScope.launch {
                val result = invitationRepository.cancelInvitation(invitationId)
                result
                    .onSuccess { fetchSentInvitations() }
                    .onServerError { changeMessageEvent(MessageEvent.from(message = it)) }
                    .onException { changeMessageEvent(MessageEvent.from(exceptionType = it)) }
            }
        }

        private suspend fun changeMessageEvent(messageEvent: MessageEvent) {
            _messageEvent.emit(messageEvent)
        }
    }
