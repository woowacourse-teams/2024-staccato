package com.on.staccato.presentation.category.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResult
import com.on.staccato.data.onException2
import com.on.staccato.data.onServerError
import com.on.staccato.data.onSuccess
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.presentation.category.model.CategoryUiModel
import com.on.staccato.presentation.category.model.CategoryUiModel.Companion.DEFAULT_CATEGORY_ID
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toUiModel
import com.on.staccato.presentation.util.ExceptionState2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel
    @Inject
    constructor(
        private val categoryRepository: CategoryRepository,
    ) : ViewModel() {
        private val _category = MutableLiveData<CategoryUiModel>()
        val category: LiveData<CategoryUiModel> get() = _category

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _exceptionState = MutableSingleLiveData<ExceptionState2>()
        val exceptionState: SingleLiveData<ExceptionState2> get() = _exceptionState

        private val _isDeleteSuccess = MutableSingleLiveData<Boolean>(false)
        val isDeleteSuccess: SingleLiveData<Boolean> get() = _isDeleteSuccess

        fun loadCategory(id: Long) {
            if (id <= DEFAULT_CATEGORY_ID) {
                handelException(ExceptionState2.UnknownError)
            } else {
                viewModelScope.launch {
                    val result: ApiResult<Category> = categoryRepository.getCategory(id)
                    result
                        .onSuccess(::updateCategory)
                        .onServerError(::handleServerError)
                        .onException2(::handelException)
                }
            }
        }

        fun deleteCategory() {
            viewModelScope.launch {
                val id = _category.value?.id ?: return@launch
                val result: ApiResult<Unit> = categoryRepository.deleteCategory(id)
                result.onSuccess { updateIsDeleteSuccess() }
                    .onServerError(::handleServerError)
                    .onException2(::handelException)
            }
        }

        private fun updateCategory(category: Category) {
            _category.value = category.toUiModel()
        }

        private fun updateIsDeleteSuccess() {
            _isDeleteSuccess.setValue(true)
        }

        private fun handleServerError(message: String) {
            _errorMessage.setValue(message)
        }

        private fun handelException(state: ExceptionState2) {
            _exceptionState.setValue(state)
        }
    }
