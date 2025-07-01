package com.on.staccato.presentation.common.photo

sealed interface PhotoUploadState {
    data object Loading : PhotoUploadState

    data object Success : PhotoUploadState

    data object Failure : PhotoUploadState

    data object Retry : PhotoUploadState
}
