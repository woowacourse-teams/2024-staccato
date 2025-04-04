package com.on.staccato.presentation.categorycreation

import android.net.Uri
import com.on.staccato.presentation.util.ExceptionState2

sealed interface CategoryCreationError {
    val state: ExceptionState2

    data class Thumbnail(
        override val state: ExceptionState2,
        val uri: Uri,
    ) : CategoryCreationError

    data class CategoryCreation(
        override val state: ExceptionState2,
    ) : CategoryCreationError
}
