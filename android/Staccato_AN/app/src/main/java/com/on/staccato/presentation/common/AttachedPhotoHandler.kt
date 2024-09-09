package com.on.staccato.presentation.common

import com.on.staccato.presentation.momentcreation.model.AttachedPhotoUiModel

interface AttachedPhotoHandler {
    fun onDeleteClicked(deletedPhoto: AttachedPhotoUiModel)

    fun onAddClicked()
}
