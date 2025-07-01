package com.on.staccato.presentation.category.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.ExceptionType
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.Participants
import com.on.staccato.domain.model.Role
import com.on.staccato.domain.model.emptyMembers
import com.on.staccato.domain.model.emptyParticipants
import com.on.staccato.domain.model.toMembers
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.InvitationRepository
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.presentation.category.invite.model.InviteState
import com.on.staccato.presentation.category.model.CategoryDialogState
import com.on.staccato.presentation.category.model.CategoryDialogState.Exit
import com.on.staccato.presentation.category.model.CategoryDialogState.None
import com.on.staccato.presentation.category.model.CategoryEvent
import com.on.staccato.presentation.category.model.CategoryEvent.Deleted
import com.on.staccato.presentation.category.model.CategoryEvent.Error
import com.on.staccato.presentation.category.model.CategoryEvent.Exited
import com.on.staccato.presentation.category.model.CategoryUiModel
import com.on.staccato.presentation.category.model.CategoryUiModel.Companion.DEFAULT_CATEGORY_ID
import com.on.staccato.presentation.category.model.defaultCategoryUiModel
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.common.event.MutableSingleLiveData
import com.on.staccato.presentation.common.event.SingleLiveData
import com.on.staccato.presentation.mapper.toUiModel
import com.on.staccato.toMessageId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel
    @Inject
    constructor(
        private val categoryRepository: CategoryRepository,
        private val memberRepository: MemberRepository,
        private val invitationRepository: InvitationRepository,
    ) : ViewModel() {
        var categoryId: Long = DEFAULT_CATEGORY_ID
            private set

        private val _category = MutableStateFlow<CategoryUiModel>(defaultCategoryUiModel)
        val category: StateFlow<CategoryUiModel> = _category.asStateFlow()

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent> get() = _messageEvent

        private val _isInviteMode = MutableStateFlow(false)
        val isInviteMode: StateFlow<Boolean> get() = _isInviteMode

        val participatingMembers = MutableStateFlow(emptyParticipants)

        private val searchedMembers = MutableStateFlow(emptyMembers)

        private val _selectedMembers = MutableStateFlow(emptyMembers)
        val selectedMembers = _selectedMembers.asStateFlow()

        private val _categoryEvent = MutableSharedFlow<CategoryEvent>()
        val categoryEvent: SharedFlow<CategoryEvent> = _categoryEvent.asSharedFlow()

        private val _dialogState = mutableStateOf<CategoryDialogState>(None)
        val dialogState: State<CategoryDialogState> = _dialogState

        val members =
            combine(
                participatingMembers,
                searchedMembers,
                selectedMembers,
            ) { participating, searched, selected ->
                searched.toUiModel()
                    .changeStates(selected, InviteState.SELECTED)
                    .changeStates(participating.toMembers(), InviteState.PARTICIPATING)
            }

        fun inviteMemberBy(ids: List<Long>) {
            if (selectedMembers.value.members.isEmpty()) return

            val categoryId = _category.value.id
            if (categoryId == DEFAULT_CATEGORY_ID) return

            viewModelScope.launch {
                invitationRepository.invite(categoryId, ids)
                    .onSuccess {
                        updateToInviteSuccessEvent(count = ids.size)
                        toggleInviteMode(false)
                    }.onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
                    .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
            }
        }

        fun unselect(member: Member) {
            viewModelScope.launch {
                _selectedMembers.emit(selectedMembers.value.filter(member))
            }
        }

        fun select(member: Member) {
            viewModelScope.launch {
                _selectedMembers.emit(selectedMembers.value.addFirst(member))
            }
        }

        fun searchMembersBy(keyword: String) {
            viewModelScope.launch {
                memberRepository.searchMembersBy(keyword).collect { result ->
                    result
                        .onSuccess {
                            searchedMembers.emit(it)
                        }
                        .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
                        .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
                }
            }
        }

        fun updateCategoryId(id: Long) {
            if (categoryId != id) {
                categoryId = id
                loadCategory(id)
            }
        }

        fun loadCategory(id: Long) {
            if (id <= DEFAULT_CATEGORY_ID) {
                emitMessageEvent(MessageEvent.ResId(ExceptionType.UNKNOWN.toMessageId()))
            } else {
                viewModelScope.launch {
                    categoryRepository.getCategory(id)
                        .onSuccess { updateCategory(it) }
                        .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
                        .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
                }
            }
        }

        fun deleteCategory() {
            viewModelScope.launch {
                val id = _category.value.id
                if (id == DEFAULT_CATEGORY_ID) {
                    updateToDeletedEvent(false)
                    return@launch
                }

                val result: ApiResult<Unit> = categoryRepository.deleteCategory(id)
                result.onSuccess { updateToDeletedEvent(true) }
                    .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
                    .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
            }
        }

        fun toggleInviteMode(isInviteMode: Boolean) {
            _isInviteMode.value = isInviteMode && category.value?.myRole == Role.HOST
            if (!isInviteMode) clearAllMembers()
        }

        fun clearSearchMembers() {
            viewModelScope.launch {
                searchedMembers.emit(emptyMembers)
            }
        }

        fun showLeaveDialog() {
            _dialogState.value = Exit(onConfirm = ::leaveCategory)
        }

        fun dismissDialog() {
            _dialogState.value = None
        }

        private fun leaveCategory() {
            viewModelScope.launch {
                val id = _category.value?.id
                if (id == null) {
                    updateToErrorEvent()
                    return@launch
                }
                categoryRepository.leaveCategory(id)
                    .onSuccess { updateToExitEvent() }
                    .onServerError { emitMessageEvent(MessageEvent.from(message = it)) }
                    .onException { emitMessageEvent(MessageEvent.from(exceptionType = it)) }
            }
            dismissDialog()
        }

        private fun clearAllMembers() {
            clearSearchMembers()
            clearSelectedMembers()
        }

        private fun clearSelectedMembers() {
            viewModelScope.launch {
                _selectedMembers.emit(emptyMembers)
            }
        }

        private suspend fun updateCategory(category: Category) {
            _category.value = category.toUiModel()
            participatingMembers.emit(Participants(category.participants))
        }

        private suspend fun updateToDeletedEvent(success: Boolean) {
            _categoryEvent.emit(Deleted(success = success))
        }

        private suspend fun updateToExitEvent() {
            _categoryEvent.emit(Exited)
        }

        private suspend fun updateToErrorEvent() {
            _categoryEvent.emit(Error)
        }

        private suspend fun updateToInviteSuccessEvent(count: Int) {
            _categoryEvent.emit(CategoryEvent.InviteSuccess(count))
        }

        private fun emitMessageEvent(messageEvent: MessageEvent) {
            _messageEvent.setValue(messageEvent)
        }
    }
