package com.on.staccato.presentation.category.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onServerError
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.Status
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.repository.CategoryRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toUiModel
import com.on.staccato.presentation.category.model.CategoryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryViewModel
    @Inject
    constructor(
        private val categoryRepository: CategoryRepository,
    ) : ViewModel() {
        private val _category = MutableLiveData<CategoryUiModel>()
        val category: LiveData<CategoryUiModel> get() = _category

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _exceptionMessage = MutableSingleLiveData<String>()
        val exceptionMessage: SingleLiveData<String> get() = _exceptionMessage

        private val _isDeleteSuccess = MutableSingleLiveData<Boolean>(false)
        val isDeleteSuccess: SingleLiveData<Boolean> get() = _isDeleteSuccess

        fun loadCategory(categoryId: Long) {
            viewModelScope.launch {
                val result: ResponseResult<Category> = categoryRepository.getCategory(categoryId)
                result
                    .onSuccess(::setCategory)
                    .onServerError(::handleServerError)
                    .onException(::handelException)
            }
        }

        fun deleteCategory(categoryId: Long) {
            viewModelScope.launch {
                val result: ResponseResult<Unit> = categoryRepository.deleteCategory(categoryId)
                result.onSuccess { updateIsDeleteSuccess() }
                    .onServerError(::handleServerError)
                    .onException(::handelException)
            }
        }

        private fun setCategory(category: Category) {
            _category.value = category.toUiModel()
        }

        private fun updateIsDeleteSuccess() {
            _isDeleteSuccess.setValue(true)
        }

        private fun handleServerError(
            status: Status,
            message: String,
        ) {
            _errorMessage.postValue(message)
        }

        private fun handelException(
            e: Throwable,
            message: String,
        ) {
            _exceptionMessage.postValue(message)
        }
    }
