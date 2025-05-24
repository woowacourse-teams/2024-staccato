package com.on.staccato.presentation.common.photo

sealed interface AttachedPhotoState {
    data object Loading : AttachedPhotoState

    data object Success : AttachedPhotoState

    data object Fail : AttachedPhotoState

    data object Retry : AttachedPhotoState
}
