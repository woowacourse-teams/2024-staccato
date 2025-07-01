package com.on.staccato.presentation.categoryupdate

import android.net.Uri
import androidx.annotation.StringRes
import com.on.staccato.domain.UploadFile

sealed interface CategoryUpdateError {
    val messageId: Int

    data class CategoryInitialization(
        @StringRes override val messageId: Int,
    ) : CategoryUpdateError

    data class Thumbnail(
        @StringRes override val messageId: Int,
        val uri: Uri,
        val file: UploadFile,
    ) : CategoryUpdateError

    data class CategoryUpdate(
        @StringRes override val messageId: Int,
    ) : CategoryUpdateError
}
