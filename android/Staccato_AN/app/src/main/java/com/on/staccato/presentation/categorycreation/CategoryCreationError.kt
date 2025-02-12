package com.on.staccato.presentation.categorycreation

import android.net.Uri

sealed interface CategoryCreationError {
    data class Thumbnail(val message: String, val uri: Uri) : CategoryCreationError

    data class CategoryCreation(val message: String) : CategoryCreationError
}
