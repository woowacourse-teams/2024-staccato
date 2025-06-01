package com.on.staccato.presentation.categorycreation.model

import android.net.Uri
import com.on.staccato.presentation.common.photo.UploadFile
import com.on.staccato.presentation.util.ExceptionState2

sealed interface CategoryCreationError {
    val state: ExceptionState2

    data class Thumbnail(
        override val state: ExceptionState2,
        val uri: Uri,
        val file: UploadFile,
    ) : CategoryCreationError

    data class CategoryCreation(
        override val state: ExceptionState2,
    ) : CategoryCreationError
}
