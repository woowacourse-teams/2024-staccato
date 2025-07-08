package com.on.staccato.presentation.categorycreation.model

import android.net.Uri
import androidx.annotation.StringRes
import com.on.staccato.domain.UploadFile

sealed interface CategoryCreationError {
    val messageId: Int

    data class Thumbnail(
        @StringRes
        override val messageId: Int,
        val uri: Uri,
        val file: UploadFile,
    ) : CategoryCreationError

    data class CategoryCreation(
        @StringRes
        override val messageId: Int,
    ) : CategoryCreationError
}
