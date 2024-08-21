package com.woowacourse.staccato.presentation.common

import com.woowacourse.staccato.presentation.momentcreation.model.AttachedPhotoUiModel

interface AttachedPhotoHandler {
    fun onDeleteClicked(deletedPhoto: AttachedPhotoUiModel)

    fun onAddClicked()
}
