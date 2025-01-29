package com.on.staccato.presentation.memoryupdate

import android.net.Uri

sealed interface CategoryUpdateError {
    data class CategoryInitialization(val message: String) : CategoryUpdateError

    data class Thumbnail(val message: String, val uri: Uri) : CategoryUpdateError

    data class CategoryUpdate(val message: String) : CategoryUpdateError
}
