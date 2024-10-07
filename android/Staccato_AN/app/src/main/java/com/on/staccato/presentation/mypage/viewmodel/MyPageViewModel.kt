package com.on.staccato.presentation.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.ApiResponseHandler.onException
import com.on.staccato.data.ApiResponseHandler.onServerError
import com.on.staccato.data.ApiResponseHandler.onSuccess
import com.on.staccato.data.dto.Status
import com.on.staccato.domain.model.AccountInformation
import com.on.staccato.domain.repository.MyPageRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.mapper.toUiModel
import com.on.staccato.presentation.mypage.MyPageHandler
import com.on.staccato.presentation.mypage.model.AccountInformationUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
    @Inject
    constructor(private val repository: MyPageRepository) :
    ViewModel(),
        MyPageHandler {
        private val _accountInformation = MutableLiveData<AccountInformationUiModel>()
        val accountInformation: LiveData<AccountInformationUiModel>
            get() = _accountInformation

        private val _uuidCode = MutableSingleLiveData<String>()
        val uuidCode: SingleLiveData<String>
            get() = _uuidCode

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String>
            get() = _errorMessage

        fun fetchMyProfile() {
            viewModelScope.launch {
                val result = repository.getAccountInformation()
                result.onException(::handleException)
                    .onServerError(::handleError)
                    .onSuccess(::setMyProfile)
            }
        }

        private fun setMyProfile(accountInformation: AccountInformation) {
            _accountInformation.value = accountInformation.toUiModel()
        }

        override fun onCodeCopyClicked() {
            accountInformation.value?.let { _uuidCode.setValue(it.uuidCode) }
        }

        private fun handleError(
            status: Status,
            errorMessage: String,
        ) {
            _errorMessage.postValue(errorMessage)
            when (status) {
                is Status.Message ->
                    Log.e(
                        this::class.java.simpleName,
                        "Error Occurred | status: ${status.message}, message: $errorMessage",
                    )

                is Status.Code ->
                    Log.e(
                        this::class.java.simpleName,
                        "Error Occurred | status: ${status.code}, message: $errorMessage",
                    )
            }
        }

        private fun handleException(
            e: Throwable,
            errorMessage: String,
        ) {
            _errorMessage.postValue(errorMessage)
            Log.e(
                this::class.java.simpleName,
                "Exception Caught | throwable: $e, message: $errorMessage",
            )
        }
    }
