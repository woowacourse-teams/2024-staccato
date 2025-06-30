package com.on.staccato.presentation.categoryupdate

import android.net.Uri
import com.on.staccato.domain.UploadFile

sealed interface CategoryUpdateError {
    val messageId: Int

    data class CategoryInitialization(override val messageId: Int) : CategoryUpdateError

    data class Thumbnail(override val messageId: Int, val uri: Uri, val file: UploadFile) : CategoryUpdateError

    data class CategoryUpdate(override val messageId: Int) : CategoryUpdateError
}
