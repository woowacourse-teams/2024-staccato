package com.on.staccato.presentation.common.photo

sealed interface AttachedPhotoState {
    data object Loading : AttachedPhotoState

    data object Success : AttachedPhotoState

    data object Failure : AttachedPhotoState

    data object Retry : AttachedPhotoState
}
