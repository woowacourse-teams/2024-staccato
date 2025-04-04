package com.on.staccato.presentation.categoryupdate

import android.net.Uri
import com.on.staccato.presentation.common.photo.FileUiModel
import com.on.staccato.presentation.util.ExceptionState2

sealed interface CategoryUpdateError {
    data class CategoryInitialization(val state: ExceptionState2) : CategoryUpdateError

    data class Thumbnail(val state: ExceptionState2, val uri: Uri, val file: FileUiModel) : CategoryUpdateError

    data class CategoryUpdate(val state: ExceptionState2) : CategoryUpdateError
}
