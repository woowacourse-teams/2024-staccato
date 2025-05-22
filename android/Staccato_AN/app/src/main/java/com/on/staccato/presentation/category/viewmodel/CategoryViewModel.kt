package com.on.staccato.presentation.category.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.onException2
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.Participants
import com.on.staccato.domain.model.Role
import com.on.staccato.domain.model.emptyMembers
import com.on.staccato.domain.model.emptyParticipants
import com.on.staccato.domain.model.toMembers
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.domain.repository.InvitationRepository
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.presentation.category.invite.model.InviteState
import com.on.staccato.presentation.category.invite.model.toUiModel
import com.on.staccato.presentation.category.model.CategoryUiModel
import com.on.staccato.presentation.category.model.CategoryUiModel.Companion.DEFAULT_CATEGORY_ID
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toUiModel
import com.on.staccato.presentation.util.ExceptionState2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        private val _category = MutableLiveData<CategoryUiModel>()
        val category: LiveData<CategoryUiModel> get() = _category

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _exceptionState = MutableSingleLiveData<ExceptionState2>()
        val exceptionState: SingleLiveData<ExceptionState2> get() = _exceptionState

        private val _isDeleted = MutableSingleLiveData<Boolean>()
        val isDeleted: SingleLiveData<Boolean> get() = _isDeleted

        private var _isInviteMode = MutableStateFlow(false)
        val isInviteMode: StateFlow<Boolean> get() = _isInviteMode

        var participatingMembers = MutableStateFlow(emptyParticipants)

        private var searchedMembers = MutableStateFlow(emptyMembers)

        private var _selectedMembers = MutableStateFlow(emptyMembers)
        val selectedMembers = _selectedMembers.asStateFlow()

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
            category.value?.id?.let { categoryId ->
                viewModelScope.launch {
                    invitationRepository.invite(categoryId, ids).onSuccess {
                        _errorMessage.setValue("${ids.size}명을 초대했어요!") // stringRes로 빼기
                        toggleInviteMode(false)
                    }.onServerError(::handleServerError)
                        .onException2(::handelException)
                }
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
                        .onServerError(::handleServerError)
                        .onException2(::handelException)
                        .onSuccess {
                            searchedMembers.emit(it)
                        }
                }
            }
        }

        fun loadCategory(id: Long) {
            if (id <= DEFAULT_CATEGORY_ID) {
                handelException(ExceptionState2.UnknownError)
            } else {
                viewModelScope.launch {
                    categoryRepository.getCategory(id)
                        .onSuccess { updateCategory(it) }
                        .onServerError(::handleServerError)
                        .onException2(::handelException)
                }
            }
        }

        fun deleteCategory() {
            viewModelScope.launch {
                val id = _category.value?.id
                if (id == null) {
                    _isDeleted.setValue(false)
                    return@launch
                }

                val result: ApiResult<Unit> = categoryRepository.deleteCategory(id)
                result.onSuccess { updateIsDeleteSuccess() }
                    .onServerError(::handleServerError)
                    .onException2(::handelException)
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
            participatingMembers.emit(Participants(category.mates))
        }

        private fun updateIsDeleteSuccess() {
            _isDeleted.setValue(true)
        }

        private fun handleServerError(message: String) {
            _errorMessage.setValue(message)
        }

        private fun handelException(state: ExceptionState2) {
            _exceptionState.setValue(state)
        }
    }
