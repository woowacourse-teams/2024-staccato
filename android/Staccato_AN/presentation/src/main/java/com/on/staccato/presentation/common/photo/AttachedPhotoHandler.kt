package com.on.staccato.presentation.common.photo

interface AttachedPhotoHandler {
    fun onDeleteClicked(deletedPhoto: AttachedPhotoUiModel)

    fun onAddClicked()

    fun onRetryClicked(retryPhoto: AttachedPhotoUiModel)
}
