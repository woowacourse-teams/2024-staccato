package com.on.staccato.presentation.categorycreation.model

import android.net.Uri
import com.on.staccato.domain.UploadFile

sealed interface CategoryCreationError {
    val state: Int

    data class Thumbnail(
        override val state: Int,
        val uri: Uri,
        val file: UploadFile,
    ) : CategoryCreationError

    data class CategoryCreation(
        override val state: Int,
    ) : CategoryCreationError
}
