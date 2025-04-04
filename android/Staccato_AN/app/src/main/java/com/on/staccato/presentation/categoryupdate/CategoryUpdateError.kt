package com.on.staccato.presentation.categoryupdate

import android.net.Uri
import com.on.staccato.presentation.common.photo.FileUiModel

sealed interface CategoryUpdateError {
    data class CategoryInitialization(val message: String) : CategoryUpdateError

    data class Thumbnail(val message: String, val uri: Uri, val file: FileUiModel) : CategoryUpdateError

    data class CategoryUpdate(val message: String) : CategoryUpdateError
}
