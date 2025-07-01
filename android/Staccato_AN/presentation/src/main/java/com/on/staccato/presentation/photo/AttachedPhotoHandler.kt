package com.on.staccato.presentation.photo

interface AttachedPhotoHandler {
    fun onDeleteClicked(deletedPhoto: PhotoUiModel)

    fun onAddClicked()

    fun onRetryClicked(retryPhoto: PhotoUiModel)
}
